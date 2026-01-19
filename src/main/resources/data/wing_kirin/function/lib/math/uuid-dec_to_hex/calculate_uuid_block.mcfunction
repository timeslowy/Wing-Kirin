## 计算hex_uuid_block
# 执行者: player


# 获取 int-uuid 值
$execute store result score int-uuid wk.math run data get storage wing_kirin:ram math.UUID[$(num)]
# 求 int-uuid 绝对值(并记录是否是负数)
data modify storage wing_kirin:ram math.hex_uuid_sign set value false
execute unless score int-uuid wk.math matches 1.. run data modify storage wing_kirin:ram math.hex_uuid_sign set value true
#execute unless score int-uuid wk.math matches 1.. run scoreboard players operation int-uuid wk.math *= #-1 wk.math
$execute unless score int-uuid wk.math matches 1.. run data get storage wing_kirin:ram math.UUID[$(num)] -1
# 初始化运算结果
data modify storage wing_kirin:ram math.hex_uuid_block set value ""
# 初始化溢出位
scoreboard players set overflow wk.math 1
# 将num存入缓存用于判断何时加入分隔符'-'
$scoreboard players set val_1 wk.math $(num)

# 计算hex_uuid_bit
function wing_kirin:lib/math/uuid-dec_to_hex/calculate_uuid_bit
function wing_kirin:lib/math/uuid-dec_to_hex/calculate_uuid_bit
function wing_kirin:lib/math/uuid-dec_to_hex/calculate_uuid_bit
function wing_kirin:lib/math/uuid-dec_to_hex/calculate_uuid_bit
execute if score val_1 wk.math matches 1..2 run function wing_kirin:lib/math/uuid-dec_to_hex/insert-separator with storage wing_kirin:ram math
function wing_kirin:lib/math/uuid-dec_to_hex/calculate_uuid_bit
function wing_kirin:lib/math/uuid-dec_to_hex/calculate_uuid_bit
function wing_kirin:lib/math/uuid-dec_to_hex/calculate_uuid_bit
function wing_kirin:lib/math/uuid-dec_to_hex/calculate_uuid_bit
execute if score val_1 wk.math matches 1..2 run function wing_kirin:lib/math/uuid-dec_to_hex/insert-separator with storage wing_kirin:ram math

# 在 hex_uuid 前插入 hex_uuid_block
function wing_kirin:lib/math/uuid-dec_to_hex/insert-hex_uuid_block with storage wing_kirin:ram math