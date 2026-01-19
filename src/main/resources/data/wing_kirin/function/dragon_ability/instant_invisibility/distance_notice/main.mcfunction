## 提示失效距离
## 执行者：标志实体
## 执行位置：标志实体
# 若玩家未移动则不显示
$execute as $(Owner_hex) unless predicate wing_kirin:is_moving run return fail

# 绿色
$execute as $(Owner_hex) if entity @s[distance=1..10.99] run return \
    run function wing_kirin:dragon_ability/instant_invisibility/distance_notice/green_25-15

# 黄色
$execute as $(Owner_hex) if entity @s[distance=11..19.99] run return \
    run function wing_kirin:dragon_ability/instant_invisibility/distance_notice/yellow_14-6

# 红色
$execute as $(Owner_hex) if entity @s[distance=20..24.99] run return \
    run function wing_kirin:dragon_ability/instant_invisibility/distance_notice/red_5-1


