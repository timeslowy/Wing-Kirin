## 执行者：玩家
# 每刻减少玩家的此技能运行标志
execute if score @s wk.indestructible_body.working_symbol matches ..25 \ 
   run return run scoreboard players add @s wk.indestructible_body.working_symbol 1

# 当运行标志达到运行上限时重置计分板
execute if score @s wk.indestructible_body.working_symbol matches 26 run function wing_kirin:dragon_ability/indestructible_body/reset