#> wing_kirin:tick/signal_arrow
# @executor server → 箭雨标志实体（marker）
# @frequency tick
# @guard 选择器即守卫：@e[type=marker,tag=signal_arrow_generic]，无实体时不产生任何命令
# @input marker 自身 data（level、tick_spawn_count、life 等）+ 计分板 wk.signal_arrow.life
# @output 每刻按 tick_spawn_count 生成箭实体；life 每刻 -1，归零自毙
# @cleanup tag signal_arrow_generic、计分板 wk.signal_arrow.life
## 一支穿云箭常驻入口：箭雨生成（当前最大性能热点，见交接文档 5.2.A）
execute as @e[type=marker,tag=signal_arrow_generic] at @s run function wing_kirin:dragon_ability/signal_arrow/tick
