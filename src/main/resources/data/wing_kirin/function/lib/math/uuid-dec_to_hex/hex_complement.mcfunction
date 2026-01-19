## 求16进制补码
# 传入:计分板 result wk.math    范围[0~15]

# 求反码
scoreboard players set complement wk.math 15
scoreboard players operation complement wk.math -= result wk.math

# 加上溢出位
scoreboard players operation complement wk.math += overflow wk.math

# 溢出
execute if score complement wk.math matches 16 run return run scoreboard players set overflow wk.math 1

# 没溢出
scoreboard players set overflow wk.math 0
