# 增加一次玩家的尝试次数（计分板）
scoreboard players add @s wk.empyrean_wine.attempt_count 1

# 显示 金风玉露 的尝试获取次数
title @s actionbar [{"translate": "actionbar.wing_kirin.empyrean_wine.cast_describiton_0"}, \
    {"score": {"name": "@s","objective": "wk.empyrean_wine.attempt_count" },"color": "#fbdc92"}, \
    {"translate": "actionbar.wing_kirin.empyrean_wine.cast_describiton_1"}]