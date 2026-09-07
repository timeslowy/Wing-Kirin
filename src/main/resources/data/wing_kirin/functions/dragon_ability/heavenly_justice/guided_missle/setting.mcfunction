## 执行者：导弹
## 执行位置：导弹自身
## 1.20.1 移植版：与 1.21.1 原样一致（无宏、无高版本语法）

# 将导弹tp至标志实体上方300格处
tp @s ~ ~300 ~

# 设置导弹向下速度分量为10d
data modify entity @s Motion set value [0.0d,-10.0d,0.0d]

# 移除火球标签防止选择混淆
tag @s remove new_summon
