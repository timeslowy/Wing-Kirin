## 循环生成直至箭雨库存（signal_arrow.tick_spawn_count）消耗殆尽

# 当计分板数小于等于0时结束
execute if score @s wk.signal_arrow_spawn_count matches ..0 run return 0

# 每刻减少箭雨库存数
scoreboard players remove @s wk.signal_arrow_spawn_count 1

# 生成自定义弹射物（箭雨）
function wing_kirin:dragon_ability/signal_arrow/arrow_rain_spwan/spawn

# 循环生成
function wing_kirin:dragon_ability/signal_arrow/arrow_rain_spwan/cycle_spawn