## 执行者：玩家（由 main 路由进入，距离已在 1..10.99 范围内）
## 执行位置：标志实体处
# 绿色 — 剩余安全距离概览（原24档简化为3档）
execute if entity @s[distance=1..4] run return \
    run title @s actionbar [{"translate": "actionbar.wing_kirin.instant_invisibility.distance_notice"},{"text": ">21","color": "#35e035"}]

execute if entity @s[distance=4..7] run return \
    run title @s actionbar [{"translate": "actionbar.wing_kirin.instant_invisibility.distance_notice"},{"text": ">18","color": "#35e035"}]

return run title @s actionbar [{"translate": "actionbar.wing_kirin.instant_invisibility.distance_notice"},{"text": ">15","color": "#35e035"}]
