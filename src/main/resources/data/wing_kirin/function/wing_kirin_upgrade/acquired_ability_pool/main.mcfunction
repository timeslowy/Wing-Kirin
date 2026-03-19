## 后天技能池
# 执行者：玩家
# 检查12%（1-12）概率执行函数A （获得技能：一支穿云箭）
execute if score @s wk.ability_add.random_value matches 1..12 run return \
    run function wing_kirin:wing_kirin_upgrade/acquired_ability_pool/signal_arrow

# 检查8%（13-20）概率执行函数B （获得技能：聚形散气）
execute if score @s wk.ability_add.random_value matches 13..20 run return \
    run function wing_kirin:wing_kirin_upgrade/acquired_ability_pool/instant_invisibility

# 如果上一步未成功，检查6%（21-26）概率执行函数C （获得技能：仁者无敌）
execute if score @s wk.ability_add.random_value matches 21..26 run return \
    run function wing_kirin:wing_kirin_upgrade/acquired_ability_pool/invincible_benevolence

# 如果上两步未成功，检查3%（27-29）概率执行函数D（获得技能：回光返照）
execute if score @s wk.ability_add.random_value matches 27..29 run return \
    run function wing_kirin:wing_kirin_upgrade/acquired_ability_pool/last_stand

# 如果上两步未成功，检查1%（30）概率执行函数E（获得技能：天降正义）
execute if score @s wk.ability_add.random_value matches 30 run return \
    run function wing_kirin:wing_kirin_upgrade/acquired_ability_pool/heavenly_justice