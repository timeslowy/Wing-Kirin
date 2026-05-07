## 执行者：玩家（由 main 路由进入，距离已在 20..24.99 范围内）
## 执行位置：标志实体处
# 红色 — 精确倒计时（保持原有精度，末尾无条件裁断）
execute if entity @s[distance=20..21] run return \
    run title @s actionbar [{"translate": "actionbar.wing_kirin.instant_invisibility.distance_notice"},{"text": "5","color": "#e61717"}]

execute if entity @s[distance=21..22] run return \
    run title @s actionbar [{"translate": "actionbar.wing_kirin.instant_invisibility.distance_notice"},{"text": "4","color": "#e61717"}]

execute if entity @s[distance=22..23] run return \
    run title @s actionbar [{"translate": "actionbar.wing_kirin.instant_invisibility.distance_notice"},{"text": "3","color": "#e61717"}]

execute if entity @s[distance=23..24] run return \
    run title @s actionbar [{"translate": "actionbar.wing_kirin.instant_invisibility.distance_notice"},{"text": "2","color": "#e61717"}]

return run title @s actionbar [{"translate": "actionbar.wing_kirin.instant_invisibility.distance_notice"},{"text": "1","color": "#e61717"}]
