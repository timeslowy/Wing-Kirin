## 执行者: 被定身的生物（非玩家，且未被"定"展示实体乘骑）
# 执行位置：当前实体
# 1.20.1 移植：物品组件 → NBT（CustomModelData 移入 tag，且 1.20.1 的 ItemStack.CODEC 要求 Count 必填）

# 生成展示物品实体显示"定"字（将原版物品替换贴图的文件导入）
summon item_display ~ ~ ~ {Tags:["item_display.being_frozen","new_item_display"],item:{id:"minecraft:firework_star",Count:1b,tag:{CustomModelData:12020000}},transformation:{scale:[1.2d,1.2d,0.5d],left_rotation:[0d,0d,0d,1d],right_rotation:[0d,0d,0d,1d],translation:[0d,0d,0d]},billboard:"vertical",brightness:{block:15,sky:15},item_display:"head",Glowing:1b,glow_color_override:16769841}

# 让物品展示实体（图片）骑乘在被定身生物上
ride @e[type=item_display,tag=new_item_display,limit=1,sort=nearest] mount @s

# 清除新生成标签
tag @e[type=item_display,tag=new_item_display,limit=1,sort=nearest] remove new_item_display
