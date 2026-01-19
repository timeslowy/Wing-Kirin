## 初始击中
# 获取自定义弹射物的弹射物等级（与技能对应，该数据来源于龙生），并存入定身计时栏当中
execute store result score @s wk.stasis_hex.freezeTimer-arrow \
    run data get entity @n[type=dragonsurvival:generic_arrow_entity,tag=stasia_hex-ding] projectile_level

# 匹配等级设置时长（⚠警告：如果想要设置的持续时长与预先存入的（弹射物等级）有重合，请另外新建计分板！否则会match成功直接运行错误的时长！）
# scoreboard players set:设置以玩家才能更改数值,objective不行
execute if score @s wk.stasis_hex.freezeTimer-arrow matches 1 run scoreboard players add @s wk.stasis_hex.freezeTimer-arrow 200
execute if score @s wk.stasis_hex.freezeTimer-arrow matches 2 run scoreboard players add @s wk.stasis_hex.freezeTimer-arrow 300
execute if score @s wk.stasis_hex.freezeTimer-arrow matches 3 run scoreboard players add @s wk.stasis_hex.freezeTimer-arrow 400

# 限制时长：1200刻（60秒）
execute if score @s wk.stasis_hex.freezeTimer-arrow matches 1201.. run scoreboard players set @s wk.stasis_hex.freezeTimer-arrow 1200

# 给被击中实体增加“定身”标签
tag @s add being_frozen-arrow

# 应用效果
function wing_kirin:dragon_ability/stasia_hex/apply/main