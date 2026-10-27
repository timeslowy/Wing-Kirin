## 执行者：生成的标志实体
# 生成粒子
particle minecraft:crit ~ ~1 ~ 1 0 1 0 50 force
particle campfire_signal_smoke ~ ~1 ~ 0 1 0 0.1 20 force

# 调整其旋转
data merge entity @n[type=item_display,tag=heavenly_justice_marker_display] {transformation:{right_rotation:[0,0,1,1]}}

# 调整位置
tp @n[type=item_display,tag=heavenly_justice_marker_display] ~ ~0.5 ~




