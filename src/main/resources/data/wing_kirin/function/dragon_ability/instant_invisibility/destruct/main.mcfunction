## 执行者：施法者
## 执行位置：标志实体
## 玩家行为导致的提前移除类

# 检测当标志实体所有者距离不在30之内产生超距移除效果
#（注意：由于没有使用 at @s，导致执行位置仍然在标志实体处，因此可以判断距离，且精确删除标志实体）
execute unless entity @s[distance=..25] run scoreboard players reset @s wk.instant_invisibility.remove_check

# 非死亡清除（统一由计分板触发）
execute unless score @s wk.instant_invisibility.remove_check matches 1 run function wing_kirin:dragon_ability/instant_invisibility/destruct/common_remove

# 当玩家死亡
execute if score @s wk.death_check matches 1.. run function wing_kirin:dragon_ability/instant_invisibility/destruct/common_remove
