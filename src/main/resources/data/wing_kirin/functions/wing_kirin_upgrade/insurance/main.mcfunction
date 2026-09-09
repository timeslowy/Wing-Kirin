## “保险补偿”
# 执行者：玩家
# 1.20.1 移植版：无 /return run（1.20.2+）。
# 注意：random_chance 谓词每次求值都会重新掷骰，不能写成 if / unless 两次求值
# （会出现“两者都中”或“两者都不中”），故先把一次判定结果落到计分板再分支。

# 掷一次 30% 判定（1=命中）
scoreboard players set @s wk.ability_add.insurance_roll 0
execute if predicate wing_kirin:30_percent run scoreboard players set @s wk.ability_add.insurance_roll 1

# 30%概率返回通仙心
execute if score @s wk.ability_add.insurance_roll matches 1 run function wing_kirin:wing_kirin_upgrade/insurance/return_item

# 如果没有返通仙心给抽到重复技能的玩家给予“安慰奖”（doge）
execute if score @s wk.ability_add.insurance_roll matches 0 run function wing_kirin:wing_kirin_upgrade/insurance/return_xp
