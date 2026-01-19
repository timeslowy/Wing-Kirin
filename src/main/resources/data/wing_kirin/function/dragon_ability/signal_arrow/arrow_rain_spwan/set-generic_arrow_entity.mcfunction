## 执行者 `new_arrow`（即生成的箭矢）

## 移除新生成标签
tag @s remove new_arrow

# 传入来源 来自标志实体
data modify entity @s Owner set from entity @n[type=marker,tag=signal_arrow] data.Owner

# 传入伤害
data modify entity @s general_data.common_hit_effects[0].general_data.effects[0].effect.amount set from entity @n[type=marker,tag=signal_arrow] data.damage

# 将玩家本身作为排除对象 使用ds官方的接口
data modify entity @s general_data.common_hit_effects[0].general_data.effects[0].condition.term.predicate.type_specific.has_uuid \
    set from storage wing_kirin:ram signal_arrow.Owner

# 进行随机传送   存储 结果 在命令存储 （自定） x、y结果 int（整数） 1（类型）
function wing_kirin:dragon_ability/signal_arrow/arrow_rain_spwan/random_tp with storage wing_kirin:ram signal_arrow