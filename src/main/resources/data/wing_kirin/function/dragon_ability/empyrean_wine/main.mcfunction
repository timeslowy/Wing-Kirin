## 随机替换玩家爪中的 玻璃瓶 为 金风玉露
## 执行者：玩家

# 提示
function wing_kirin:dragon_ability/empyrean_wine/notice

# 概率
function wing_kirin:dragon_ability/empyrean_wine/probability

# 概率计算返回1，则获得
execute if function wing_kirin:dragon_ability/empyrean_wine/probability run function wing_kirin:dragon_ability/empyrean_wine/modify_item

# 设置技能的运行标志
scoreboard players set @s wk.empyrean_wine.working_symbol 25

# 消费后清理：probability 内部有多条 run return 早退分支，故把清理放在调用方收口
execute if data storage wing_kirin:ram empyrean_wine run data remove storage wing_kirin:ram empyrean_wine

