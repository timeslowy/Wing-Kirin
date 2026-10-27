#> wing_kirin:tick/last_stand
# @executor server → 所有玩家
# @frequency tick
# @guard 首行：无玩家 death_countdown ≥ 0 时立即返回（未设置或已 reset 均判 false）
# @input 计分板 wk.last_stand.death_countdown
# @output 倒计时每刻 -1；归零时由 last_stand/kill 自毙并清空计分板
# @cleanup 由 kill 重置 death_countdown；death_check 由 player_death/main 清理
## 回光返照常驻入口：死亡倒计时
execute unless entity @a[scores={wk.last_stand.death_countdown=0..}] run return 0

execute as @a if score @s wk.last_stand.death_countdown matches 0.. run function wing_kirin:dragon_ability/last_stand/tick
