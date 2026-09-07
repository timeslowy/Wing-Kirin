## 执行者：标志实体（由 ability_data/player 在施法瞬间调用）
## 1.20.1 移植版：移除 neoforge:attachments 路径段与 UUID 十六进制转换段
##  - 施法者 UUID / 等级改由 ability_data/player 经命令存储传入
##  - 配对 id 由自增伪玩家 #hj_next 直接写入标志实体计分板

# 将施法者UUID传入标志实体自身
data modify entity @s data.Owner set from storage wing_kirin:ram heavenly_justice.Owner

# 将施法者等级从命令存储传入标志实体
data modify entity @s data.projectile_level set from storage wing_kirin:ram heavenly_justice.level

# 记录配对 id（施法者与标志实体持同一 id，供后续 if score 反查）
scoreboard players operation @s wk.heavenly_justice.cast_id = #hj_next wk.heavenly_justice.cast_id

# 将标志实体位置传入自身
data modify entity @s data.Pos set from entity @s Pos

# 移除新生成标签(防止重复写入)
tag @s remove new_hj_marker
