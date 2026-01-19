## 执行者：自己
# 给路径沿线的实体施加效果
execute as @e[distance=..5,predicate=!wing_kirin:wing_kirin,predicate=wing_kirin:is_living_entity] run \
    effect give @s slowness 10 1

execute as @e[distance=..5,predicate=!wing_kirin:wing_kirin,predicate=wing_kirin:is_living_entity] run \
    effect give @s weakness 10 1
