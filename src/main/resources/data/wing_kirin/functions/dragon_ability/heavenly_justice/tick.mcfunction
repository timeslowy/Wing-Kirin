## 执行者：标志实体
## 1.20.1 移植版：
##  - 移除 `with entity @s data` 宏调用（1.20.2+），成功消息改为直接调用
##  - 原「读取施法者数据」段已前移至 ability_data/player（施法瞬间写入）

# 给标志实体上计时器
execute unless score @s wk.heavenly_justice.countdown matches 0.. run scoreboard players set @s wk.heavenly_justice.countdown 400

# 兜底：若施法瞬间标志实体尚未生成（动作执行顺序所致），此处用命令存储补写一次
execute if score @s wk.heavenly_justice.countdown matches 400 if entity @s[tag=new_hj_marker] run function wing_kirin:dragon_ability/heavenly_justice/ability_data/marker

# 对打击地点进行标记
execute if score @s wk.heavenly_justice.countdown matches 400 at @s run function wing_kirin:dragon_ability/heavenly_justice/position_display/main

# 输出成功定位消息
execute if score @s wk.heavenly_justice.countdown matches 400 at @s run function wing_kirin:dragon_ability/heavenly_justice/message/successful_message

# 改换颜色
execute if score @s wk.heavenly_justice.countdown matches 100 at @s run function wing_kirin:dragon_ability/heavenly_justice/position_display/color_change

# 开始导弹部署
execute if score @s wk.heavenly_justice.countdown matches 40 at @s run function wing_kirin:dragon_ability/heavenly_justice/guided_missle/main

# 处理计时器
execute if score @s wk.heavenly_justice.countdown matches 1.. run scoreboard players remove @s wk.heavenly_justice.countdown 1
