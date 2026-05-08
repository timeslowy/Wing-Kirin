## 执行者：玩家
# 每刻减少玩家的此技能运行标志
execute if score @s wk.empyrean_wine.working_symbol matches 1.. run return run scoreboard players remove @s wk.empyrean_wine.working_symbol 1

# 当运行标志归零时重置计分板
execute if score @s wk.empyrean_wine.working_symbol matches 0 run function wing_kirin:dragon_ability/empyrean_wine/reset