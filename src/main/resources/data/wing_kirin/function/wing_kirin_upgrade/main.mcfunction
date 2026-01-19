## 通仙心主函数，由进度触发器触发函数
## 执行者：玩家
# 剥夺进度，使得该进度可以反复触发
advancement revoke @s only wing_kirin:function/consume_wing_kirin_upgrade

# 不是对应的种族直接退出
execute unless data entity @s {"neoforge:attachments":{"dragonsurvival:magic_data":{current_species:"dragonsurvival:wing_kirin"}}} run return fail

# 判断当前（主手）使用的物品是否为“通仙心”，是的话运行下列函数
execute unless items entity @s weapon.* wing_kirin:wing_kirin_upgrade run return fail

# 生成1-100的随机数（包含1和100）
execute store result score @s wk.ability_add.random_value run random value 1..100

# 匹配1-30（30%）进入后天技能池分配
execute if score @s wk.ability_add.random_value matches 1..30 run function wing_kirin:wing_kirin_upgrade/acquired_ability_pool/main

# 匹配31-100（70%），进入鼓励奖池分配
execute unless score @s wk.ability_add.random_value matches 1..30 run return \
    run function wing_kirin:wing_kirin_upgrade/other_award/main
