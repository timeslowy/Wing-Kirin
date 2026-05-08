##执行者：玩家（技能函数触发）
# 将玩家的此技能技能计分板分数设成1
scoreboard players set @s wk.instant_invisibility.remove_check 1

# 将玩家的UUID传入命令存储后进行计算
data modify storage wing_kirin:ram math.UUID set from entity @s UUID

# 十进制UUID转换为十六进制（提高查找速度）
function wing_kirin:lib/math/uuid-dec_to_hex/main

# 将玩家的技能等级存入命令存储
execute store result storage wing_kirin:ram instant_invisibility.level int 1 run dragon-ability query @s wing_kirin:instant_invisibility level

# 生成标志实体来框定法术范围（施法位置处）
summon minecraft:marker ~ ~ ~ {Tags:[new_spawn,instant_invisibility]}

# 初始化标志实体（UUID、计时器、清除临时标签）
execute as @n[type=marker,tag=new_spawn] run function wing_kirin:dragon_ability/instant_invisibility/init_marker