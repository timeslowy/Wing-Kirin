##由 wing_kirin:dragon_ability/stasia_hex/tick 触发
## 执行者：箭击命中实体

# 处理箭中实体定身计时器，每刻-1
execute if score @s wk.stasis_hex.freezeTimer-arrow matches 1.. run scoreboard players remove @s wk.stasis_hex.freezeTimer-arrow 1

# 当箭击计时器结束时移除效果
execute if score @s wk.stasis_hex.freezeTimer-arrow matches ..0 \
    run function wing_kirin:dragon_ability/stasia_hex/desctuor/remove_effects-arrow

