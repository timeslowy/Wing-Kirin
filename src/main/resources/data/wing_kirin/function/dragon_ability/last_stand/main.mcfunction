## 执行者：玩家自己
# 将玩家的技能等级存入命令存储
execute store result storage wing_kirin:ram last_stand.level int 1 run dragon-ability query @s dragonsurvival:last_stand level

# 根据传入的等级设置标分版分数来达到计时效果
execute store result score @s wk.last_stand.death_countdown run data get storage wing_kirin:ram last_stand.level 800

# 增加死亡计数检测（防止重复触发）
scoreboard players set @s wk.last_stand.death_check 1

# 提示
function wing_kirin:dragon_ability/last_stand/notice