## 执行者：穿云箭


# 根据不同等级以粒子显示范围
# 1级（12）
execute if data storage wing_kirin:ram {signal_arrow:{projectile_level:1}} run return \
    run function wing_kirin:dragon_ability/signal_arrow/radiu_display/success/level_1
# 2级（14）
execute if data storage wing_kirin:ram {signal_arrow:{projectile_level:2}} run return \
    run function wing_kirin:dragon_ability/signal_arrow/radiu_display/success/level_2
# 3级（16）
execute if data storage wing_kirin:ram {signal_arrow:{projectile_level:3}} run return \
    run function wing_kirin:dragon_ability/signal_arrow/radiu_display/success/level_3
# 4级（18）
execute if data storage wing_kirin:ram {signal_arrow:{projectile_level:4}} run return \
    run function wing_kirin:dragon_ability/signal_arrow/radiu_display/success/level_4
# 5级（20）
execute if data storage wing_kirin:ram {signal_arrow:{projectile_level:5}} run return \
    run function wing_kirin:dragon_ability/signal_arrow/radiu_display/success/level_5