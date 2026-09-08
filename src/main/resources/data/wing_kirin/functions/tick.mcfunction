## 每刻执行各个技能里的主函数触发函数
## 初始执行者：服务器（server）
## （已为每条指令添加前置守卫，空闲时跳过无效遍历）

# 给 定身术 每刻执行刻函数（标签已统一为 being_frozen；@e 全实体扫描不可避免，无匹配时仅有扫描开销、无分发开销）
execute as @e[predicate=wing_kirin:is_living_entity,tag=being_frozen] run function wing_kirin:dragon_ability/stasia_hex/main-tick
# 定身术 孤儿“定”字展示实体清理（宿主死亡后 being_frozen 随之消失，故须独立于上方扫描：无载具的展示实体即孤儿）
kill @e[type=item_display,tag=item_display.being_frozen,predicate=wing_kirin:no_vehicle]

# 给 金风玉露 执行刻函数（守卫：working_symbol 在 0..25 之间，即技能激活或待重置）
execute as @a[predicate=wing_kirin:wing_kirin] if score @s wk.empyrean_wine.working_symbol matches 0.. run function wing_kirin:dragon_ability/empyrean_wine/tick

# 给 回光返照 执行刻函数（守卫：death_countdown 存在且 ≥0，即倒计时中或待击杀）
execute as @a if score @s wk.last_stand.death_countdown matches 0.. run function wing_kirin:dragon_ability/last_stand/tick

# 给 不坏金身 执行刻函数（守卫：working_symbol 在 ..26 之间，即激活中或待重置）
# execute as @a[predicate=wing_kirin:wing_kirin] if score @s wk.indestructible_body.working_symbol matches ..26 run function wing_kirin:dragon_ability/indestructible_body/tick

# 为 聚形散气 标志实体执行（已通过 marker tag 过滤，无需额外守卫）
# execute as @e[type=marker,tag=instant_invisibility] at @s run function wing_kirin:dragon_ability/instant_invisibility/tick with entity @s data

# 给 一支穿云箭 标志实体每刻执行主函数（已通过 marker tag 过滤，无需额外守卫）
# execute as @e[type=marker,tag=signal_arrow_generic] at @s run function wing_kirin:dragon_ability/signal_arrow/tick

# 为 天降正义 标志实体 执行（已通过 marker tag 过滤，无需额外守卫）
execute as @e[type=marker,tag=heavenly_justice_marker] run function wing_kirin:dragon_ability/heavenly_justice/tick
# 为 天降正义 发射者 执行（守卫：countdown 存在且 ≥0，即定位中或待输出失败消息）
execute as @a[predicate=wing_kirin:wing_kirin] if score @s wk.heavenly_justice.countdown matches 0.. run function wing_kirin:dragon_ability/heavenly_justice/ability_data/tick

# 玩家死亡执行（已通过 death_check 计分板过滤，无需额外守卫）
# execute as @a if score @s wk.death_check matches 1.. run function wing_kirin:player_death/main

