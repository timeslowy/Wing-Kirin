## 执行者：被范围定身选中的生物
# 执行位置：此生物（带标签stasis_hex-area）

# 移除标签防止箭击定时器继续运行
execute if entity @s[tag=being_frozen-arrow] run tag @s remove being_frozen-arrow

# 叠加机制1：将箭击定身的剩余时长转移至范围定身计时器中
scoreboard players operation @s wk.stasis_hex.freezeTimer-area += @s wk.stasis_hex.freezeTimer-arrow

# 给箭定身效果实体移除箭用定时器
scoreboard players reset @s wk.stasis_hex.freezeTimer-arrow

# 增加一次定身次数计次
scoreboard players add @s wk.stasis_hex.freezeTimer-max_count 1

# 计时器设置,能定身的最大次数为10次
execute if score @s wk.stasis_hex.freezeTimer-max_count matches ..10 run function wing_kirin:dragon_ability/stasia_hex/area_select/timer

# 限制时长：1200刻（60秒）
execute if score @s wk.stasis_hex.freezeTimer-area matches 1201.. run scoreboard players set @s wk.stasis_hex.freezeTimer-area 1200

# 给生物增加范围定身标签
tag @s add being_frozen-area

# 移除所用标签
tag @s remove stasis_hex-area


# 应用效果
execute if score @s wk.stasis_hex.freezeTimer-max_count matches ..10 run function wing_kirin:dragon_ability/stasia_hex/apply/main


