#> wing_kirin:dragon_ability/transposition/tp_caster
# @executor 被视线选中的生物
# @frequency event（transposition/main 内调用一次）
# @input storage wing_kirin:ram transposition.caster（带连字符 UUID）
# @output 施法者被原地转向 180°
# @cleanup 由 transposition/cleanup 统一清标签与缓存
## 按 caster UUID 精确定位施法者，替代原先的 @n[tag=transposition_ready,distance=..15]。
## 原写法在两名玩家相邻且都在施法时可能选到最近的那一个（转错人）；UUID 选择无歧义。
## caster 指向已离线/不存在玩家时命令失败即静默跳过，与原先 @n 匹配失败等价。

$execute as $(caster) rotated as @s run function wing_kirin:dragon_ability/transposition/tp_player
