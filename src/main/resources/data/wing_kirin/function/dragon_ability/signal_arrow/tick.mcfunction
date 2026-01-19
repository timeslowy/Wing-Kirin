## 执行者: signal_arrow_spawn（标志实体）

# 生成箭雨
function wing_kirin:dragon_ability/signal_arrow/arrow_rain_spwan/main with entity @s data

# 粒子效果
particle minecraft:electric_spark ~ ~ ~ 3 0 3 0 5

# 计分板 remove：减，从标志实体减去
scoreboard players remove @s wk.signal_arrow.life 1

# 清理实体  
execute if score @s wk.signal_arrow.life matches ..0 run kill @s