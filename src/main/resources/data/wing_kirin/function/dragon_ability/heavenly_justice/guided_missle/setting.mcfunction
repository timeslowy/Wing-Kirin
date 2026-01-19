## 执行者：导弹
## 执行位置：导弹自身
# 将导弹tp至标志实体上方300格处
tp @s ~ ~300 ~

# 设置导弹向下速度分量为10d
data modify entity @s Motion set value [0.0d,-10.0d,0.0d]

# 移除火球标签防止选择混淆
tag @s remove new_summon

## 制导导弹已发射！
