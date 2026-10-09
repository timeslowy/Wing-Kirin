## 初始击中（统一计时器 freezeTimer）
# 获取施法者的技能等级，并存入定身计时栏当中
# 龙生的技能查询指令的直接目标选择器不支持十六进制UUID！！
$execute as $(out) store result storage wing_kirin:ram stasis_hex.level int 1 run dragon-ability query @s wing_kirin:stasis_hex level
execute store result score @s wk.stasis_hex.freezeTimer run data get storage wing_kirin:ram stasis_hex.level

# 匹配等级设置时长（⚠警告：如果想要设置的持续时长与预先存入的（弹射物等级）有重合，请另外新建计分板！否则会match成功直接运行错误的时长！）
execute if score @s wk.stasis_hex.freezeTimer matches 1 run scoreboard players add @s wk.stasis_hex.freezeTimer 200
execute if score @s wk.stasis_hex.freezeTimer matches 2 run scoreboard players add @s wk.stasis_hex.freezeTimer 300
execute if score @s wk.stasis_hex.freezeTimer matches 3 run scoreboard players add @s wk.stasis_hex.freezeTimer 400

# 给被击中实体增加"定身"标签
tag @s add being_frozen

# 应用效果
function wing_kirin:dragon_ability/stasia_hex/apply/set_duration