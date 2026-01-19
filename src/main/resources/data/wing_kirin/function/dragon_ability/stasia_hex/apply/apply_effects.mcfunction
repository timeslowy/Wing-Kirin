##执行者：被定身实体
## 执行位置：实体
# 传入：wing_kirin:ram stasis_hex

# 禁用生物AI
data modify entity @s NoAI set value true

# 给予各种效果
$effect give @s dragonsurvival:broken_wings $(duration) 0
$effect give @s dragonsurvival:magic_disabled $(duration)
$effect give @s wing_kirin:ding_shen $(duration) 0
$effect give @s minecraft:glowing $(duration)

# 视声效果
function wing_kirin:dragon_ability/stasia_hex/display/main
