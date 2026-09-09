## 后天技能池
# 执行者：玩家
# 1.20.1 移植版：无 /return run（1.20.2+）且无 \ 换行。
# 原 return 仅用于“命中即提前返回”，而 6 个区间互不相交，
# 故顺序执行即可——命中区间后其余 if 自然不成立，语义完全等价。

# 检查8%（1-8）概率执行函数A （获得技能：一支穿云箭）
execute if score @s wk.ability_add.random_value matches 1..8 run function wing_kirin:wing_kirin_upgrade/acquired_ability_pool/signal_arrow

# 检查7%（9-15）概率执行函数B （获得技能：聚形散气）
execute if score @s wk.ability_add.random_value matches 9..15 run function wing_kirin:wing_kirin_upgrade/acquired_ability_pool/instant_invisibility

# 检查6%（16-21）概率执行函数C （获得技能：仁者无敌）
execute if score @s wk.ability_add.random_value matches 16..21 run function wing_kirin:wing_kirin_upgrade/acquired_ability_pool/invincible_benevolence

# 检查5%（22-26）概率执行函数C （获得技能：唯快不破）
execute if score @s wk.ability_add.random_value matches 22..26 run function wing_kirin:wing_kirin_upgrade/acquired_ability_pool/unstoppable_speed

# 检查3%（27-29）概率执行函数D（获得技能：回光返照）
execute if score @s wk.ability_add.random_value matches 27..29 run function wing_kirin:wing_kirin_upgrade/acquired_ability_pool/last_stand

# 检查1%（30）概率执行函数E（获得技能：天降正义）
execute if score @s wk.ability_add.random_value matches 30 run function wing_kirin:wing_kirin_upgrade/acquired_ability_pool/heavenly_justice
