# 执行者: "穿云箭"a_signal_arrow（即火球消失时执行的）
## 1.20.1 移植版：@n 为 1.20.2+ 选择器，改用 @e[...limit=1,sort=nearest]（语义等价，刚 summon 的 marker 即最近者）

## 生成标志实体
summon marker ~ ~ ~ {Tags:["new_marker","signal_arrow_generic","signal_arrow"]}

# 设置标志实体 at:在第一个弹射物的最坐标位置执行 @S：往前推
execute as @e[type=marker,tag=new_marker,limit=1,sort=nearest] at @s run function wing_kirin:dragon_ability/signal_arrow/signal_arrow_spawn_marker/set-marker
