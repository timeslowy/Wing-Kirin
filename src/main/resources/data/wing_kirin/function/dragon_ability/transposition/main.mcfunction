#> wing_kirin:dragon_ability/transposition/main
# @executor 被视线选中的生物
# @frequency event（施法时触发一次，目标为空则不执行）
# @input storage wing_kirin:ram transposition.{x,y,z,caster}
# @output 施法者与目标交换位置；缓存与标签被清理
# @cleanup transposition/cleanup（末尾无条件执行）
## 转位主体：先按 caster UUID 把施法者原地转向，再把目标送到施法者原位。
## 末尾的 cleanup 不依赖是否命中目标，因此只要 main 执行过就不会残留标签。

# 传送玩家：按 caster UUID 精确定位施法者（原 @n[distance=..15] 相邻双人时会选错）
function wing_kirin:dragon_ability/transposition/tp_caster with storage wing_kirin:ram transposition

# 根据先前玩家存储的坐标进行目标的传送，实现近似交换效果
function wing_kirin:dragon_ability/transposition/tp_target with storage wing_kirin:ram transposition

# 清场：按 UUID 移除施法者标签并清空缓存（必须在 tp_target 之后，两者共用同一份快照）
function wing_kirin:dragon_ability/transposition/cleanup with storage wing_kirin:ram transposition
