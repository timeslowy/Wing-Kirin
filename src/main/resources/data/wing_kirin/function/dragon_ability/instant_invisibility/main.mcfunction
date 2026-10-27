## 执行者：玩家（技能函数触发）
# 将玩家的此技能技能计分板分数设成1
scoreboard players set @s wk.instant_invisibility.remove_check 1

# 获取施法者的十六进制UUID
function wing_kirin:lib/uuid/get_caster-player

# 将玩家的技能等级存入命令存储
execute store result storage wing_kirin:ram instant_invisibility.level int 1 run dragon-ability query @s wing_kirin:instant_invisibility level

# 生成标志实体来框定法术范围（施法位置处）
summon minecraft:marker ~ ~ ~ {Tags:[new_spawn,instant_invisibility]}

# 初始化标志实体（UUID、计时器、清除临时标签）
execute as @n[type=marker,tag=new_spawn] run function wing_kirin:dragon_ability/instant_invisibility/init_marker