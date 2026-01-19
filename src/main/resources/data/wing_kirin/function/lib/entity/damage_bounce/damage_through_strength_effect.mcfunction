## 通过力量等级计算伤害加成
# 执行者: player
# 输入: wing_kirin:ram math.result
# 结果: wing_kirin:ram math.result

# 直接获取药水效果modifier值
execute store result score val_0 wk.math run attribute @s minecraft:generic.attack_damage modifier value get minecraft:effect.strength 50

# 如果玩家没有获得力量效果则直接结束
execute if score val_0 wk.math matches 0 run return fail

# 运算 modifier + damage
execute store result score result wk.math run data get storage wing_kirin:ram math.result 10
scoreboard players operation result wk.math += val_0 wk.math

# 将运算结果储存到命令存储
execute store result storage wing_kirin:ram math.result float 0.1 run scoreboard players get result wk.math
