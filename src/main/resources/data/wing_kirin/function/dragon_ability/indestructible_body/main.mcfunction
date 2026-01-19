## 当《不坏金身》施法期间受攻击执行
## 执行者：玩家

# 播放反撞音
playsound block.bell.use player @a[distance=..20]

# 自身产生反撞粒子特效
particle dust{color:[0.98,0.86,0.57],scale:4.0} ~ ~ ~ 0.5 0.5 0.5 1 50

# 给方圆五格内的实体产生受击粒子特效
execute as @e[distance=..5] at @s run particle minecraft:crit ~ ~ ~ 1 0.5 1 1 50

# 给方圆五格内的实体造成伤害
execute as @e[distance=..5] run damage @s 4 dragonsurvival:counter_shock \
    by @p[predicate=wing_kirin:wing_kirin] from @p[predicate=wing_kirin:wing_kirin]

# 反震次数计分板+1
scoreboard players add @s wk.indestructible_body.counter_shock_count 1

# 剥夺进度使其可反复触发
advancement revoke @s only wing_kirin:function/indestructible_body_casting_effect

