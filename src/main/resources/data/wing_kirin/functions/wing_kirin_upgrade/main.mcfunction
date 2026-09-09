## 通仙心主函数，由进度触发器触发函数
## 执行者：玩家
# 剥夺进度，使得该进度可以反复触发
advancement revoke @s only wing_kirin:function/consume_wing_kirin_upgrade

# 不是对应的种族直接退出
# （1.20.1 无 /return fail，也不存在 neoforge:attachments；种族守卫已由进度的 dragon_predicate 条件承担，故保持注释）
# execute unless data entity @s {"neoforge:attachments":{"dragonsurvival:magic_data":{current_species:"dragonsurvival:wing_kirin"}}} run return fail
# 1.20.1 等价写法：execute unless predicate wing_kirin:wing_kirin run return 0

# 判断当前（主手）使用的物品是否为“通仙心”，是的话运行下列函数
# （1.20.1 无 `items entity ... weapon.*`（1.20.5+）；进度已按“食用 wing_kirin:wing_kirin_upgrade”触发，此守卫冗余，故保持注释）
# execute unless items entity @s weapon.* wing_kirin:wing_kirin_upgrade run return fail

# 生成1-100的随机数（包含1和100）
# 1.20.1 移植：无 /random value（1.20.2+）。改用「新建 marker 的随机 UUID[0] 取模 100 再 +1」：
#   · /summon 生成的实体 UUID 为随机 UUID，其高 32 位（UUID[0]）均匀随机；
#   · 1.20.1 计分板 %= 走 Mth.positiveModulo，结果恒为 0..99，无需处理负数。
execute at @s run summon minecraft:marker ~ ~ ~ {Tags:["wk.rng"]}
execute store result score @s wk.ability_add.random_value run data get entity @e[type=minecraft:marker,tag=wk.rng,limit=1,sort=nearest] UUID[0]
kill @e[type=minecraft:marker,tag=wk.rng]
scoreboard players operation @s wk.ability_add.random_value %= #100 wk.math
scoreboard players add @s wk.ability_add.random_value 1

# 匹配1-30（30%）进入后天技能池分配
execute if score @s wk.ability_add.random_value matches 1..30 run function wing_kirin:wing_kirin_upgrade/acquired_ability_pool/main

# 匹配31-100（70%），进入鼓励奖池分配
# （1.20.1 无 /return run；原句仅为提前返回，而这是最后一条指令，直接 run function 等价）
execute unless score @s wk.ability_add.random_value matches 1..30 run function wing_kirin:wing_kirin_upgrade/other_award/main
