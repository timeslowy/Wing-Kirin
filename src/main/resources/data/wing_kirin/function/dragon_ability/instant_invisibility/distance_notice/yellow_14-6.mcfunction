## 执行者：玩家（由 main 路由进入，距离已在 11..19.99 范围内）
## 执行位置：标志实体处
# 黄色 — 警告距离概览（原9档简化为3档）
execute if entity @s[distance=11..14] run return \
    run title @s actionbar [{"translate": "actionbar.wing_kirin.instant_invisibility.distance_notice"},{"text": ">11","color": "#e9f00e"}]

execute if entity @s[distance=14..17] run return \
    run title @s actionbar [{"translate": "actionbar.wing_kirin.instant_invisibility.distance_notice"},{"text": ">8","color": "#e9f00e"}]

return run title @s actionbar [{"translate": "actionbar.wing_kirin.instant_invisibility.distance_notice"},{"text": "6","color": "#e9f00e"}]

