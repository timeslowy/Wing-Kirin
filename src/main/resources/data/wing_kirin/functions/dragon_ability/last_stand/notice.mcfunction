# 标题显示
title @s title {"translate": "title.wing_kirin.last_stand","color": "#fbdc92"}
title @s subtitle {"translate": "subtitle.wing_kirin.last_stand","bold": true}

# 播放声音
playsound minecraft:item.totem.use player @s

# 授予玩家进度
execute if entity @s[advancements={wing_kirin:wing_kirin/delay_death=false}] run advancement grant @s only wing_kirin:wing_kirin/delay_death

# 增加“触发回光返照”统计数据值
wk-stats add @s wing_kirin:triggered_last_stand_times 1