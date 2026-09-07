## 执行者：生成的标志实体
## 1.20.1 移植版：@n 为 1.20.2+ 选择器，用 @e[...,limit=1,sort=nearest] 等价替代

# 生成粒子
particle minecraft:crit ~ ~1 ~ 1 0 1 0 50 force
particle campfire_signal_smoke ~ ~1 ~ 0 1 0 0.1 20 force

# 调整其旋转
data merge entity @e[type=item_display,tag=heavenly_justice_marker_display,limit=1,sort=nearest] {transformation:{right_rotation:[0,0,1,1]}}

# 调整位置
tp @e[type=item_display,tag=heavenly_justice_marker_display,limit=1,sort=nearest] ~ ~0.5 ~
