## 执行者：所有触发过回光返照的玩家
# 倒计时归零则自毙（此为穿甲伤害，不会损坏甲，放心）
execute if score @s wk.last_stand.death_countdown matches 0 run return \
    run damage @s 99999999999999999999999999999999999999 dragonsurvival:breath_disappear

# 处理计时器
execute if score @s wk.last_stand.death_countdown matches 1.. run scoreboard players remove @s wk.last_stand.death_countdown 1