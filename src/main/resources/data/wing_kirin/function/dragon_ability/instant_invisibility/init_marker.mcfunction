## 执行者：新生成的 instant_invisibility 标志实体（由 main 调度）
## 初始化标志实体数据

# 将UUID传入标志实体
data modify entity @s data.Owner_hex set from storage wing_kirin:ram math.hex_uuid

# 根据传入的等级设置标志实体的计分板分数来达到计时效果，每级*200（即scale），每刻减少1(在tick里减少)
execute store result score @s wk.instant_invisibility.duration \
    run data get storage wing_kirin:ram instant_invisibility.level 200

# 移除新生成标记防止选择器错乱
tag @s remove new_spawn
