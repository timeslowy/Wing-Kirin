## 执行者：施法者
## 执行位置：标志实体
## 玩家行为导致的提前移除类

# 检测当标志实体所有者距离不在30之内产生超距移除效果
#（注意：由于没有使用 at @s，导致执行位置仍然在标志实体处，因此可以判断距离，且精确删除标志实体）
execute unless entity @s[distance=..25] run function wing_kirin:dragon_ability/instant_invisibility/destruct/distance_out

# 当玩家使出“破隐一击”时删除标志实体
execute unless score @s wk.instant_invisibility.remove_check matches 1 run kill @n[type=marker,tag=instant_invisibility-select]

# 当玩家死亡时清除属于此玩家的标志实体
execute if score @s wk.death_check matches 1.. run kill @n[type=marker,tag=instant_invisibility-select]