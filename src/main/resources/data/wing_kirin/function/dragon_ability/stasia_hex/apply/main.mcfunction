## 执行者：被定身实体
# 设置具体效果时长

# 箭击定身
execute if entity @s[tag=being_frozen-arrow] run return run function wing_kirin:dragon_ability/stasia_hex/apply/arrow

# 范围定身
execute if entity @s[tag=being_frozen-area] run function wing_kirin:dragon_ability/stasia_hex/apply/area
