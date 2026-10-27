#> wing_kirin:tick/heavenly_justice
# @executor server → marker（打点阶段）/ 翼麒麟玩家（定位阶段）
# @frequency tick
# @guard 首行：既无 heavenly_justice_marker、也无倒计时玩家时返回。
#         两分支合并为一处守卫，避免任一分支的提前 return 切断另一分支。
# @input marker data 与计分板 wk.heavenly_justice.countdown
# @output marker 倒计时每刻 -1；玩家倒计时每刻 -1，归零输出失败提示并清空
# @cleanup tag heavenly_justice_marker、tag heavenly_justice_marker_display、计分板 countdown
## 天降正义常驻入口：marker 计时（400 tick 生命周期）与发射者倒计时
execute unless entity @e[type=marker,tag=heavenly_justice_marker] unless entity @a[scores={wk.heavenly_justice.countdown=0..}] run return 0

# marker 分支：部署、显示与导弹投放
execute as @e[type=marker,tag=heavenly_justice_marker] run function wing_kirin:dragon_ability/heavenly_justice/tick-marker

# player 分支：定位失败提示与计时
execute as @a[predicate=wing_kirin:wing_kirin] if score @s wk.heavenly_justice.countdown matches 0.. run function wing_kirin:dragon_ability/heavenly_justice/tick-player
