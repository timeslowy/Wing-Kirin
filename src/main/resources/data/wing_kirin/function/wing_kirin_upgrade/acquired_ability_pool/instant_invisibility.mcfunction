## 执行者：玩家
# 判断是否已拥有此技能
execute store success score @s wk.ability_add.has_ability run dragon-ability query @s dragonsurvival:instant_invisibility level

# 如果已拥有获得补偿
execute if score @s wk.ability_add.has_ability matches 1 run return run function wing_kirin:wing_kirin_upgrade/insurance/main

# 获得《聚形散气》技能
dragon-ability add @s dragonsurvival:instant_invisibility

# 动作栏显示获得信息
title @s actionbar [{"translate": "actionbar.wing_kirin.upgrade.describiton_0"}, \
    {"translate": "actionbar.wing_kirin.upgrade.instant_invisibility.describiton_1","color": "#fbdc92"}]

# 播放声音
playsound ui.toast.challenge_complete player @s