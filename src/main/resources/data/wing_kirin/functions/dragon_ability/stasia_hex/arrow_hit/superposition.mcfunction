## 叠加机制：每次此生物被箭击定身可依等级增加不同时长，有上限：1200刻（60秒）
# 1.20.1 移植：@n → @e[limit=1,sort=nearest]；storage 中转 → wk.stasis_hex.temp 计分板中转

# 获取自定义弹射物的弹射物等级（与技能对应，该数据来源于龙生），并存入临时计分板
execute store result score @s wk.stasis_hex.temp run data get entity @e[type=dragonsurvival:generic_arrow_entity,tag=stasia_hex-ding,limit=1,sort=nearest] projectile_level

# 叠加机制2：匹配等级设置叠加时长
# 1级+100刻（5秒）
execute if score @s wk.stasis_hex.temp matches 1 run scoreboard players add @s wk.stasis_hex.freezeTimer 100
# 2级+150刻（7.5秒）
execute if score @s wk.stasis_hex.temp matches 2 run scoreboard players add @s wk.stasis_hex.freezeTimer 150
# 3级+200刻（10秒）
execute if score @s wk.stasis_hex.temp matches 3 run scoreboard players add @s wk.stasis_hex.freezeTimer 200

# 重置临时计分板
scoreboard players reset @s wk.stasis_hex.temp

# 限制时长：1200刻（60秒）
execute if score @s wk.stasis_hex.freezeTimer matches 1201.. run scoreboard players set @s wk.stasis_hex.freezeTimer 1200

# 给被击中实体增加"定身"标签
tag @s add being_frozen

# 应用效果
function wing_kirin:dragon_ability/stasia_hex/apply/set_duration

# 满定身限制提示
execute if score @s wk.stasis_hex.freezeTimer matches 1200.. run function wing_kirin:dragon_ability/stasia_hex/display/max_district_notice
