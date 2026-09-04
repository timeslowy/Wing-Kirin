## 进行物品替换
## 执行者：玩家

# 判断玩家爪中的物品是否是玻璃瓶(不是则返回)(1.20.1无语法)
# execute unless items entity @s weapon.mainhand glass_bottle run return fail

# 移除一次玩家的物品
item modify entity @s weapon.mainhand wing_kirin:empyrean_wine

# 给予玩家 金风玉露
give @s wing_kirin:empyrean_wine 1

# 播放转换成功声音
playsound block.beacon.activate player @s

# TODO:转换成功粒子（1.20.1无法执行？）
particle dragonsurvival:treasure 1 1 0 1 ~ ~ ~ 1 1 1 1 30
particle dragonsurvival:treasure 1 0.4 1 1 ~ ~ ~ 1 1 1 1 30

# 重设玩家尝试次数
scoreboard players reset @s wk.empyrean_wine.attempt_count