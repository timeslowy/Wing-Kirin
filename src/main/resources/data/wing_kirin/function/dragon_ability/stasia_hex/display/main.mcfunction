## 执行者: 被定身的生物
# 执行位置：当前实体
#播放被定身音效（烈焰人受伤调音调）
playsound entity.blaze.hurt player @a ~ ~ ~ 2.0 1.2

# 检查当前实体是否正在被展示实体乘骑
execute if function wing_kirin:dragon_ability/stasia_hex/display/ding_display_check run return fail

# 生成展示物品实体显示“定”字（将原版物品替换贴图的文件导入）
summon item_display ~ ~ ~ { \
    Tags:["item_display.being_frozen","new_item_display"], \
    item:{id:firework_star,components:{"custom_model_data":12020000},count:1,}, \
    transformation:{ \
        scale:[1.2d,1.2d,0.5d], \
        left_rotation:[0d,0d,0d,1d], \
        right_rotation:[0d,0d,0d,1d], \
        translation:[0d,0d,0d] \
    }, \
    billboard:"vertical", \
    brightness:{block:15,sky:15}, \
    item_display: "head", \
    Glowing:true, \
    glow_color_override: 16769841 \
}

# 让物品展示实体（图片）骑乘在被定身生物上
ride @n[type=item_display,tag=new_item_display] mount @s

# 清除新生成标签
tag @n[type=item_display,tag=new_item_display] remove new_item_display

