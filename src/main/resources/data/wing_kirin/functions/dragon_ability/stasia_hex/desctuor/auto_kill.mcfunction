##执行者：弹射物数据定义的 block_hit_effect 所执行（命中方块时）
# 1.20.1 移植：@n → @e[limit=1,sort=nearest]

# 自动杀死未攻击到实体（落在地上）的弹射物，防止卡顿
kill @e[distance=0..,type=dragonsurvival:generic_arrow_entity,tag=stasia_hex-ding,limit=1,sort=nearest]
