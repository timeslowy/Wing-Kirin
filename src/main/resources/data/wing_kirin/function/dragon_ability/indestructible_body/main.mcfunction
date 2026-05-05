## 当《不坏金身》施法期间受攻击执行
## 执行者：玩家

# 播放反撞音
playsound block.bell.use player @a[distance=..20]

# 自身产生反撞粒子特效
particle dust{color:[0.98,0.86,0.57],scale:4.0} ~ ~ ~ 0.5 0.5 0.5 1 50

# 通过属性巨龙技能伤害数值计算伤害
execute store result storage wing_kirin:ram indestructible_body.countershock_damage float 1 \
    run attribute @s dragonsurvival:dragon_ability_damage get 4

# 伤害流程
execute as @e[distance=0.1..5] run function wing_kirin:dragon_ability/indestructible_body/damage with storage wing_kirin:ram indestructible_body

# 粒子效果
particle wing_kirin:thunderous_shout ~ ~ ~ 0.0 0.0 0.0 0 1

# 反震次数计分板+1
scoreboard players add @s wk.indestructible_body.counter_shock_count 1

# 单次反震超过10次授予「金身不坏」进度
execute if entity @s[advancements={wing_kirin:wing_kirin/indestructible_body=false}] \
    if score @s wk.indestructible_body.counter_shock_count matches 10.. \
        run advancement grant @s only wing_kirin:wing_kirin/indestructible_body

# 增加“不坏金身反震次数”统计数据值
wk-stats add @s wing_kirin:indestructible_body_countershock_times 1

# 剥夺进度使其可反复触发
advancement revoke @s only wing_kirin:function/indestructible_body_casting_effect

