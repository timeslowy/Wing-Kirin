## 执行者：标志实体
# 给标志实体上计时器
execute unless score @s wk.heavenly_justice.countdown matches 0.. \
    run scoreboard players set @s wk.heavenly_justice.countdown 400 

# 将命令存储的数据传入标志实体
execute if score @s wk.heavenly_justice.countdown matches 400 run function wing_kirin:dragon_ability/heavenly_justice/ability_data/marker

# 对打击地点进行标记
execute if score @s wk.heavenly_justice.countdown matches 400 at @s run function wing_kirin:dragon_ability/heavenly_justice/position_display/main

# 输出成功定位消息
execute if score @s wk.heavenly_justice.countdown matches 400 at @s \
    run function wing_kirin:dragon_ability/heavenly_justice/message/successful_message with entity @s data

# 改换颜色
execute if score @s wk.heavenly_justice.countdown matches 100 at @s run function wing_kirin:dragon_ability/heavenly_justice/position_display/color_change

# 开始导弹部署
execute if score @s wk.heavenly_justice.countdown matches 40 at @s run function wing_kirin:dragon_ability/heavenly_justice/guided_missle/main

# 处理计时器
execute if score @s wk.heavenly_justice.countdown matches 1.. run scoreboard players remove @s wk.heavenly_justice.countdown 1