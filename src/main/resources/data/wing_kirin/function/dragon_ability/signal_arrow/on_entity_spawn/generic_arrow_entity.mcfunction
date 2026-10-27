## 在 "dragonsurvival:generic_arrow_entity" 生成的下一瞬间执行
# 执行者: player
# 增加标签
# ⚠ 该标签当前无任何选择器消费（全工程仅此处写入），属死代码；
#   因无消费者，即使 @n 选到别人的球也不产生后果。保留行为，待 P2 清理。
tag @n[distance=0..,type=dragonsurvival:generic_ball_entity,nbt={general_data:{name:"wing_kirin:signal_arrow"}}] add signal_arrow-generic_entity

# 播放射出音效
playsound minecraft:entity.arrow.shoot player @a ~ ~ ~ .5 1.5

# 增加“射出穿云箭”统计数据值
wk-stats add @s wing_kirin:shoot_signal_arrow_count 1
