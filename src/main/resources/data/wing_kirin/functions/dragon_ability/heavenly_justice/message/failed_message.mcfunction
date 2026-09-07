## 执行者：玩家
## 1.20.1 移植版：与 1.21.1 原样一致（无宏、无高版本语法）

# 显示定位失败消息
title @s actionbar {"translate": "actionbar.wing_kirin.ability.heavenly_justice.failed_launched.notification","bold": true,"color":"#d11111"}

# 播放音效
execute at @s run playsound block.beacon.deactivate player @s ~ ~ ~
