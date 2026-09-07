## 执行者：玩家
## 显示箭雨大致范围
## 1.20.1 移植版：dragon-ability query 在 DS 1.20.1 可用；无 /return run（1.20.2+），分支改为直接 run function（等级互斥，至多命中一支）
# 将玩家技能等级存入命令存储
execute store result storage wing_kirin:ram signal_arrow.level int 1 run dragon-ability query @s wing_kirin:signal_arrow level

# 根据不同等级以粒子显示范围
# 1级（12）
execute if data storage wing_kirin:ram {signal_arrow:{level:1}} run function wing_kirin:dragon_ability/signal_arrow/radiu_display/charging/level_1
# 2级（14）
execute if data storage wing_kirin:ram {signal_arrow:{level:2}} run function wing_kirin:dragon_ability/signal_arrow/radiu_display/charging/level_2
# 3级（16）
execute if data storage wing_kirin:ram {signal_arrow:{level:3}} run function wing_kirin:dragon_ability/signal_arrow/radiu_display/charging/level_3
# 4级（18）
execute if data storage wing_kirin:ram {signal_arrow:{level:4}} run function wing_kirin:dragon_ability/signal_arrow/radiu_display/charging/level_4
# 5级（20）
execute if data storage wing_kirin:ram {signal_arrow:{level:5}} run function wing_kirin:dragon_ability/signal_arrow/radiu_display/charging/level_5
