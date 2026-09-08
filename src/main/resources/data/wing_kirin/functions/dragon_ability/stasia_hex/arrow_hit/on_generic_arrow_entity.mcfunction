## 在 "dragonsurvival:generic_arrow_entity" 生成的下一瞬间执行
# 执行者: player
# 1.20.1 移植：@n → @e[...,limit=1,sort=nearest]（两者均只选存活实体；distance=0.. 为恒真过滤，保留以对齐原写法）

# 增加标签，此标签在 wing_kirin:dragon_ability/stasia_hex/desctuor/auto_kill 和 wing_kirin:dragon_ability/stasia_hex/arrow_hit 用到
tag @e[distance=0..,type=dragonsurvival:generic_arrow_entity,nbt={general_data:{name:"wing_kirin:ding"}},limit=1,sort=nearest] add stasia_hex-ding
