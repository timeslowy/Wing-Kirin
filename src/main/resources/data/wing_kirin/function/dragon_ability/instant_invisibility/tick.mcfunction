## 执行者：instant_invisibility（标志实体）
## 执行位置：执行者

# 增加标签以供选择器选择
tag @s add instant_invisibility-select

# 每刻减少积分版1
execute if score @s wk.instant_invisibility.duration matches 1.. run scoreboard players remove @s wk.instant_invisibility.duration 1

# 检测当标志实体的持续时间归零时，移除所有者（UUID确认）的计分板（空），使得伤害调整失效
$execute if score @s wk.instant_invisibility.duration matches 0 run scoreboard players reset $(Owner_hex) wk.instant_invisibility.remove_check

# 标志实体计分板归零时自毙以移除效果
execute if score @s wk.instant_invisibility.duration matches 0 run kill @s

# 检测当标志实体所有者距离不在30之内产生超距移除效果
#（注意：由于没有使用 at @s，导致执行位置仍然在标志实体处，因此可以判断距离，且精确删除标志实体）
$execute as $(Owner_hex) unless entity @s[distance=..25] run function wing_kirin:dragon_ability/instant_invisibility/destruct/distance_out

# 当玩家使出“破隐一击”时删除标志实体
$execute as $(Owner_hex) unless score @s wk.instant_invisibility.remove_check matches 1 run kill @n[type=marker,tag=instant_invisibility-select]

# 当玩家死亡时清除属于此玩家的标志实体
$execute as $(Owner_hex) if score @s wk.death_check matches 1.. run kill @n[type=marker,tag=instant_invisibility-select]

# 提示失效距离
execute as @s run function wing_kirin:dragon_ability/instant_invisibility/distance_notice/main with entity @s data

# 移除标签防止重选
tag @s remove instant_invisibility-select