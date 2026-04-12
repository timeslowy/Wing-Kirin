## 每刻执行各个技能里的主函数触发函数
## 初始执行者：服务器（server）

# 给 定身术 每刻执行刻函数
function wing_kirin:dragon_ability/stasia_hex/tick

# 给 金风玉露 执行刻函数
execute as @a[predicate=wing_kirin:wing_kirin] run function wing_kirin:dragon_ability/empyrean_wine/tick

# 给 回光返照 执行刻函数
execute as @a run function wing_kirin:dragon_ability/last_stand/tick

# 给 不坏金身 执行刻函数
execute as @a[predicate=wing_kirin:wing_kirin] run function wing_kirin:dragon_ability/indestructible_body/tick

# 为 聚形散气 标志实体执行
execute as @e[type=marker,tag=instant_invisibility] at @s run function wing_kirin:dragon_ability/instant_invisibility/tick with entity @s data

# 给 一支穿云箭 标志实体每刻执行主函数
execute as @e[type=marker,tag=signal_arrow_generic] at @s run function wing_kirin:dragon_ability/signal_arrow/tick

# 为 天降正义 标志实体 执行
execute as @e[type=marker,tag=heavenly_justice_marker] run function wing_kirin:dragon_ability/heavenly_justice/tick
# 为 天降正义 发射者 执行
execute as @a[predicate=wing_kirin:wing_kirin] run function wing_kirin:dragon_ability/heavenly_justice/ability_data/tick

# 玩家死亡执行
execute as @a if score @s wk.death_check matches 1.. run function wing_kirin:player_death/main

