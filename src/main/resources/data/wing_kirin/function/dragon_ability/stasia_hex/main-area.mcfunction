##由 wing_kirin:dragon_ability/stasia_hex/tick 触发
## 执行者：被选择的实体

# 范围计时器
execute if score @s wk.stasis_hex.freezeTimer-area matches 1.. run scoreboard players remove @s wk.stasis_hex.freezeTimer-area 1

# 范围定身计时器结束时移除效果
execute if score @s wk.stasis_hex.freezeTimer-area matches ..0 \
    run function wing_kirin:dragon_ability/stasia_hex/desctuor/remove_effects-area

