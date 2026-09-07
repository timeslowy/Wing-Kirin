# 执行者: `new_marker`（标志实体）
## 1.20.1 移植版：
##  - 移除 UUID 十六进制转换库段（1.20.1 实体参数不支持 UUID 字符串，原 uuid-dec_to_hex 库无用途）
##  - 移除 caculate_damage 宏调用段（$execute as $(hex_Owner) 为 1.20.2+ 宏，且伤害计算改由
##    Java 侧 SignalArrowRainHandler 在爆点时刻按发射者当前状态完成，语义与 1.21.1 一致）
##  - 等级参数段原样保留（无宏），Java 侧每刻从 data 复合标签读取 radius / life / tick_spawn_count

## 移除新生成标签(防止选错)
tag @s remove new_marker

## 拷贝缓存数据到实体
# data.Owner            (生成者UUID)
data modify entity @s data.Owner set from storage wing_kirin:ram signal_arrow.Owner
# data.projectile_level (等级信息)
data modify entity @s data.projectile_level set from storage wing_kirin:ram signal_arrow.projectile_level

## 根据等级设置参数 也是存到标志实体里 merge：合并 （按tick运行，由 Java 侧读取）
# data.radius           (箭雨半径)
# data.life             (箭雨雨持续时间)
# data.tick_spawn_count (单个tick内生成的箭矢数量)
execute if data storage wing_kirin:ram {signal_arrow:{projectile_level:1}} run data modify entity @s data merge value {radius:12, life:100, tick_spawn_count:6}

execute if data storage wing_kirin:ram {signal_arrow:{projectile_level:2}} run data modify entity @s data merge value {radius:14, life:100, tick_spawn_count:8}

execute if data storage wing_kirin:ram {signal_arrow:{projectile_level:3}} run data modify entity @s data merge value {radius:16, life:100, tick_spawn_count:10}

execute if data storage wing_kirin:ram {signal_arrow:{projectile_level:4}} run data modify entity @s data merge value {radius:18, life:100, tick_spawn_count:12}

execute if data storage wing_kirin:ram {signal_arrow:{projectile_level:5}} run data modify entity @s data merge value {radius:20, life:100, tick_spawn_count:14}

## 设置计分板 store：储存 result：结果      计分板已在load申请 wk.signal_arrow.life（调试观察用，Java 侧不依赖）
execute store result score @s wk.signal_arrow.life run data get entity @s data.life
