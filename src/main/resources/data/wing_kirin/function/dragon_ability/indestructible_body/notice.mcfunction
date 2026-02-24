## 执行者：玩家自身
# 设运行标志计分板为0
scoreboard players set @s wk.indestructible_body.working_symbol 0

# 显示反震次数计分板
title @s actionbar [{"translate":"actionbar.wing_kirin.indestructible_body.counter_shock_count_0"}, \
    {"score": {"name": "@s","objective": "wk.indestructible_body.counter_shock_count"},"color": "#fbdc92"},{"translate": "actionbar.wing_kirin.indestructible_body.counter_shock_count_1"}]