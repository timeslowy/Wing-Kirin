## 在 "dragonsurvival:generic_ball_entity" 生成的下一瞬间执行
# 执行者: player
## 1.20.1 移植版：
##  - 移除 wk-stats 统计行（统计命令随后续批次移植；未知命令会导致整个函数解析失败）
##  - @n 为 1.20.2+ 选择器，1.20.1 用 @e[...limit=1,sort=nearest] 等价替代
##    （@n[args] ≡ @e[args,limit=1,sort=nearest]，两者均只选存活实体；distance=0.. 为恒真过滤已省略）

# 增加标签
tag @e[type=dragonsurvival:generic_ball_entity,nbt={general_data:{name:"wing_kirin:signal_arrow"}},limit=1,sort=nearest] add signal_arrow-generic_entity

# 播放射出音效
playsound minecraft:entity.arrow.shoot player @a ~ ~ ~ .5 1.5

# 增加“射出穿云箭”统计数据值
wk-stats add @s wing_kirin:shoot_signal_arrow_count 1
