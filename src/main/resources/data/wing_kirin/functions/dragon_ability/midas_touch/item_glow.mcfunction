## 执行者：范围内掉落物
# 遍历打上标签，已在技能内有距离限制了
execute if entity @s[type=item,nbt={Item:{id:"minecraft:stone"}}] run tag @s add midas_touch
execute if entity @s[type=item,nbt={Item:{id:"minecraft:cobblestone"}}] run tag @s add midas_touch
execute if entity @s[type=item,nbt={Item:{id:"minecraft:basalt"}}] run tag @s add midas_touch
execute if entity @s[type=item,nbt={Item:{id:"minecraft:netherrack"}}] run tag @s add midas_touch
execute if entity @s[type=item,nbt={Item:{id:"minecraft:diorite"}}] run tag @s add midas_touch
execute if entity @s[type=item,nbt={Item:{id:"minecraft:granite"}}] run tag @s add midas_touch
execute if entity @s[type=item,nbt={Item:{id:"minecraft:andesite"}}] run tag @s add midas_touch
execute if entity @s[type=item,nbt={Item:{id:"minecraft:calcite"}}] run tag @s add midas_touch
execute if entity @s[type=item,nbt={Item:{id:"minecraft:copper_ingot"}}] run tag @s add midas_touch
execute if entity @s[type=item,nbt={Item:{id:"minecraft:lapis_lazuli"}}] run tag @s add midas_touch
execute if entity @s[type=item,nbt={Item:{id:"minecraft:emerald"}}] run tag @s add midas_touch
execute if entity @s[type=item,nbt={Item:{id:"minecraft:coal"}}] run tag @s add midas_touch
execute if entity @s[type=item,nbt={Item:{id:"minecraft:charcoal"}}] run tag @s add midas_touch

# 加入队伍
execute if entity @s[type=item,tag=midas_touch] run team join midas_touch

# 他妈的再穷举删去标签
execute unless entity @s[type=item,nbt={Item:{id:"minecraft:stone"}}] unless entity @s[type=item,nbt={Item:{id:"minecraft:cobblestone"}}] unless entity @s[type=item,nbt={Item:{id:"minecraft:basalt"}}] unless entity @s[type=item,nbt={Item:{id:"minecraft:netherrack"}}] unless entity @s[type=item,nbt={Item:{id:"minecraft:diorite"}}] unless entity @s[type=item,nbt={Item:{id:"minecraft:granite"}}] unless entity @s[type=item,nbt={Item:{id:"minecraft:andesite"}}] unless entity @s[type=item,nbt={Item:{id:"minecraft:calcite"}}] unless entity @s[type=item,nbt={Item:{id:"minecraft:copper_ingot"}}] unless entity @s[type=item,nbt={Item:{id:"minecraft:lapis_lazuli"}}] unless entity @s[type=item,nbt={Item:{id:"minecraft:emerald"}}] unless entity @s[type=item,nbt={Item:{id:"minecraft:coal"}}] unless entity @s[type=item,nbt={Item:{id:"minecraft:charcoal"}}] run tag @s remove midas_touch

# 移除队伍
execute if entity @s[type=item,tag=!midas_touch] run team leave @s


