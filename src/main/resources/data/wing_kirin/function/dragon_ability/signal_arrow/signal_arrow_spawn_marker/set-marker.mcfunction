# 执行者: `new_marker`（标志实体）


## 移除新生成标签(防止选错)
tag @s remove new_marker


## 拷贝缓存数据到实体
# data.Owner            (生成者UUID)
data modify entity @s data.Owner set from storage wing_kirin:ram signal_arrow.Owner
# data.projectile_level (等级信息)
data modify entity @s data.projectile_level set from storage wing_kirin:ram signal_arrow.projectile_level

# 计算十六进制UUID并存入标志实体
data modify storage wing_kirin:ram math.UUID set from entity @s data.Owner
function wing_kirin:lib/math/uuid-dec_to_hex/main
data modify entity @s data.hex_Owner set from storage wing_kirin:ram math.hex_uuid

# 计算并传入伤害（单次计算，一口价，不实时更新）
function wing_kirin:dragon_ability/signal_arrow/signal_arrow_spawn_marker/caculate_damage with entity @s data


## 根据等级设置参数 也是存到标志实体里 merge：合并 （按tick运行）
# data.radius           (箭雨半径)
# data.life             (箭雨雨持续时间)
# data.tick_spawn_count (单个tick内生成的箭矢数量)
execute if data storage wing_kirin:ram {signal_arrow:{projectile_level:1}} \
    run data modify entity @s data merge value { \
        radius:12, life:100, tick_spawn_count:6 \
    }

execute if data storage wing_kirin:ram {signal_arrow:{projectile_level:2}} \
    run data modify entity @s data merge value { \
        radius:14, life:100, tick_spawn_count:8 \
    }

execute if data storage wing_kirin:ram {signal_arrow:{projectile_level:3}} \
    run data modify entity @s data merge value { \
        radius:16, life:100, tick_spawn_count:10 \
    }

execute if data storage wing_kirin:ram {signal_arrow:{projectile_level:4}} \
    run data modify entity @s data merge value { \
        radius:18, life:100, tick_spawn_count:12 \
    }

execute if data storage wing_kirin:ram {signal_arrow:{projectile_level:5}} \
    run data modify entity @s data merge value { \
        radius:20, life:100, tick_spawn_count:14 \
    }


## 设置计分板 store：储存 sesult：结果      计分板已在load申请 wk.signal_arrow.life(这是自订的名字)
execute store result score @s wk.signal_arrow.life \
    run data get entity @s data.life