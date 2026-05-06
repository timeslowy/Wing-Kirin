## 执行者：施法者
# 已统计完（未找到剩余的）所有的被治疗实体数则直接进入计算，终止循环
execute unless entity @e[distance=..9,tag=beneficiary] \
    run return run function wing_kirin:dragon_ability/invincible_benevolence/caculate/main

# 达到上限88个则亦终止循环
execute if score @s wk.invincible_benevolence.beneficiary_amount matches 88.. \
    run return run function wing_kirin:dragon_ability/invincible_benevolence/caculate/main

# 找到一定范围内被治疗实体数则加1
execute if entity @n[distance=..9,tag=beneficiary] run \
    scoreboard players add @s wk.invincible_benevolence.beneficiary_amount 1

# 增加“仁者无敌惠及友方总数”统计数据值
wk-stats add @s wing_kirin:cured_alies_count 1

# 并移除此实体的标签以防重复统计
tag @n[distance=..9,tag=beneficiary] remove beneficiary

# 循环
function wing_kirin:dragon_ability/invincible_benevolence/search_beneficiary
