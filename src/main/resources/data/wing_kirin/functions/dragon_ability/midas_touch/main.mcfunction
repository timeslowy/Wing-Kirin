## 执行者：玩家
# 移除手中 金风玉露
item modify entity @s weapon.mainhand wing_kirin:remove_1item

# 授予进度
execute if entity @s[advancements={wing_kirin:wing_kirin/midas_touch=false}] run advancement grant @s only wing_kirin:wing_kirin/midas_touch


