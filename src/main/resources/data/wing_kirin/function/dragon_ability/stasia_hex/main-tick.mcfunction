## 由 wing_kirin:dragon_ability/stasia_hex/tick 触发（合并自 main-arrow + main-area）
## 执行者：被定身实体（统一标签 being_frozen）
## 每刻处理定身计时器递减，归零时移除效果

# 处理计时器，每刻-1
execute if score @s wk.stasis_hex.freezeTimer matches 1.. run scoreboard players remove @s wk.stasis_hex.freezeTimer 1

# 当计时器结束时移除效果
execute if score @s wk.stasis_hex.freezeTimer matches ..0 \
    run function wing_kirin:dragon_ability/stasia_hex/desctuor/remove_effects
