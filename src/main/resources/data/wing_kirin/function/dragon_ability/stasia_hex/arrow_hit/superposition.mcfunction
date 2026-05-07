## 叠加机制：每次此生物被箭击定身可依等级增加不同时长，有上限：1200刻（60秒）
# 获取自定义弹射物的弹射物等级（与技能对应，该数据来源于龙生），并存入命令存储
execute store result storage wing_kirin:ram stasis_hex.level int 1 \
    run data get entity @n[type=dragonsurvival:generic_arrow_entity,tag=stasia_hex-ding] projectile_level

# 叠加机制2：匹配等级设置叠加时长
# 1级+100刻（5秒）
execute if data storage wing_kirin:ram {stasis_hex:{level:1}} \
    run scoreboard players add @s wk.stasis_hex.freezeTimer 100
# 2级+150刻（7.5秒）
execute if data storage wing_kirin:ram {stasis_hex:{level:2}} \
     run scoreboard players add @s wk.stasis_hex.freezeTimer 150
# 3级+200刻（10秒）
execute if data storage wing_kirin:ram {stasis_hex:{level:3}} \
    run scoreboard players add @s wk.stasis_hex.freezeTimer 200

# 限制时长：1200刻（60秒）
execute if score @s wk.stasis_hex.freezeTimer matches 1201.. run scoreboard players set @s wk.stasis_hex.freezeTimer 1200

# 给被击中实体增加"定身"标签
tag @s add being_frozen

# 应用效果
function wing_kirin:dragon_ability/stasia_hex/apply/set_duration


