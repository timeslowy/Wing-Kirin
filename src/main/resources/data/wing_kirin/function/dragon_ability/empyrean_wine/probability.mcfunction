# 将玩家技能等级存至命令存储格式
execute store result storage wing_kirin:ram empyrean_wine.level int 1 run dragon-ability query @s dragonsurvival:empyrean_wine level

# 根据等级设置参数
# 1级：1%
execute if data storage wing_kirin:ram {empyrean_wine:{level:1}} \
    if predicate wing_kirin:1_percent \
    run return run function wing_kirin:dragon_ability/empyrean_wine/modify_item
# 2级：3%
execute if data storage wing_kirin:ram {empyrean_wine:{level:2}} \
    if predicate wing_kirin:3_percent \
    run return run function wing_kirin:dragon_ability/empyrean_wine/modify_item
# 3级：5%
execute if data storage wing_kirin:ram {empyrean_wine:{level:3}} \
    if predicate wing_kirin:5_percent \
    run return run function wing_kirin:dragon_ability/empyrean_wine/modify_item
# 3级：7%
execute if data storage wing_kirin:ram {empyrean_wine:{level:4}} \
    if predicate wing_kirin:7_percent \
    run return run function wing_kirin:dragon_ability/empyrean_wine/modify_item
# 3级：9%
execute if data storage wing_kirin:ram {empyrean_wine:{level:5}} \
    if predicate wing_kirin:9_percent \
    run return run function wing_kirin:dragon_ability/empyrean_wine/modify_item

# 若拥有「浩然正气」药水效果，则使得保底时间缩短为20秒
execute if data entity @s active_effects[{id:"wing_kirin:great_zhengqi"}] if score @s wk.empyrean_wine.attempt_count matches 20.. \
    run return run function wing_kirin:dragon_ability/empyrean_wine/modify_item

# 大保底,30秒必定成功一次
execute if score @s wk.empyrean_wine.attempt_count matches 30.. run function wing_kirin:dragon_ability/empyrean_wine/modify_item