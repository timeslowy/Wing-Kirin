## 执行者：标志实体

# 生成制导
function wing_kirin:dragon_ability/heavenly_justice/guided_missle/summon_missle

# 向导弹传入发射者UUID
data modify entity @n[type=dragonsurvival:generic_ball_entity,tag=new_summon,distance=..1] Owner set from entity @s data.Owner
# 向导弹传入技能等级（弹射物等级）
data modify entity @n[type=dragonsurvival:generic_ball_entity,tag=new_summon,distance=..1] projectile_level set from entity @s data.projectile_level

# 进行导弹部署
execute as @n[type=dragonsurvival:generic_ball_entity,tag=new_summon] at @s run function wing_kirin:dragon_ability/heavenly_justice/guided_missle/setting


