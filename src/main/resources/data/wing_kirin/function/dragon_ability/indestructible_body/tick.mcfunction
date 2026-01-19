## 执行者：玩家
# 每刻减少玩家的此技能运行标志
execute if score @s wk.indestructible_body.working_symbol matches ..25 \ 
   run return run scoreboard players add @s wk.indestructible_body.working_symbol 1

# 当运行标志达到运行上限时重置两个计分板，按顺序：1.反震次数；2.运行标志
execute if score @s wk.indestructible_body.working_symbol matches 26 run scoreboard players reset @s wk.indestructible_body.counter_shock_count
execute if score @s wk.indestructible_body.working_symbol matches 26 run scoreboard players reset @s wk.indestructible_body.working_symbol