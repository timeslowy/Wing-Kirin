#> wing_kirin:tick/stasia_hex
# @executor server → 被定身实体 / 定字展示实体
# @frequency tick
# @guard 首行：既无 being_frozen 标签实体、也无定字展示实体时立即返回（纯标签选择器，不评估谓词）
# @input 计分板 wk.stasis_hex.freezeTimer
# @output 计时器每刻 -1；归零后由 remove_effects 负责清理
# @cleanup tag being_frozen、tag item_display.being_frozen、定字 item_display 实体
## 定身术常驻入口：守卫后转交 dragon_ability/stasia_hex/tick（计时推进 + 展示实体校验）
execute unless entity @e[tag=being_frozen] unless entity @e[tag=item_display.being_frozen] run return 0

function wing_kirin:dragon_ability/stasia_hex/tick
