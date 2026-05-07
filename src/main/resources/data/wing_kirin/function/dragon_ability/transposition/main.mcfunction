## 执行者：被视线选中的生物
# 传送玩家
execute as @n[type=player,tag=transposition_ready,distance=..15] rotated as @s run function wing_kirin:dragon_ability/transposition/tp_player

# 根据先前玩家存储的坐标进行目标的传送，实现近似交换效果
function wing_kirin:dragon_ability/transposition/tp_target with storage wing_kirin:ram transposition


