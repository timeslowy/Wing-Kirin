## 鼓励奖池（对应31-100）
# 执行者：玩家
# 15%（31-45）获得400点经验
execute if score @s wk.ability_add.random_value matches 31..45 run function wing_kirin:wing_kirin_upgrade/other_award/get_xp

# 15%（46-60）获得 金锭
execute if score @s wk.ability_add.random_value matches 46..60 run function wing_kirin:wing_kirin_upgrade/other_award/gold_ingot

# 15%（61-75）获得 古龙之心
execute if score @s wk.ability_add.random_value matches 61..75 run function wing_kirin:wing_kirin_upgrade/other_award/elder_dragon_heart

# 10%（76-85）获得 金风玉露
execute if score @s wk.ability_add.random_value matches 76..85 run function wing_kirin:wing_kirin_upgrade/other_award/empyrean_wine

# 8%（86-93）获得 钻石
execute if score @s wk.ability_add.random_value matches 86..93 run function wing_kirin:wing_kirin_upgrade/other_award/diamond

# 5%（94-98）获得 下界之星
execute if score @s wk.ability_add.random_value matches 94..98 run function wing_kirin:wing_kirin_upgrade/other_award/nether_star

# 2%（99-100）获得 下界合金锭
execute if score @s wk.ability_add.random_value matches 99..100 run function wing_kirin:wing_kirin_upgrade/other_award/nether_ingot