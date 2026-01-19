## 执行者：被范围定身选中的生物
# 叠加机制2：每次此生物被范围定身可加长时间150刻（7.5秒），有上限：800刻（40秒）
execute if score @s wk.stasis_hex.freezeTimer-area matches 1.. run return \
    run scoreboard players add @s wk.stasis_hex.freezeTimer-area 150

# 设置定身时长为300刻
# 给范围内生物设置范围定身定时器为300刻
scoreboard players add @s wk.stasis_hex.freezeTimer-area 300
