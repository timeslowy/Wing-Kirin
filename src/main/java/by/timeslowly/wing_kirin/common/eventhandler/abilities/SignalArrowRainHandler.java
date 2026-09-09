package by.timeslowly.wing_kirin.common.eventhandler.abilities;

import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ClawToolHandler;
import by.timeslowly.wing_kirin.WingKirin;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.mojang.logging.LogUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

/**
 * 一支穿云箭（signal_arrow）箭雨循环与伤害结算（服务端，自 1.21.1 函数链按方案C由 Java 接管）。
 * <p>
 * 分界线 = 锚点 marker。DS 数据包原生管线全部保留：
 * <ul>
 *   <li>技能 JSON 的 projectile 效果生成穿云箭球（generic_ball_entity）→ on_destroy run_function
 *       （执行者=弹射物、爆点坐标）→ main-projectile → 生成 marker（data.Owner / data.projectile_level /
 *       data.radius / data.life / data.tick_spawn_count 参数化，{@code Marker} 原样保留 data 复合标签）；</li>
 *   <li>充能/成功范围显示、音效等纯数据包函数照常执行。</li>
 * </ul>
 * 本类接管 marker 之后的部分（1.21.1 中为宏函数链 + random 指令 + UUID 实体参数，1.20.1 均不可用）：
 * <ul>
 *   <li>发现 marker：{@link EntityJoinLevelEvent} 收集带 signal_arrow_generic 标签的 marker，
 *       ServerTickEvent(END) 阶段延迟注册——marker 的 data 参数由 summon 之后的 set-marker 函数写入，
 *       join 时刻尚未就绪；注册成功后按 marker UUID 去重（世界重载后 marker 重入时自动重新注册）；</li>
 *   <li>伤害计算：爆点时刻按发射者当前状态一口价结算（与 1.21.1 caculate_damage 语义一致）——
 *       基础 15×技能等级 + 力量效果（+15×(amplifier+1)）+ 爪剑锋利（+等级+0.5）；
 *       密度附魔加成舍弃（1.20.1 无重锤/致密），浩然正气加成待浩然正气到期行为移植后补；</li>
 *   <li>箭雨循环：持续 life 刻，每刻生成 tick_spawn_count 支梨花针（generic_arrow_entity + 内联
 *       general_data NBT，与 1.21.1 spawn.mcfunction 完全一致），随机散点 X/Z ∈ [-radius, radius]
 *       （整数均匀，等价 random value）、抬升爆点+64 刻度、Motion -10 落下；</li>
 *   <li>箭矢 NBT 注入 Owner（击杀归因进度的前提）与 has_uuid 免伤条件（不伤发射者），
 *       伤害值写入 general_data.common_hit_effects 的 amount。</li>
 * </ul>
 * 性能考量：
 * <ul>
 *   <li>无实例时每刻开销仅为 pending 队列判空，不做世界扫描；</li>
 *   <li>NBT 模板按场（marker）构建一次，伤害与发射者 UUID 预先烘焙，每支箭仅 copy + 写 Pos，
 *       避免 per-needle 编解码开销；</li>
 *   <li>箭矢 life 设为 1180（原版落地寿命上限 1200），落地约 1 秒后自动消散，防止实体堆积；</li>
 *   <li>中心粒子每刻一次 sendParticles 群发。</li>
 * </ul>
 */
@Mod.EventBusSubscriber(modid = WingKirin.MODID)
public class SignalArrowRainHandler {
    private static final org.slf4j.Logger LOGGER = LogUtils.getLogger();

    /** 标志实体标签（spawn.mcfunction 写入，与 1.21.1 一致） */
    private static final String MARKER_TAG = "signal_arrow_generic";
    /** 梨花针实体类型（DS 原版箭形弹射物） */
    private static final String NEEDLE_TYPE = "dragonsurvival:generic_arrow_entity";
    /** 梨花针标签（与 1.21.1 spawn.mcfunction 一致） */
    private static final String NEEDLE_TAG = "signal_arrow";
    /** 梨花针贴图（wing_kirin:ding_arrow，资源已随本批次复制） */
    private static final String NEEDLE_TEXTURE = "wing_kirin:ding_arrow";
    /** 伤害类型（数据包注册表条目，随 on_destroy/箭雨命中管线经 DS 解析） */
    private static final String DAMAGE_TYPE = "wing_kirin:rainstorm_arrow";
    /** 命中判定范围（与 pear_blossom_needles.json / spawn.mcfunction 内联 NBT 一致） */
    private static final float HIT_RADIUS = 2.0F;

