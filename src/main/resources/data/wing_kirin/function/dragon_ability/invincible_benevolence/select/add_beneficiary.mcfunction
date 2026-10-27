## 执行者：被治疗的实体（包括自己）
# 打上标签以供判断
tag @s add beneficiary

# 如果为翼麒麟玩家则赠予5秒的抗性提升Ⅴ
execute if entity @s[type=player,predicate=wing_kirin:wing_kirin] run effect give @s resistance 5 4