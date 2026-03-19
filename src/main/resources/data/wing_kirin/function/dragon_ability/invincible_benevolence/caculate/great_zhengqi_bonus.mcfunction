# 由于计分板不能存储小数，故先乘以12再除以10以达成乘以1.2的效果
scoreboard players operation @s wk.invincible_benevolence.beneficiary_amount *= #12 wk.math

scoreboard players operation @s wk.invincible_benevolence.beneficiary_amount /= #10 wk.math