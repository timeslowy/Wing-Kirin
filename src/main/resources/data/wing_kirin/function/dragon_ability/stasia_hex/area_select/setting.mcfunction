## 执行者：被范围定身选中的生物（统一标签 being_frozen）
# 执行位置：此生物（带标签stasis_hex-area）

# 增加一次定身次数计次
scoreboard players add @s wk.stasis_hex.freezeTimer-max_count 1

# 计时器设置,能定身的最大次数为10次
execute if score @s wk.stasis_hex.freezeTimer-max_count matches ..10 run function wing_kirin:dragon_ability/stasia_hex/area_select/timer

# 限制时长：1200刻（60秒）
execute if score @s wk.stasis_hex.freezeTimer matches 1201.. run scoreboard players set @s wk.stasis_hex.freezeTimer 1200

# 给生物增加统一定身标签
tag @s add being_frozen

# 移除所用临时标签
tag @s remove stasis_hex-area

# 应用效果
execute if score @s wk.stasis_hex.freezeTimer-max_count matches ..10 run function wing_kirin:dragon_ability/stasia_hex/apply/set_duration


