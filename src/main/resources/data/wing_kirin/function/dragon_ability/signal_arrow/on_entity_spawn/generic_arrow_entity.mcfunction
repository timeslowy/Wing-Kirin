## 在 "dragonsurvival:generic_arrow_entity" 生成的下一瞬间执行
# 执行者: player


# 增加标签
tag @n[distance=0..,type=dragonsurvival:generic_ball_entity,nbt={general_data:{name:"wing_kirin:a_signal_arrow"}}] add signal_arrow-generic_entity


# 播放射出音效
playsound minecraft:entity.arrow.shoot player @a ~ ~ ~ .5 1.5
