## 计算hex_uuid_bit
# 执行者: player


# 获取初始值
scoreboard players operation result wk.math = int-uuid wk.math
# 求余
scoreboard players operation result wk.math %= #16 wk.math
# uuid为负数求补码
execute if data storage wing_kirin:ram {math:{hex_uuid_sign:true}} run function wing_kirin:lib/math/uuid-dec_to_hex/hex_complement
# 存入命令存储
execute store result storage wing_kirin:ram math.result int 1 run scoreboard players get result wk.math
# 映射表 hex_map 储存至 math.hex
function wing_kirin:lib/math/uuid-dec_to_hex/hex_map with storage wing_kirin:ram math


# 在 hex_uuid_block 前插入 hex_uuid_bit
function wing_kirin:lib/math/uuid-dec_to_hex/insert-hex_uuid_bit with storage wing_kirin:ram math


# 递除uuid
scoreboard players operation int-uuid wk.math /= #16 wk.math
