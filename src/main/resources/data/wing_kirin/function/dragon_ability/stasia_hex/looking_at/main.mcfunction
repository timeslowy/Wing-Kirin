## 执行者: 所看向的实体
# 视指用（统一标签 being_frozen）

# 增加一次定身次数计次
scoreboard players add @s wk.stasis_hex.freezeTimer-max_count 1

# 满定身限制提示
execute if score @s wk.stasis_hex.freezeTimer-max_count matches 10.. run return run function wing_kirin:dragon_ability/stasia_hex/display/max_district_notice

# 叠加机制：若实体已被定身（freezeTimer ≥ 1）则叠加时长
execute if score @s wk.stasis_hex.freezeTimer matches 1.. run return run function wing_kirin:dragon_ability/stasia_hex/looking_at/superposition with storage wing_kirin:uuid main

# 初始击中，增加正常初始时长
function wing_kirin:dragon_ability/stasia_hex/looking_at/initial_hit with storage wing_kirin:uuid main
