## 执行者：玩家
## 1.20.1 移植版：
##  - 移除 UUID 十六进制转换库调用（1.20.1 实体选择器不支持 UUID 字符串，该库无用途）
##  - 施法者 UUID / 技能等级 / 配对 id 改在施法瞬间直接写入标志实体
##    （1.21.1 从 neoforge:attachments 读召唤者；DS 1.20.1 的 SummonData 是内存附件，/data 读不到）

# 将玩家的技能等级存入命令存储
execute store result storage wing_kirin:ram heavenly_justice.level int 1 run dragon-ability query @s wing_kirin:heavenly_justice level

# 将施法者 UUID 存入命令存储
data modify storage wing_kirin:ram heavenly_justice.Owner set from entity @s UUID

# 生成自增配对 id，并发放给施法者（供后续从标志实体反查施法者）
scoreboard players add #hj_next wk.heavenly_justice.cast_id 1
scoreboard players operation @s wk.heavenly_justice.cast_id = #hj_next wk.heavenly_justice.cast_id

# 把上述数据写入刚生成的标志实体（写入后即刻摘掉 new_hj_marker 标签，防止重复写入）
execute as @e[type=marker,tag=new_hj_marker,limit=1,sort=nearest] run function wing_kirin:dragon_ability/heavenly_justice/ability_data/marker

# 设置自身判断计时器
scoreboard players set @s wk.heavenly_justice.countdown 40
