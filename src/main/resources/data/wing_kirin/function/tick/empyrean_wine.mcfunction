#> wing_kirin:tick/empyrean_wine
# @executor server → 翼麒麟玩家
# @frequency tick
# @guard 首行：无玩家 working_symbol ≥ 0 时立即返回（未设置的计分项判 false，reset 即视为关闭）
# @input 计分板 wk.empyrean_wine.working_symbol
# @output working_symbol 每刻 -1，归零后调用 empyrean_wine/reset
# @cleanup 由 reset 清除 working_symbol、attempt_count
## 金风玉露常驻入口：运行标志递减与归零重置
execute unless entity @a[scores={wk.empyrean_wine.working_symbol=0..}] run return 0

execute as @a[predicate=wing_kirin:wing_kirin] if score @s wk.empyrean_wine.working_symbol matches 0.. run function wing_kirin:dragon_ability/empyrean_wine/tick
