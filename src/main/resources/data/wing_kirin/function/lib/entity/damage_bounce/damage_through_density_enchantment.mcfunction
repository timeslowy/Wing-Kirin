## 通过主手重锤的致密等级计算伤害加成
# 执行者: player
# 输入: wing_kirin:ram math.result
# 结果: wing_kirin:ram math.result


# 如果龙玩家的主手中不存在致密附魔的武器（重锤）则直接结束
execute unless data entity @s SelectedItem.components."minecraft:enchantments".levels.minecraft:density run return fail

# 传入值 damage, density_level(致密等级)
# 结果
# = damage + 0.5 * density_level
# 考虑到计分板无法储存小数位故所有数值需 * 10
# 公式转化为
# = (( sharpness_level * 5 ) + damage * 10) / 10

# 计算 density_level * 10 
execute store result score result wk.math run data get entity @s \
    SelectedItem.components."minecraft:enchantments".levels."minecraft:density" 10

# 运算 [( density_level * 5 ) / 10] + damage * 10
execute store result score val_0 wk.math run data get storage wing_kirin:ram math.result 10
scoreboard players operation result wk.math += val_0 wk.math

# 将运算结果储存到命令存储
execute store result storage wing_kirin:ram math.result float 0.1 run scoreboard players get result wk.math
