#> wing_kirin:tick/instant_invisibility
# @executor server → 标志实体（marker）
# @frequency tick
# @guard 选择器即守卫：@e[type=marker,tag=instant_invisibility]，无实体时不产生任何命令
# @input marker 自身 data（Owner_hex、duration）
# @output 计分板 duration 每刻 -1，归零自毙
# @cleanup tag instant_invisibility、tag instant_invisibility-select、计分板 wk.instant_invisibility.remove_check
## 聚形散气常驻入口：标志实体计时与失效检测
execute as @e[type=marker,tag=instant_invisibility] at @s run function wing_kirin:dragon_ability/instant_invisibility/tick with entity @s data
