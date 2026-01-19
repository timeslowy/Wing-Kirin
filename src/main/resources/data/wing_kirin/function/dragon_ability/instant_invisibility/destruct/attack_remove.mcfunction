## 执行者：玩家
# 触发攻击检测成就时移除玩家的计分板
scoreboard players reset @s wk.instant_invisibility.remove_check

# ......时移除玩家隐身效果
effect clear @s invisibility

# 现身粒子特效
particle cloud ~ ~ ~ 1 1 1 0 80

# 现身音效
playsound block.fire.extinguish player @a ~ ~ ~

# 剥夺进度使其可反复触发
advancement revoke @s only wing_kirin:function/instant_invisibility_check