    /** marker UUID -> 箭雨实例 */
    private static final Map<UUID, Rain> RAINS = new HashMap<>();
    /** 已发现但参数尚未就绪的 marker（summon 后 set-marker 同刻补写 data，下一刻 END 阶段注册） */
    private static final Set<Marker> PENDING = new HashSet<>();

    /** 单场箭雨 */
    private static final class Rain {
        final ServerLevel level;
        final Marker marker;
        final Vec3 center;
        final float damage;
        final int radius;
        final int perTick;
        /** 预烘焙伤害与发射者 UUID 的箭矢 NBT 模板（每场一份） */
        final CompoundTag needleTemplate;
        int ticksLeft;

        Rain(@NotNull ServerLevel level, @NotNull Marker marker, @NotNull Vec3 center,
             float damage, int radius, int perTick, int ticksLeft, @NotNull CompoundTag needleTemplate) {
            this.level = level;
            this.marker = marker;
            this.center = center;
            this.damage = damage;
            this.radius = radius;
            this.perTick = perTick;
            this.ticksLeft = ticksLeft;
            this.needleTemplate = needleTemplate;
        }
    }

    // ---------------------------------------------------------------- 发现与注册

    @SubscribeEvent
    public static void onEntityJoin(@NotNull EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        if (event.getEntity() instanceof Marker marker && marker.getTags().contains(MARKER_TAG)) {
            PENDING.add(marker);
        }
    }

    @SubscribeEvent
    public static void onServerTick(@NotNull TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        resolvePending();
        if (RAINS.isEmpty()) {
            return;
        }
        tickRains();
    }

    /** 将参数就绪的 pending marker 注册为箭雨实例（未就绪者留在队列，下一刻重试） */
    private static void resolvePending() {
        if (PENDING.isEmpty()) {
            return;
        }
        Iterator<Marker> iterator = PENDING.iterator();
        while (iterator.hasNext()) {
            Marker marker = iterator.next();
            if (marker.isRemoved() || RAINS.containsKey(marker.getUUID())) {
                iterator.remove();
                continue;
            }
            tryRegister(marker);
            if (RAINS.containsKey(marker.getUUID())) {
                iterator.remove();
            }
        }
    }

    private static void tryRegister(@NotNull Marker marker) {
        if (!(marker.level() instanceof ServerLevel level)) {
            return;
        }
        // Marker 实体将自定义参数原样保存在 data 复合标签中（saveWithoutId 即可读出）
        CompoundTag root = marker.saveWithoutId(new CompoundTag());
        CompoundTag data = root.getCompound("data");
        if (!data.hasUUID("Owner") || !data.contains("projectile_level", Tag.TAG_ANY_NUMERIC)) {
            return; // set-marker 尚未完成写入，下一刻重试
        }
        int abilityLevel = data.getInt("projectile_level");
        if (abilityLevel < 1 || abilityLevel > 5) {
            return; // 防御：等级越界则不启动（与 1.21.1 按级分支等价）
        }
        int radius = data.contains("radius", Tag.TAG_ANY_NUMERIC) ? data.getInt("radius") : 10 + 2 * abilityLevel;
        int perTick = data.contains("tick_spawn_count", Tag.TAG_ANY_NUMERIC) ? data.getInt("tick_spawn_count") : 4 + 2 * abilityLevel;
        int life = data.contains("life", Tag.TAG_ANY_NUMERIC) ? data.getInt("life") : 100;

        UUID owner = data.getUUID("Owner");
        ServerPlayer ownerPlayer = level.getServer().getPlayerList().getPlayer(owner);
        float damage = computeDamage(ownerPlayer, abilityLevel);

        UUID markerId = marker.getUUID();
        RAINS.put(markerId, new Rain(level, marker, marker.position(), damage, radius, perTick, life,
                buildNeedleTemplate(owner, damage)));
    }

