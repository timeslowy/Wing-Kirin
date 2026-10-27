#> wing_kirin:lifecycle/reset
# @executor server
# @frequency 手动（/function wing_kirin:lifecycle/reset）；也是 uninstall 的第一步
# @input 全服运行态（玩家计分板、实体标签、marker/display、storage）
# @output 运行时状态全清；objective 与常量保留
# @cleanup 见文件内 5 个分段
## 软重置：保留 objective 与常量，清空全部运行时状态。
## 用于状态卡死排查、数据包更新后复位、以及卸载前的状态收敛。
## 玩家态归零复用 lifecycle/reset_player，与死亡清理走同一套清单。

# 1) 玩家态归零
execute as @a run function wing_kirin:lifecycle/reset_player

# 2) 定身中的生物：恢复 AI、清效果、移标签、重置计时、清定字展示实体
execute as @e[tag=being_frozen] run function wing_kirin:dragon_ability/stasia_hex/remove_effects

# 3) 清除本模组生成的 marker 与展示实体
kill @e[type=marker,tag=instant_invisibility]
kill @e[type=marker,tag=signal_arrow_generic]
kill @e[type=marker,tag=heavenly_justice_marker]
kill @e[type=item_display,tag=heavenly_justice_marker_display]
kill @e[type=item_display,tag=item_display.being_frozen]

# 4) 兜底移除临时标签（覆盖已脱离上述实体的漏网项）
tag @e[tag=beneficiary] remove beneficiary
tag @e[tag=instant_invisibility-select] remove instant_invisibility-select
tag @e[tag=new_arrow] remove new_arrow
tag @e[tag=new_marker] remove new_marker
tag @e[tag=new_spawn] remove new_spawn
tag @e[tag=new_summon] remove new_summon
tag @e[tag=new_item_display] remove new_item_display

# 5) 清空运行时 storage（保留 uuid.hex_map 与 lifecycle.schema）
data remove storage wing_kirin:ram empyrean_wine
data remove storage wing_kirin:ram heavenly_justice
data remove storage wing_kirin:ram instant_invisibility
data remove storage wing_kirin:ram invincible_benevolence
data remove storage wing_kirin:ram last_stand
data remove storage wing_kirin:ram math
data remove storage wing_kirin:ram signal_arrow
data remove storage wing_kirin:ram stasis_hex
data remove storage wing_kirin:ram transposition
data remove storage wing_kirin:uuid main
data remove storage wing_kirin:uuid temp
