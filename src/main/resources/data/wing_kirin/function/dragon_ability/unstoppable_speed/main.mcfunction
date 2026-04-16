## 执行者：玩家
# 攻击计数加1
scoreboard players add @s wk.unstoppable_speed.attack_count 1

# 单次达到100授予进度
execute if entity @s[advancements={wing_kirin:wing_kirin/unstoppable_speed=false}] \
    if score @s wk.unstoppable_speed.attack_count matches 100.. run advancement grant @s only wing_kirin:wing_kirin/unstoppable_speed

# 剥夺进度使其可重复触发
advancement revoke @s only wing_kirin:function/unstoppable_speed_attack_check

