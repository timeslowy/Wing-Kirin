# 根据技能等级设置初始值
execute store result storage wing_kirin:ram math.result int 15 run data get entity @s data.projectile_level

# 根据玩家力量效果等级进行加成
$execute as $(hex_Owner) run function wing_kirin:lib/entity/damage_bounce/damage_through_strength_effect

# 根据玩家爪牙槽武器的锋利等级进行加成
$execute as $(hex_Owner) run function wing_kirin:lib/entity/damage_bounce/damage_through_sharpness_enchantment

# 根据玩家主手重锤的致密等级进行加成
$execute as $(hex_Owner) run function wing_kirin:lib/entity/damage_bounce/damage_through_density_enchantment

# 根据玩家是否有“浩然正气”效果进行加成
$execute as $(hex_Owner) run function wing_kirin:lib/entity/damage_bounce/damage_through_great_zhengqi_effect

# 存入标志实体数据里
data modify entity @s data.damage set from storage wing_kirin:ram math.result
