## 执行者：在wing_kirin:dragon_ability/stasia_hex/main （“定身术”主函数）中已指定被箭击定身的实体为执行者

# 清除效果
effect clear @s dragonsurvival:broken_wings
effect clear @s dragonsurvival:magic_disabled
effect clear @s wing_kirin:ding_shen

# 移除所用标签
tag @s remove being_frozen-arrow

# 重置所属计分板
scoreboard players reset @s wk.stasis_hex.freezeTimer-arrow
scoreboard players reset @s wk.stasis_hex.freezeTimer-max_count

# 杀死骑乘的物品展示实体（定）
execute on passengers if entity @s[type=item_display] run kill @s
