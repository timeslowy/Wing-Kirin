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

# 将UUID传入标志实体（data.Owner-hex为NBT路径）
execute as @n[type=marker,tag=new_spawn] run data modify entity @s data.Owner_hex set from storage wing_kirin:ram math.hex_uuid

# 根据传入的等级设置标志实体的记分版分数来达到计时效果，每级*200（即scale），每刻减少1(在main里减少)
execute as @n[type=marker,tag=new_spawn] store result score @s wk.instant_invisibility.duration \
    run data get storage wing_kirin:ram instant_invisibility.level 200

# 移除新生成标记防止选择器错乱
tag @n[type=marker,tag=new_spawn] remove new_spawn