package by.timeslowly.wing_kirin.common.item;

import by.dragonsurvivalteam.dragonsurvival.registry.DSAttributes;
import by.timeslowly.wing_kirin.registry.WKAttributes;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class GoldenBellItem extends Item {
    // 1.20.1 的 AttributeModifier 以 UUID 标识（1.21 起才改为 ResourceLocation），
    // 此处按 1.21.1 分支的修饰符 ID 字符串确定性生成 UUID，保证两端可一一对应
    private static final UUID SONIC_BOOM_MODIFIER_ID = uuidOf("wing_kirin:effect.golden_bell_1");
    private static final UUID FLIGHT_SPEED_MODIFIER_ID = uuidOf("wing_kirin:effect.golden_bell_3");
    private static final UUID MOVEMENT_SPEED_MODIFIER_ID = uuidOf("wing_kirin:effect.golden_bell_4");
    private static final UUID GRAVITY_MODIFIER_ID = uuidOf("wing_kirin:effect.golden_bell_5");

    // 主手属性修饰符表。Forge 1.20.1 的自定义属性（forge:attribute）注册事件在物品之后触发，
    // 不能在物品构造器中解析 RegistryObject，故首次取用时惰性构建
    private static volatile Multimap<Attribute, AttributeModifier> mainhandModifiers;

    public GoldenBellItem() {
        super(new Item.Properties()
                .rarity(Rarity.COMMON)
                // 设置耐久
                .durability(580)
        );
    }

    private static UUID uuidOf(String modifierId) {
        return UUID.nameUUIDFromBytes(modifierId.getBytes(StandardCharsets.UTF_8));
    }

    // 1.20.1 无 ItemAttributeModifiers 组件（1.20.5+ 新增），改写为 Multimap + getDefaultAttributeModifiers
    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlot equipmentSlot) {
        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            Multimap<Attribute, AttributeModifier> modifiers = mainhandModifiers;
            if (modifiers == null) {
                modifiers = buildMainhandModifiers();
            }
            return modifiers;
        }
        return super.getDefaultAttributeModifiers(equipmentSlot);
    }

    private static synchronized Multimap<Attribute, AttributeModifier> buildMainhandModifiers() {
        if (mainhandModifiers != null) {
            return mainhandModifiers;
        }
        return mainhandModifiers = ImmutableMultimap.<Attribute, AttributeModifier>builder()
                // 攻击伤害
                .put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID,
                        "Weapon modifier", 4.0, AttributeModifier.Operation.ADDITION))
                // 攻击速度
                .put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID,
                        "Weapon modifier", -2.4, AttributeModifier.Operation.ADDITION))
                // 音爆伤害倍率
                .put(WKAttributes.SONIC_BOOM_DAMAGE_MULTIPLIER.get(), new AttributeModifier(SONIC_BOOM_MODIFIER_ID,
                        "Golden bell sonic boom multiplier", 1.0, AttributeModifier.Operation.MULTIPLY_BASE))
                // TODO:重锤猛击伤害倍率（合理的，因为其重；1.21.1 中为副手 +0.5 ADD_MULTIPLIED_BASE）未随本次移植：
                //  1.20.1 原版没有重锤，MACE_SMASH_DAMAGE_MULTIPLIER 属性已按用户要求移除
                // 龙飞行速度（DragonSurvival 属性）
                .put(DSAttributes.FLIGHT_SPEED.get(), new AttributeModifier(FLIGHT_SPEED_MODIFIER_ID,
                        "Golden bell flight speed", -0.9, AttributeModifier.Operation.MULTIPLY_TOTAL))
                // 移动速度
                .put(Attributes.MOVEMENT_SPEED, new AttributeModifier(MOVEMENT_SPEED_MODIFIER_ID,
                        "Golden bell movement speed", -0.6, AttributeModifier.Operation.MULTIPLY_TOTAL))
                // 重力（1.20.1 原版无 Attributes.GRAVITY（1.20.5+ 才有），改用 Forge 的实体重力属性 forge:entity_gravity）
                .put(ForgeMod.ENTITY_GRAVITY.get(), new AttributeModifier(GRAVITY_MODIFIER_ID,
                        "Golden bell gravity", 0.4, AttributeModifier.Operation.MULTIPLY_TOTAL))
                .build();
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack itemstack, @NotNull BlockState state) {
        return 0.5f;
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        // 1.20.1 无 Item#postHurtEnemy（1.20.5+ 新增），直接攻击损耗 1 点耐久的逻辑合并进 hurtEnemy（同原版 SwordItem 做法；
        // getSlotForHand 亦为 1.20.5+ API，近战攻击固定主手）
        stack.hurtAndBreak(1, attacker, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }

    // 添加描述
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("item.wing_kirin.golden_bell.description_0"));
        tooltipComponents.add(Component.translatable("item.wing_kirin.golden_bell.description_1"));
    }

    // 静态方法：原双倍耐久损耗方法简化至WKEventHandler中

}
