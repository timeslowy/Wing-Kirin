#> wing_kirin:tick/player_death
# @executor server → 死亡玩家
# @frequency tick（death_check 置 1 后持续命中，直至 player_death/main 归零）
# @guard 首行：无玩家 death_check ≥ 1 时立即返回
# @input 计分板 wk.death_check
# @output 触发 player_death/main 完成定身、回光返照、聚形散气状态清理
# @cleanup 计分板 death_check、death_countdown、instant_invisibility.remove_check、tag being_frozen
## 玩家死亡常驻入口：非技能，属生命周期钩子
execute unless entity @a[scores={wk.death_check=1..}] run return 0

execute as @a if score @s wk.death_check matches 1.. run function wing_kirin:player_death/main
