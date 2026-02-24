## 反震伤害流程
## 执行者：施法者方圆五格内的实体
## 执行位置：施法者

# 给方圆五格内的实体产生受击粒子特效
execute at @s run particle minecraft:crit ~ ~ ~ 1 0.5 1 1 50

# 给方圆五格内的实体造成伤害
$damage @s $(countershock_damage) wing_kirin:counter_shock \
    by @p[predicate=wing_kirin:wing_kirin] from @p[predicate=wing_kirin:wing_kirin]