## 执行者：标志实体
## 1.20.1 移植版：
##  - 移除 $() 宏：落点坐标改由命令存储传递，actionbar 用 NBT 组件渲染
##  - 通过计分板配对 id 定位施法者（1.20.1 实体选择器不支持 UUID 字符串）
##  - TODO:移除 wk-stats 统计行（该命令随后续批次移植）

# 将落点坐标写入命令存储（供 actionbar 的 NBT 组件读取）
data modify storage wing_kirin:ram heavenly_justice.Pos set from entity @s Pos

# 取出本标志实体的配对 id
scoreboard players operation #hj_cur wk.heavenly_justice.cast_id = @s wk.heavenly_justice.cast_id

# 交由施法者自身执行 提示 / 重置 / 音效
execute as @a if score @s wk.heavenly_justice.cast_id = #hj_cur wk.heavenly_justice.cast_id run function wing_kirin:dragon_ability/heavenly_justice/message/to_caster
