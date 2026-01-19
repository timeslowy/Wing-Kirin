## 执行者：玩家
## 执行位置：原标志实体
# 移除玩家的计分板（空），使得伤害调整（破隐一击）失效
scoreboard players reset @s wk.instant_invisibility.remove_check

# 清除施法者的隐身效果（主要在离开法术范围时自动清除）
effect clear @s invisibility

# 当施法者离开限定距离之后自动清除属于此玩家的标志实体
kill @n[type=marker,tag=instant_invisibility-select]

# 现身粒子特效
execute at @s run particle cloud ~ ~ ~ 1 1 1 0 80

# 现身音效
execute at @s run playsound block.fire.extinguish player @a ~ ~ ~