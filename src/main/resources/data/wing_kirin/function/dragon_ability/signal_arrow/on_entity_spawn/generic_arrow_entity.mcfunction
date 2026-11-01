## 在 "dragonsurvival:generic_arrow_entity" 生成的下一瞬间执行
# 执行者: player
# 播放射出音效
playsound minecraft:entity.arrow.shoot player @a ~ ~ ~ .5 1.5

# 增加“射出穿云箭”统计数据值
wk-stats add @s wing_kirin:shoot_signal_arrow_count 1