    /**
     * 爆点时刻一口价伤害（与 1.21.1 caculate_damage 及其加成函数语义逐项对齐）：
     * <ul>
     *   <li>基础：15×技能等级（caculate_damage 中 store scale 15）；</li>
     *   <li>力量：1.21.1 读 modifier minecraft:effect.strength（3×等级）×scale50 → ×0.1，净乘 ≡ +15×(amplifier+1)；</li>
     *   <li>锋利：爪剑锋利等级 level×10+5 → ×0.1，≡ +level+0.5（1.21.1 经 neoforge:attachments 附件路径读取，
     *       1.20.1 改用 DS ClawToolHandler.getDragonSword 直接取物品栈）；</li>
     *   <li>致密：1.20.1 无重锤与致密附魔，舍弃；</li>
     *   <li>TODO:浩然正气：待浩然正气到期行为（Amnesia/GreatZhengqi 批次后续）移植后补。</li>
     * </ul>
     */
    private static float computeDamage(@Nullable ServerPlayer owner, int abilityLevel) {
        float damage = 15.0F * abilityLevel;
        if (owner != null) {
            MobEffectInstance strength = owner.getEffect(MobEffects.DAMAGE_BOOST);
            if (strength != null) {
                damage += 15.0F * (strength.getAmplifier() + 1);
            }
            ItemStack sword = ClawToolHandler.getDragonSword(owner);
            if (sword != null && !sword.isEmpty()) {
                int sharpness = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SHARPNESS, sword);
                if (sharpness > 0) {
                    damage += sharpness + 0.5F;
                }
            }
        }
        return damage;
    }

    // ---------------------------------------------------------------- 箭雨循环

    private static void tickRains() {
        Iterator<Map.Entry<UUID, Rain>> iterator = RAINS.entrySet().iterator();
        while (iterator.hasNext()) {
            Rain rain = iterator.next().getValue();
            // 标志实体被外力移除（区块卸载/命令清理）时终止该场箭雨
            if (rain.marker.isRemoved()) {
                iterator.remove();
                continue;
            }
            Vec3 center = rain.center;
            for (int i = 0; i < rain.perTick; i++) {
                spawnNeedle(rain.level, rain, center);
            }
            // 中心电火花粒子（复刻 1.21.1 signal_arrow/tick.mcfunction：electric_spark 3 0 3 0 5）
            rain.level.sendParticles(ParticleTypes.ELECTRIC_SPARK, center.x, center.y, center.z, 5, 3.0D, 0.0D, 3.0D, 0.0D);
            rain.ticksLeft--;
            if (rain.ticksLeft <= 0) {
                rain.marker.discard();
                iterator.remove();
            }
        }
    }

    private static void spawnNeedle(@NotNull ServerLevel level, @NotNull Rain rain, @NotNull Vec3 center) {
        // 随机散点：整数均匀 [-radius, radius]（等价 1.21.1 的 random value -$(radius)..$(radius)）
        double dx = level.random.nextInt(rain.radius * 2 + 1) - rain.radius;
        double dz = level.random.nextInt(rain.radius * 2 + 1) - rain.radius;
        CompoundTag nbt = rain.needleTemplate.copy();
        nbt.put("Pos", doubleList(center.x + dx, center.y + 64.0D, center.z + dz));
        Entity needle = EntityType.loadEntityRecursive(nbt, level, Function.identity());
        if (needle != null) {
            level.addFreshEntity(needle);
        }
    }

    // ---------------------------------------------------------------- 箭矢 NBT 模板

    /**
     * 构建梨花针 NBT 模板（每场一份），内容与 1.21.1 spawn.mcfunction 的 summon NBT 一致，
     * 差异仅两点：伤害 amount 换成本场计算值、has_uuid 换成本场发射者 UUID
     * （1.21.1 由 set-generic_arrow_entity 函数在运行期注入）。条件类型键统一补 minecraft: 前缀。
     */
    private static @NotNull CompoundTag buildNeedleTemplate(@NotNull UUID owner, float damage) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", NEEDLE_TYPE);
        ListTag tags = new ListTag();
        tags.add(StringTag.valueOf(NEEDLE_TAG));
        tag.put("Tags", tags);
        tag.putByte("pickup", (byte) 0);        // Pickup.DISALLOWED：不可拾取
        tag.putByte("PierceLevel", (byte) 0);
        tag.putFloat("FallDistance", 0.0F);
        tag.putShort("life", (short) 1180);     // 原版落地寿命上限 1200，留 20 刻自动消散，防实体堆积
        tag.put("Motion", doubleList(0.0D, -10.0D, 0.0D));
        // 根级 Owner：击杀归因的生命线——DS ProjectileDamageEffect 以 projectile.getOwner() 作为
        // DamageSource 的归因实体（源码 m_19749_ 实证）；缺它则击杀不归属施法者。
        // 原版 Projectile.readAdditionalSaveData 走 hasUUID("Owner")（int 数组），loadEntityRecursive 时即生效。
        tag.put("Owner", NbtUtils.createUUID(owner));
        tag.put("general_data", buildGeneralData(owner, damage));
        tag.put("type_data", buildTypeData());
        return tag;
    }

    private static @NotNull CompoundTag buildGeneralData(@NotNull UUID owner, float damage) {
        CompoundTag generalData = new CompoundTag();
        generalData.putString("name", "wing_kirin:pear_blossom_needles");

        // ticking_effects：飞行途中金粉色尘埃粒子（每刻 10 颗，复刻内联 NBT）
        generalData.put("ticking_effects", single(tickingEntry()));

        // common_hit_effects：范围 2.0 伤害，inverted has_uuid 免伤发射者
        generalData.put("common_hit_effects", single(areaDamageEntry(owner, damage)));

        // block_hit_effects：落地暴击粒子
        generalData.put("block_hit_effects", single(blockCritEntry()));

        // entity_hit_effects：空（伤害走范围结算，与 1.21.1 一致）
        generalData.put("entity_hit_effects", new ListTag());

        // entity_hit_condition：仅对 living_entity 感知命中
        generalData.put("entity_hit_condition", livingEntityCondition());
        return generalData;
    }

    /** ticking_effects 条目：point 目标的尘埃粒子世界效果（effects 列表元素须为 {effect:{...}} 包裹，缺失会被 DS 整条丢弃） */
    private static @NotNull CompoundTag tickingEntry() {
        CompoundTag entry = new CompoundTag();
        CompoundTag wrapper = new CompoundTag();
        wrapper.put("effects", single(wrapInEffect(particleWorldEffect(10, "dust", goldDust()))));
        entry.put("general_data", wrapper);
        entry.putString("target_type", "dragonsurvival:point");
        return entry;
    }

    /** 通用效果列表元素包裹键：{effect: <effect>[, condition: <condition>]} */
    private static @NotNull CompoundTag wrapInEffect(@NotNull CompoundTag effect) {
        CompoundTag tag = new CompoundTag();
        tag.put("effect", effect);
        return tag;
    }

    /** common_hit_effects 条目：area 2.0 的 rainstorm_arrow 伤害，免伤发射者 */
    private static @NotNull CompoundTag areaDamageEntry(@NotNull UUID owner, float damage) {
        CompoundTag damageEffect = new CompoundTag();
        damageEffect.putString("damage_type", DAMAGE_TYPE);
        damageEffect.put("amount", FloatTag.valueOf(damage));
        damageEffect.putString("entity_effect", "dragonsurvival:damage");

        CompoundTag effectWrapper = new CompoundTag();
        effectWrapper.put("effect", damageEffect);
        effectWrapper.put("condition", excludeOwnerCondition(owner));

        CompoundTag wrapper = new CompoundTag();
        wrapper.put("effects", single(effectWrapper));
        CompoundTag entry = new CompoundTag();
        entry.put("general_data", wrapper);
        entry.putString("target_type", "dragonsurvival:area");
        entry.putFloat("radius", HIT_RADIUS);
        return entry;
    }

    /** 免伤条件：inverted(entity_properties(custom_predicates.has_uuid = 发射者)) */
    private static @NotNull CompoundTag excludeOwnerCondition(@NotNull UUID owner) {
        CompoundTag typeSpecific = new CompoundTag();
        typeSpecific.putString("type", "dragonsurvival:custom_predicates");
        typeSpecific.put("has_uuid", NbtUtils.createUUID(owner));

        CompoundTag predicate = new CompoundTag();
        predicate.put("type_specific", typeSpecific);

        CompoundTag term = entityProperties("this", predicate);
        CompoundTag condition = new CompoundTag();
        condition.putString("condition", "minecraft:inverted");
        condition.put("term", term);
        return condition;
    }

    private static @NotNull CompoundTag entityProperties(@NotNull String entity, @NotNull CompoundTag predicate) {
        CompoundTag tag = new CompoundTag();
        tag.putString("condition", "minecraft:entity_properties");
        tag.putString("entity", entity);
        tag.put("predicate", predicate);
        return tag;
    }

    /** block_hit_effects 条目：crit 粒子 ×10 */
    private static @NotNull CompoundTag blockCritEntry() {
        CompoundTag crit = new CompoundTag();
        crit.putString("type", "minecraft:crit");
        CompoundTag particleData = new CompoundTag();
        particleData.put("particle", crit);
        particleData.put("horizontal_position", inBoundingBox());
        particleData.put("vertical_position", inBoundingBox());
        particleData.put("horizontal_velocity", baseVelocity());
        particleData.put("vertical_velocity", baseVelocity());

        CompoundTag entry = new CompoundTag();
        entry.putString("block_effect", "dragonsurvival:particle");
        entry.put("particle_data", particleData);
        entry.putInt("particle_count", 10);
        return entry;
    }

    /** entity_hit_condition：仅 living_entity 触发命中 */
    private static @NotNull CompoundTag livingEntityCondition() {
        CompoundTag typeSpecific = new CompoundTag();
        typeSpecific.putString("type", "dragonsurvival:entity_check_predicate");
        typeSpecific.putString("check_for", "living_entity");

        CompoundTag predicate = new CompoundTag();
        predicate.put("type_specific", typeSpecific);
        return entityProperties("this", predicate);
    }

    /** 金粉色尘埃粒子选项（与 pear_blossom_needles.json 一致） */
    private static @NotNull CompoundTag goldDust() {
        CompoundTag dust = new CompoundTag();
        dust.putString("type", "dust");
        dust.put("color", floatList(0.98F, 0.86F, 0.57F));
        dust.putFloat("scale", 1.0F);
        return dust;
    }

    /** world_effect: dragonsurvival:particle 包装（count + 粒子数据） */
    private static @NotNull CompoundTag particleWorldEffect(int count, @NotNull String type, @NotNull CompoundTag particleOptions) {
        CompoundTag particleData = new CompoundTag();
        particleData.put("particle", particleOptions);
        particleData.put("horizontal_position", inBoundingBox());
        particleData.put("vertical_position", inBoundingBox());
        particleData.put("horizontal_velocity", baseVelocity());
        particleData.put("vertical_velocity", baseVelocity());

        CompoundTag effect = new CompoundTag();
        effect.putString("world_effect", "dragonsurvival:particle");
        effect.putInt("particle_count", count);
        effect.put("particle_data", particleData);
        return effect;
    }

    /** type_data：箭形弹射物贴图（texture.texture_entries） */
    private static @NotNull CompoundTag buildTypeData() {
        CompoundTag textureEntry = new CompoundTag();
        textureEntry.putString("texture_resource", NEEDLE_TEXTURE);
        textureEntry.putInt("from_level", 1);

        CompoundTag texture = new CompoundTag();
        texture.put("texture_entries", single(textureEntry));

        CompoundTag typeData = new CompoundTag();
        typeData.put("texture", texture);
        return typeData;
    }

    private static @NotNull CompoundTag inBoundingBox() {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", "in_bounding_box");
        return tag;
    }

    private static @NotNull CompoundTag baseVelocity() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("base", 1.0F);
        return tag;
    }

    private static @NotNull ListTag single(@NotNull CompoundTag entry) {
        ListTag list = new ListTag();
        list.add(entry);
        return list;
    }

    private static @NotNull ListTag doubleList(double @NotNull ... values) {
        ListTag list = new ListTag();
        for (double value : values) {
            list.add(DoubleTag.valueOf(value));
        }
        return list;
    }

    private static @NotNull ListTag floatList(float @NotNull ... values) {
        ListTag list = new ListTag();
        for (float value : values) {
            list.add(FloatTag.valueOf(value));
        }
        return list;
    }

    // ---------------------------------------------------------------- 清理

    @SubscribeEvent
    public static void onServerStopped(@NotNull ServerStoppedEvent event) {
        RAINS.clear();
        PENDING.clear();
    }
}
