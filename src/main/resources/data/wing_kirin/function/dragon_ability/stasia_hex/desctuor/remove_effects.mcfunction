## 执行者：定身时间结束的被冻结实体（合并自 remove_effects-arrow + remove_effects-area）
## 恢复AI、清除效果、移除标签、重置计分板、杀死展示实体

# 恢复生物AI效果(双保险)
data modify entity @s NoAI set value false

# 清除效果
effect clear @s dragonsurvival:broken_wings
effect clear @s dragonsurvival:magic_disabled
effect clear @s wing_kirin:ding_shen

# 移除所用标签
tag @s remove being_frozen

# 重置所属计分板
scoreboard players reset @s wk.stasis_hex.freezeTimer
scoreboard players reset @s wk.stasis_hex.freezeTimer-max_count

# 杀死骑乘的物品展示实体（定）
execute on passengers if entity @s[type=item_display] run kill @s
