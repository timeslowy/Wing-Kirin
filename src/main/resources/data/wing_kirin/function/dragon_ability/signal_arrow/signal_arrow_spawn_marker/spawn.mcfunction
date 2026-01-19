# 执行者: “穿云箭“a_signal_arrow（即火球消失时执行的）


## 生成标志实体
summon marker ~ ~ ~ {Tags:["new_marker","signal_arrow_generic","signal_arrow"]}

# 设置标志实体 at:在第一个弹射物的最坐标位置执行 @S：往前推
execute as @n[type=marker,tag=new_marker] at @s run function wing_kirin:dragon_ability/signal_arrow/signal_arrow_spawn_marker/set-marker