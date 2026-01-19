## 执行者 `new_arrow`（即生成的箭矢）
#$：宏命令让游戏认为这是传入的命令/数值 ，$(radius) 为前面的set—marker里面规定的，在范围（radius）内随机（random）取值
$execute store result storage wing_kirin:ram signal_arrow.result_x int 1 run random value -$(radius)..$(radius)
$execute store result storage wing_kirin:ram signal_arrow.result_y int 1 run random value -$(radius)..$(radius)

# 获得随机好的X、Z坐标后进行传送
execute as @s at @s \
    run function wing_kirin:dragon_ability/signal_arrow/arrow_rain_spwan/tp with storage wing_kirin:ram signal_arrow