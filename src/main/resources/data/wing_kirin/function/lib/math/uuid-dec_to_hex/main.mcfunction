## 将执行者的 Int-UUID 转换成 Hex-UUID
# 输入: storage wing_kirin:ram math.UUID
# 结果: storage wing_kirin:ram math.hex_uuid


## 初始化
# 设置映射表
data modify storage wing_kirin:ram math.hex_map set value ['0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f']
# 初始化结果
data modify storage wing_kirin:ram math.hex_uuid set value ""

# 计算hex_uuid_block
function wing_kirin:lib/math/uuid-dec_to_hex/calculate_uuid_block {num:0}
function wing_kirin:lib/math/uuid-dec_to_hex/calculate_uuid_block {num:1}
function wing_kirin:lib/math/uuid-dec_to_hex/calculate_uuid_block {num:2}
function wing_kirin:lib/math/uuid-dec_to_hex/calculate_uuid_block {num:3}
