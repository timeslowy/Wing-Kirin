## 执行于弹射物（打出的球弹）消失后
# 执行者: 穿云箭（DS projectile_data on_destroy_effects 的 run_function，以弹射物为执行者、爆点为坐标）
# 1.20.1 移植版：与 1.21.1 原样一致（无宏）

# 为 自定义弹射物 生成 signal_arrow_spawns 标志实体
function wing_kirin:dragon_ability/signal_arrow/signal_arrow_spawn_marker/main

# 清除自定义弹射物
kill @s
