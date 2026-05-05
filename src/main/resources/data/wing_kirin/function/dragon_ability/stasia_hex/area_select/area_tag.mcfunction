## 在 定身术 范围施法分支下执行
## 执行者: 自己
## 执行位置：自己
# 给自己和乘客/载具加标签来排除
tag @s add caster
execute on passengers run tag @s add caster
execute on vehicle run tag @s add caster

# 储存玩家的技能等级至命令存储
execute store result storage wing_kirin:ram stasis_hex.level int 1 run dragon-ability query @s dragonsurvival:stasis_hex level

# 依据等级增加标签的距离，用于范围定身
# 1级：半径8
execute if data storage wing_kirin:ram {stasis_hex:{level:1}} \
     run tag @e[tag=!caster,predicate=wing_kirin:is_living_entity,distance=..8] add stasis_hex-area

# 2级：半径14
execute if data storage wing_kirin:ram {stasis_hex:{level:2}} \
     run tag @e[tag=!caster,predicate=wing_kirin:is_living_entity,distance=..14] add stasis_hex-area

# 3级：半径20
execute if data storage wing_kirin:ram {stasis_hex:{level:3}} \
     run tag @e[tag=!caster,predicate=wing_kirin:is_living_entity,distance=..20] add stasis_hex-area

# 移除标签
tag @s remove caster
execute on passengers run tag @s remove caster
execute on vehicle run tag @s remove caster




