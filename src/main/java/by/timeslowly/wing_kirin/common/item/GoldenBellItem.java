package by.timeslowly.wing_kirin.common.item;

import by.dragonsurvivalteam.dragonsurvival.registry.DSAttributes;
import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.registry.WKAttributes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class GoldenBellItem extends Item {
    public GoldenBellItem(Identifier id) {
        // 26.1 起 Item.Properties 必须携带注册 id
        super(new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .rarity(Rarity.COMMON)
                // 设置耐久
                .durability(580)
                .attributes(
                // 属性修饰
                ItemAttributeModifiers.builder()
                        // 攻击伤害
                        .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID,
                                        4, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND)
                        // 攻击速度
                        .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID,
                                        -2.4, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND)
                        // 音爆伤害倍率
                        .add(WKAttributes.SONIC_BOOM_DAMAGE_MULTIPLIER, new AttributeModifier(
                                Identifier.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.golden_bell_1"),
                                        1.0, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                                EquipmentSlotGroup.MAINHAND)
                        // 重锤猛击伤害倍率（合理的，因为其重）
                        .add(WKAttributes.MACE_SMASH_DAMAGE_MULTIPLIER, new AttributeModifier(
                                        Identifier.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.golden_bell_2"),
                                        0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                                EquipmentSlotGroup.OFFHAND)
                        // 龙飞行速度
                        .add(DSAttributes.FLIGHT_SPEED, new AttributeModifier(
                                Identifier.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.golden_bell_3"),
                                        -0.9, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                                EquipmentSlotGroup.MAINHAND)
                        // 移动速度
                        .add(Attributes.MOVEMENT_SPEED, new AttributeModifier(
                                Identifier.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.golden_bell_4"),
                                        -0.6, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                                EquipmentSlotGroup.MAINHAND)
                        // 重力
                        .add(Attributes.GRAVITY, new AttributeModifier(
                                Identifier.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.golden_bell_5"),
                                        0.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                                EquipmentSlotGroup.MAINHAND)
                        .build()
                )
        );
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack itemstack, @NotNull BlockState state) {
        return 0.5f;
    }

    @Override
    public void hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        // 26.1 起 Item.hurtEnemy 返回类型由 boolean 改为 void
    }

    // 添加耐久损耗逻辑 - 直接攻击
    @Override
    public void postHurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        // 直接攻击损耗1点耐久（26.1 起 LivingEntity.getSlotForHand 被移除，手动映射）
        EquipmentSlot handSlot = attacker.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        stack.hurtAndBreak(1, attacker, handSlot);
    }

    // 添加描述
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull TooltipDisplay tooltipDisplay, @NotNull Consumer<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipComponents, tooltipFlag);
        tooltipComponents.accept(Component.translatable("item.wing_kirin.golden_bell.description_0"));
        tooltipComponents.accept(Component.translatable("item.wing_kirin.golden_bell.description_1"));
    }

    // 静态方法：原双倍耐久损耗方法简化至WKEventHandler中

}
