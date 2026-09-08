## 在 定身术 范围施法分支下执行
## 执行者: 自己
## 执行位置：自己
# 1.20.1 移植：storage 中转 → wk.stasis_hex.temp 计分板中转

# 给自己和乘客/载具加标签来排除
tag @s add caster
execute on passengers run tag @s add caster
execute on vehicle run tag @s add caster

# 储存玩家的技能等级至临时计分板
execute store result score @s wk.stasis_hex.temp run dragon-ability query @s wing_kirin:stasis_hex level

# 依据等级增加标签的距离，用于范围定身
# 1级：半径8
execute if score @s wk.stasis_hex.temp matches 1 run tag @e[tag=!caster,predicate=wing_kirin:is_living_entity,distance=..8] add stasis_hex-area

# 2级：半径14
execute if score @s wk.stasis_hex.temp matches 2 run tag @e[tag=!caster,predicate=wing_kirin:is_living_entity,distance=..14] add stasis_hex-area

# 3级：半径20
execute if score @s wk.stasis_hex.temp matches 3 run tag @e[tag=!caster,predicate=wing_kirin:is_living_entity,distance=..20] add stasis_hex-area

# 重置临时计分板
scoreboard players reset @s wk.stasis_hex.temp

# 移除标签
tag @s remove caster
execute on passengers run tag @s remove caster
execute on vehicle run tag @s remove caster
