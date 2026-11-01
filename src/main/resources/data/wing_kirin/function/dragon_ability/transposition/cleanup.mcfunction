#> wing_kirin:dragon_ability/transposition/cleanup
# @executor server（macro：以 storage wing_kirin:ram transposition 调用）
# @frequency event（transposition/main 末尾、以及任何需要中止转位时）
# @input storage wing_kirin:ram transposition.caster（带连字符 UUID）
# @output 施法者标签被移除；transposition 缓存被清空
# @cleanup 本文件即 cleanup
## 转位清场：按 UUID 定位施法者并移除待转位标签，然后清空本次缓存。
## caster 可能指向已离线/不存在的玩家，此时命令失败即静默跳过，不影响后续清理。
## 注意：必须与 tp_target 使用同一份 storage 快照，故调用方在传送完成后再调本文件。

$execute as $(caster) run tag @s remove transposition_ready

# 清空本次缓存，避免坐标与 caster 跨次 / 跨维度复用
execute if data storage wing_kirin:ram transposition run data remove storage wing_kirin:ram transposition
