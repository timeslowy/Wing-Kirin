## 执行者：玩家
# 1.20.1 移植版：无 /return run（1.20.2+）且无 \ 换行——补偿/授予两条路径改为
# if / unless 反转互斥（has_ability 分数在补偿前已存好，补偿函数不会改写它，顺序安全）；
# 返回值语义舍弃（当前无调用方消费）。

# 判断是否已拥有此技能（1=已拥有）
execute store success score @s wk.ability_add.has_ability run dragon-ability query @s wing_kirin:signal_arrow level

# 如果已拥有获得补偿
execute if score @s wk.ability_add.has_ability matches 1 run function wing_kirin:wing_kirin_upgrade/insurance/main

# 获得《一支穿云箭》技能（未拥有时）
execute unless score @s wk.ability_add.has_ability matches 1 run dragon-ability add @s wing_kirin:signal_arrow

# 动作栏显示获得信息（未拥有时）
execute unless score @s wk.ability_add.has_ability matches 1 run title @s actionbar [{"translate": "actionbar.wing_kirin.upgrade.describiton_0"}, {"translate": "actionbar.wing_kirin.upgrade.signal_arrow.describiton_1","color": "#fbdc92"}]

# 播放声音（未拥有时）
execute unless score @s wk.ability_add.has_ability matches 1 run playsound ui.toast.challenge_complete player @s
