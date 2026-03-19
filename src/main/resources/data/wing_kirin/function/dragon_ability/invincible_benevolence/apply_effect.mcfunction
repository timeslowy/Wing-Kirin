# 给予施法者相应时长的抗性提升Ⅴ
$effect give @s resistance $(beneficiary_amount) 4

# 重置计分板
scoreboard players reset @s wk.invincible_benevolence.beneficiary_amount

# 粒子效果
function wing_kirin:dragon_ability/invincible_benevolence/particle