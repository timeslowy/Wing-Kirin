## 执行者: 自定义箭类弹射物击中的实体
# 箭击用（统一标签 being_frozen）

# 增加一次定身次数计次
scoreboard players add @s wk.stasis_hex.freezeTimer-max_count 1

# 检查：能定身的最大次数为10次，超过不予叠加时长
execute unless score @s wk.stasis_hex.freezeTimer-max_count matches ..10 run return run tag @s add being_frozen

# 叠加机制：若实体已被定身（freezeTimer ≥ 1）则叠加时长
execute if score @s wk.stasis_hex.freezeTimer matches 1.. run return run function wing_kirin:dragon_ability/stasia_hex/arrow_hit/superposition

# 初始击中，增加正常初始时长
function wing_kirin:dragon_ability/stasia_hex/arrow_hit/initial_hit





