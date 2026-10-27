#> wing_kirin:tick/indestructible_body
# @executor server → 翼麒麟玩家
# @frequency tick
# @guard 首行：无玩家 working_symbol ≤ 26 时立即返回（未设置的计分项判 false，施法前该分支不运行）
# @input 计分板 wk.indestructible_body.working_symbol
# @output working_symbol 每刻 +1，达到 26 后调用 reset
# @cleanup 由 reset 清除 working_symbol、counter_shock_count
## 不坏金身常驻入口：运行标志递增与到点重置
execute unless entity @a[scores={wk.indestructible_body.working_symbol=..26}] run return 0

execute as @a[predicate=wing_kirin:wing_kirin] if score @s wk.indestructible_body.working_symbol matches ..26 run function wing_kirin:dragon_ability/indestructible_body/tick
