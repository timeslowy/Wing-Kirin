## 执行者：玩家自己

# 计算倒计时长
function wing_kirin:dragon_ability/last_stand/countdown

# 增加死亡计数检测（防止重复触发）
scoreboard players set @s wk.last_stand.death_check 1

# 提示
function wing_kirin:dragon_ability/last_stand/notice