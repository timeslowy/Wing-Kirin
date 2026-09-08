## 执行者: 自定义箭类弹射物击中的实体
# 箭击用（统一标签 being_frozen）
# 1.20.1 移植：无 return run，后续行全部改由互斥条件守卫

# 增加一次定身次数计次
scoreboard players add @s wk.stasis_hex.freezeTimer-max_count 1

# 检查：能定身的最大次数为10次，超过不予叠加时长（仅补统一标签）
execute unless score @s wk.stasis_hex.freezeTimer-max_count matches ..10 run tag @s add being_frozen

# 叠加机制：若实体已被定身（freezeTimer ≥ 1）则叠加时长
execute if score @s wk.stasis_hex.freezeTimer-max_count matches ..10 if score @s wk.stasis_hex.freezeTimer matches 1.. run function wing_kirin:dragon_ability/stasia_hex/arrow_hit/superposition

# 初始击中，增加正常初始时长
execute if score @s wk.stasis_hex.freezeTimer-max_count matches ..10 unless score @s wk.stasis_hex.freezeTimer matches 1.. run function wing_kirin:dragon_ability/stasia_hex/arrow_hit/initial_hit

# 满定身限制提示（原版仅在恰好第10次时提示；超过10次的中箭已在上方提前返回，不会走到这里）
execute if score @s wk.stasis_hex.freezeTimer-max_count matches 10 run function wing_kirin:dragon_ability/stasia_hex/display/max_district_notice
