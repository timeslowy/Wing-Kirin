## 通过龙爪剑槽武器的锋利等级计算伤害加成
# 执行者: player
# 输入: wing_kirin:ram math.result
# 结果: wing_kirin:ram math.result


# 如果龙玩家的剑槽中不存在锋利附魔的武器则直接结束
execute unless data entity @s \
    "neoforge:attachments"."dragonsurvival:claw_inventory_data".SWORD.components.minecraft:enchantments.levels."minecraft:sharpness" \
    run return fail


# 传入值 damage, sharpness_level(锋利等级)

# 结果
# = damage + 0.5 * sharpness_level + 0.5
# 考虑到计分板无法储存小数位故所有数值需 * 10
# 公式转化为
# = (( sharpness_level * 5 + 5) + damage * 10) / 10

# 计算 sharpness_level * 10 + 5
execute store result score result wk.math run data get entity @s \
    "neoforge:attachments"."dragonsurvival:claw_inventory_data".SWORD.components.minecraft:enchantments.levels."minecraft:sharpness" 10
scoreboard players add result wk.math 5

# 运算 [( sharpness_level * 5 + 5) / 10] + damage * 10
execute store result score val_0 wk.math run data get storage wing_kirin:ram math.result 10
scoreboard players operation result wk.math += val_0 wk.math

# 将运算结果储存到命令存储
execute store result storage wing_kirin:ram math.result float 0.1 run scoreboard players get result wk.math
