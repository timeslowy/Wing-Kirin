## 初始执行者：标志实体

# 显示发射成功消息
$execute as $(hex_Owner) run title @s actionbar [{"translate": "actionbar.wing_kirin.ability.heavenly_justice.successful_launched.notification", \
    "bold": true,"color":"#fbdc92"},{"text":"$(Pos)","color": "#d11111"}]

# 重置玩家计分板
$execute as $(hex_Owner) run scoreboard players reset @s wk.heavenly_justice.countdown

# 播放成功音效
$execute as $(hex_Owner) at @s run playsound entity.arrow.hit_player player @s
