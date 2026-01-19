## 执行者：玩家
# 将玩家的技能等级存入命令存储
execute store result storage wing_kirin:ram heavenly_justice.level int 1 run dragon-ability query @s dragonsurvival:heavenly_justice level

# 设置自身判断计时器
scoreboard players set @s wk.heavenly_justice.countdown 40