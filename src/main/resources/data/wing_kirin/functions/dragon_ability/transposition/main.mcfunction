## 执行者：被视线选中的生物
# 传送玩家
execute as @p[tag=transposition_ready,distance=..15] rotated as @s run function wing_kirin:dragon_ability/transposition/tp_player

# 根据先前玩家生成的标志实体进行目标的传送，实现近似交换效果
tp @s @e[type=marker,tag=transposition_player_position,limit=1,sort=nearest]

# 杀掉这个标记位置的标志实体
kill @e[type=marker,tag=transposition_player_position,limit=1,sort=nearest]


