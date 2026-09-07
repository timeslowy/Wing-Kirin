## 执行者：施法者
## 由 message/successful_message 通过配对 id 反查后调用

# 显示发射成功消息
title @s actionbar [{"translate": "actionbar.wing_kirin.ability.heavenly_justice.successful_launched.notification", "bold": true, "color": "#fbdc92"},{"nbt": "heavenly_justice.Pos", "storage": "wing_kirin:ram", "color": "#d11111"}]

# 重置玩家计分板
scoreboard players reset @s wk.heavenly_justice.countdown

# 播放成功音效
execute at @s run playsound entity.arrow.hit_player player @s

# 增加“触发天降正义”统计数据值
# TODO wk-stats add @s wing_kirin:triggered_heavenly_justice_times 1（统计命令随后续批次移植）
