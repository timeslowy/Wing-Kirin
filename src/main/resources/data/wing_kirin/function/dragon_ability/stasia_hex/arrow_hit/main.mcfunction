## 执行者: 自定义箭类弹射物击中的实体
# 箭击用

# 移除标签防止范围计时器继续运行
tag @s remove being_frozen-area

# 叠加机制1：将范围定身的剩余时长转移至箭击定身计时器中
scoreboard players operation @s wk.stasis_hex.freezeTimer-arrow += @s wk.stasis_hex.freezeTimer-area

# 给范围定身的实体移除范围定时器以防止额外箭定身效果提前失效
scoreboard players reset @s wk.stasis_hex.freezeTimer-area

# 增加一次定身次数计次
scoreboard players add @s wk.stasis_hex.freezeTimer-max_count 1

# 检查：能定身的最大次数为10次，超过不予叠加时长
execute unless score @s wk.stasis_hex.freezeTimer-max_count matches ..10 run return run tag @s add being_frozen-arrow

# 叠加机制2：若实体已被定身则时长叠加机制
execute if score @s wk.stasis_hex.freezeTimer-arrow matches 1.. run return run function wing_kirin:dragon_ability/stasia_hex/arrow_hit/superposition

# 初始击中，增加正常初始时长
function wing_kirin:dragon_ability/stasia_hex/arrow_hit/initial_hit





