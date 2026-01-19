## 执行者: `signal_arrow-generic_entity`
## 由 wing_kirin:dragon_ability/signal_arrow/main-projectile 函数调动

## 缓存数据
# data.Owner            (生成者UUID)
data modify storage wing_kirin:ram signal_arrow.Owner set from entity @s Owner
# data.projectile_level (等级信息)
data modify storage wing_kirin:ram signal_arrow.projectile_level set from entity @s projectile_level

## 生成标志实体
function wing_kirin:dragon_ability/signal_arrow/signal_arrow_spawn_marker/spawn

# 生成范围标识粒子
execute at @s run function wing_kirin:dragon_ability/signal_arrow/radiu_display/success/main

# 可以落下箭雨（高度条件满足）时播放成功音效
playsound entity.firework_rocket.large_blast_far player @a ~ ~ ~ 60