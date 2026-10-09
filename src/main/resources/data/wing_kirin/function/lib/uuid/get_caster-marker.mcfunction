## 执行者：标志实体
# 标志实体→标志实体

# 计算十六进制UUID并存入标志实体
data modify storage wing_kirin:uuid main.in set from entity @s data.Owner
function wing_kirin:lib/uuid/dec_to_hex
data modify entity @s data.hex_Owner set from storage wing_kirin:uuid main.out