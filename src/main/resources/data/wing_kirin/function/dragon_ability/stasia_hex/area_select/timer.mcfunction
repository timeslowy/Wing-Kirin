## 执行者：被范围定身选中的生物（统一计时器 freezeTimer）
# 叠加机制：每次此生物被范围定身可加长时间150刻（7.5秒），有上限：1200刻（60秒）
execute if score @s wk.stasis_hex.freezeTimer matches 1.. run return \
    run scoreboard players add @s wk.stasis_hex.freezeTimer 150

# 首次范围定身：设置时长为300刻
scoreboard players add @s wk.stasis_hex.freezeTimer 300
