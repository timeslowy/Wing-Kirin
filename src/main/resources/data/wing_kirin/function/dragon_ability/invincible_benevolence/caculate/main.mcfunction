# （如果达到上限）将范围内所有生物的相应标签移去
execute as @e[distance=..9,tag=beneficiary] run tag @s remove beneficiary

# 将玩家技能等级存至命令存储格式
execute store result storage wing_kirin:ram invincible_benevolence.level int 1 \
    run dragon-ability query @s dragonsurvival:invincible_benevolence level

# 如果拥有「浩然正气」效果则增加20%的相对受惠实体数
execute if data entity @s active_effects[{id:"wing_kirin:great_zhengqi"}] \
    run function wing_kirin:dragon_ability/invincible_benevolence/caculate/great_zhengqi_bonus

# 根据等级设置每个受惠实体可给予的抗性提升时长参数
function wing_kirin:dragon_ability/invincible_benevolence/caculate/ability_level

# 根据被治疗实体数应用效果
function wing_kirin:dragon_ability/invincible_benevolence/apply_effect with storage wing_kirin:ram invincible_benevolence