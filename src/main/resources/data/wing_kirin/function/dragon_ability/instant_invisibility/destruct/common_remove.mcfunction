## 执行者：玩家
## 执行位置：标志实体

# 删去标志实体
kill @n[type=marker,tag=instant_invisibility-select]

# 移去隐身
effect clear @s minecraft:invisibility

# 现身粒子特效
execute at @s run particle cloud ~ ~ ~ 1 1 1 0 80

# 现身音效
execute at @s run playsound block.fire.extinguish player @a ~ ~ ~