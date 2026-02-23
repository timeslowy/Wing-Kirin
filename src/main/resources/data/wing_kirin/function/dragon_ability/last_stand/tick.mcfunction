## 执行者：所有触发过回光返照的玩家
# 倒计时归零则自毙
execute if score @s wk.last_stand.death_countdown matches 0 run return \
    run function wing_kirin:dragon_ability/last_stand/kill

# 处理计时器
execute if score @s wk.last_stand.death_countdown matches 1.. run scoreboard players remove @s wk.last_stand.death_countdown 1