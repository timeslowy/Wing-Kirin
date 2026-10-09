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

# 玩家行为导致的失效类
$execute as $(Owner_hex) run function wing_kirin:dragon_ability/instant_invisibility/destruct/main

# 提示失效距离
function wing_kirin:dragon_ability/instant_invisibility/distance_notice/main with entity @s data

# 移除标签防止重选
tag @s remove instant_invisibility-select