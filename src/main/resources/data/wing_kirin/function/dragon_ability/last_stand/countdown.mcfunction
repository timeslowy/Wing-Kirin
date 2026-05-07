## 计算倒计时

# 将玩家的技能等级存入命令存储
execute store result storage wing_kirin:ram last_stand.level int 1 run dragon-ability query @s wing_kirin:last_stand level

# 根据传入的等级设置标分版分数来达到计时效果
execute store result score @s wk.last_stand.death_countdown run data get storage wing_kirin:ram last_stand.level 800

# 若拥有「浩然正气」药水效果则多20%时间(乘12再除以10，为乘1.2)
execute if data entity @s active_effects[{id:"wing_kirin:great_zhengqi"}] run scoreboard players operation @s wk.last_stand.death_countdown *= #12 wk.math
execute if data entity @s active_effects[{id:"wing_kirin:great_zhengqi"}] run scoreboard players operation @s wk.last_stand.death_countdown /= #10 wk.math