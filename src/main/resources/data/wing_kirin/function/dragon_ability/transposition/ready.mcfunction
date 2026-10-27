#> wing_kirin:dragon_ability/transposition/ready
# @executor 施法玩家
# @frequency event（施法时触发一次）
# @input 玩家位置、UUID
# @output storage wing_kirin:ram transposition.{x,y,z,caster}；tag transposition_ready
# @cleanup transposition/cleanup（按 caster 清标签与缓存）、lifecycle/reset_player（死亡）
## 施法前置：记录施法者坐标与带连字符 UUID，并为施法者打上待转位标签。
## 记录 caster 是为了让 main 在「目标超距 / 目标消失 / main 未执行」时
## 仍能定位到施法者本人，避免标签永久残留。

# 将玩家的三维坐标存储入命令存储内
execute store result storage wing_kirin:ram transposition.x double 1 run data get entity @s Pos[0]
execute store result storage wing_kirin:ram transposition.y double 1 run data get entity @s Pos[1]
execute store result storage wing_kirin:ram transposition.z double 1 run data get entity @s Pos[2]

# 记录施法者带连字符 UUID（供 cleanup 按 UUID 清标签）
# 复用通用库：main.in ← 施法者 UUID，经 dec_to_hex 输出到 main.out
function wing_kirin:lib/uuid/get_caster-player
data modify storage wing_kirin:ram transposition.caster set from storage wing_kirin:uuid main.out

# 先移除旧标签再添加，保证重复施法幂等
tag @s remove transposition_ready
tag @s add transposition_ready
