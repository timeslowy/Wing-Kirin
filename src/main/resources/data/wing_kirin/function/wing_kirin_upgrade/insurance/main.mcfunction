## “保险补偿”
# 执行者：玩家
# 30%概率返回通仙心
execute if predicate wing_kirin:30_percent run return run function wing_kirin:wing_kirin_upgrade/insurance/return_item

# 如果没有返通仙心给抽到重复技能的玩家给予“安慰奖”（doge）
function wing_kirin:wing_kirin_upgrade/insurance/return_xp
