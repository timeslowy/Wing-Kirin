## 执行者：施法者
# 玩家→命令存储

# 将施法者的UUID转换为16进制并存入命令存储
data modify storage wing_kirin:uuid main.in set from entity @s UUID
function wing_kirin:lib/uuid/dec_to_hex