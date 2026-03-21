# 给予施法者相应时长的抗性提升Ⅴ
$effect give @s resistance $(beneficiary_amount) 4

# 单次使3名以上之盟友受惠则授予「仁者无敌」进度
execute if entity @s[advancements={wing_kirin:wing_kirin/invincible_benevolence=false}] \
    if score @s wk.invincible_benevolence.beneficiary_amount matches 3.. run \
        advancement grant @s only wing_kirin:wing_kirin/invincible_benevolence

# 显示惠及数 
title @s actionbar [{"translate": "actionbar.wing_kirin.ability.invincible_benevolence.beneficiary_amount"},\
    {"score": {"name": "@s","objective": "wk.invincible_benevolence.beneficiary_amount"},"color":"green"}]

# 重置计分板
scoreboard players reset @s wk.invincible_benevolence.beneficiary_amount

# 粒子效果
function wing_kirin:dragon_ability/invincible_benevolence/particle