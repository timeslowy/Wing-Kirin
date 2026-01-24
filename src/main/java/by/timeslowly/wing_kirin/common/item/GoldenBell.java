package by.timeslowly.wing_kirin.common.item;

import by.dragonsurvivalteam.dragonsurvival.registry.DSAttributes;
import by.timeslowly.wing_kirin.Wing_kirin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GoldenBell extends Item {
    public GoldenBell() {
        super(new Item.Properties()
                // 设置堆叠
                .stacksTo(1)
                // 设置耐久
                .durability(580)
                .attributes(
                // 属性修饰
                ItemAttributeModifiers.builder()
                        // 攻击伤害
                        .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 4, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND)
                        // 攻击速度
                        .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.4, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND)
                        // 龙飞行速度
                        .add(DSAttributes.FLIGHT_SPEED, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.golden_bell_2"),-0.9, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                                EquipmentSlotGroup.MAINHAND)
                        // 移动速度
                        .add(Attributes.MOVEMENT_SPEED, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.golden_bell_3"),-0.6, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                                EquipmentSlotGroup.MAINHAND)
                        // 重力
                        .add(Attributes.GRAVITY, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.golden_bell_4"),0.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                                EquipmentSlotGroup.MAINHAND)
                        .build()
                )
        );
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack itemstack, @NotNull BlockState state) {
        return 0.5f;
    }

    // 添加耐久损耗逻辑 - 直接攻击
    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        // 直接攻击损耗1点耐久
        stack.hurtAndBreak(1, attacker, LivingEntity.getSlotForHand(attacker.getUsedItemHand()));
        return true;
    }

    // 添加描述
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("item.wing_kirin.golden_bell.description_0"));
        tooltipComponents.add(Component.translatable("item.wing_kirin.golden_bell.description_1"));
    }

    // 静态方法：当玩家造成音波伤害（龙吼功）时调用此方法，双倍耐久损耗
    public static void onSonicBoomDamage(LivingEntity attacker) {
        // 检查攻击者是否持有GoldenBell
        if (attacker != null) {
            ItemStack mainHand = attacker.getMainHandItem();
            // 检查主手
            if (!mainHand.isEmpty() && mainHand.getItem() instanceof GoldenBell) {
                mainHand.hurtAndBreak(2, attacker, LivingEntity.getSlotForHand(attacker.getUsedItemHand()));
            }
        }
    }
}
