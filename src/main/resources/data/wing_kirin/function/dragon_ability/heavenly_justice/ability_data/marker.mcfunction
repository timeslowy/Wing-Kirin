## 执行者：标志实体
# 将施法者UUID传入标志实体自身
data modify entity @s data.Owner set from entity @s "neoforge:attachments"."dragonsurvival:summon_data".data.owner_uuid

# 计算hex_Owner
function wing_kirin:lib/uuid/get_caster-marker

# 将施法者等级从命令存储传入标志实体
data modify entity @s data.projectile_level set from storage wing_kirin:ram heavenly_justice.level

# 将标志实体位置传入自身
data modify entity @s data.Pos set from entity @s Pos
