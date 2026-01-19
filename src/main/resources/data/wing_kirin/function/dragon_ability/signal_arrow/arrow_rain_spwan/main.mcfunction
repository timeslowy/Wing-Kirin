## 执行者: `signal_arrow_spawns`（标志实体）

# 缓存数据到命令存储
data modify storage wing_kirin:ram signal_arrow set from entity @s data

# 缓存生成的箭雨数
execute store result score @s wk.signal_arrow_spawn_count run data get storage wing_kirin:ram signal_arrow.tick_spawn_count

# 根据计分板的箭雨数量循环生成自定义弹射物（箭雨）
execute at @s run function wing_kirin:dragon_ability/signal_arrow/arrow_rain_spwan/cycle_spawn