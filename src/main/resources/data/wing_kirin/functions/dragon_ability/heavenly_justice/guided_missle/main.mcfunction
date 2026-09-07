## 执行者：标志实体
## 1.20.1 移植版：@n 为 1.20.2+ 选择器，用 @e[...,limit=1,sort=nearest] 等价替代

# 生成制导
function wing_kirin:dragon_ability/heavenly_justice/guided_missle/summon_missle

# 向导弹传入发射者UUID
data modify entity @e[type=dragonsurvival:generic_ball_entity,tag=new_summon,distance=..1,limit=1,sort=nearest] Owner set from entity @s data.Owner

# 向导弹传入技能等级（弹射物等级）
data modify entity @e[type=dragonsurvival:generic_ball_entity,tag=new_summon,distance=..1,limit=1,sort=nearest] projectile_level set from entity @s data.projectile_level

# 进行导弹部署
execute as @e[type=dragonsurvival:generic_ball_entity,tag=new_summon,distance=..1,limit=1,sort=nearest] at @s run function wing_kirin:dragon_ability/heavenly_justice/guided_missle/setting

## 制导导弹已发射！
