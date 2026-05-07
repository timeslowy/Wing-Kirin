## 执行者：玩家
# tp并调整视角
tp @s ~ ~ ~ ~180 ~

# 播放声音
playsound minecraft:entity.wind_charge.wind_burst master @s ~ ~ ~ 1 1

# 产生粒子
particle minecraft:cloud ~ ~1 ~ 0.5 0.5 0.5 0.1 10

# 去掉标签
tag @s remove transposition_ready