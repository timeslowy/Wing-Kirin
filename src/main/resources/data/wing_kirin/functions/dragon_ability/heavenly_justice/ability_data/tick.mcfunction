## 执行者：玩家
## 1.20.1 移植版：`return run` 为 1.20.2+ 语法（1.20.1 的 /return 仅支持 <int>），改为 if/unless 反转互斥

# 如果超过时间未被重置则输出失败提示（即没有成功定位坐标）
execute if score @s wk.heavenly_justice.countdown matches ..0 run function wing_kirin:dragon_ability/heavenly_justice/message/failed_message

# 重置玩家计分板（与上一行互斥：未归零时不会执行）
execute if score @s wk.heavenly_justice.countdown matches ..0 run scoreboard players reset @s wk.heavenly_justice.countdown

# 处理计时器
execute if score @s wk.heavenly_justice.countdown matches 1.. run scoreboard players remove @s wk.heavenly_justice.countdown 1
