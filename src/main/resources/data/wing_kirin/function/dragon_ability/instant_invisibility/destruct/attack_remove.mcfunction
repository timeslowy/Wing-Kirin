## 执行者：玩家
# 触发攻击检测成就时移除玩家的计分板
scoreboard players reset @s wk.instant_invisibility.remove_check

# 剥夺进度使其可反复触发
advancement revoke @s only wing_kirin:function/instant_invisibility_check

# 增加“破隐一击次数”统计数据值
wk-stats add @s wing_kirin:broken_instant_invisibility_times 1