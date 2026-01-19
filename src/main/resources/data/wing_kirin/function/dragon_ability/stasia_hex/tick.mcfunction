## 初始执行者：服务器（server）
## 每刻执行”定身术“两种效果函数
execute as @e[predicate=wing_kirin:is_living_entity,tag=being_frozen-arrow] run function wing_kirin:dragon_ability/stasia_hex/main-arrow
execute as @e[predicate=wing_kirin:is_living_entity,tag=being_frozen-area] run function wing_kirin:dragon_ability/stasia_hex/main-area

# 检查骑乘的实体是否死亡（对定身时间尚未结束而死亡实体的处理） unless:返回0则执行
execute as @e[type=item_display,tag=item_display.being_frozen] \
    unless function wing_kirin:dragon_ability/stasia_hex/display/check_vehicle \
        run kill @s