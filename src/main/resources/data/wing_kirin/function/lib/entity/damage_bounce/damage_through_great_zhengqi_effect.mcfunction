## 通过判断是否有浩然正气效果计算伤害加成
# 执行者: player
# 输入: wing_kirin:ram math.result
# 结果: wing_kirin:ram math.result

# 如果玩家不存在“浩然正气”效果则直接结束
execute unless data entity @s active_effects[{id:"wing_kirin:great_zhengqi"}] run return fail

# 传入前三步计算的damage
execute store result score result wk.math run data get storage wing_kirin:ram math.result

# 加成 20% 由于计分板无法存储小数，故先乘12再在存入命令存储时除以10，亦达成保留小数的乘1.2效果
scoreboard players operation result wk.math *= #12 wk.math

# 将运算结果存储到命令存储（除以10）
execute store result storage wing_kirin:ram math.result float 0.1 run scoreboard players get result wk.math
 