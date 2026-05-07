## 执行者：玩家
# 将玩家的三维坐标存储入命令存储内
execute store result storage wing_kirin:ram transposition.x double 1 run data get entity @s Pos[0]
execute store result storage wing_kirin:ram transposition.y double 1 run data get entity @s Pos[1]
execute store result storage wing_kirin:ram transposition.z double 1 run data get entity @s Pos[2]

# 为自己添加标签以供后续判断
tag @s add transposition_ready