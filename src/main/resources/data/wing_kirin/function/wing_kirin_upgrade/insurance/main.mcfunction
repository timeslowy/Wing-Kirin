## “保险补偿”
# 执行者：玩家
# 给抽到重复技能的玩家给予“安慰奖”（doge）
xp add @s 200 points

# 播放声音
playsound entity.experience_orb.pickup player @s

# 提示
title @s actionbar [{"translate": "actionbar.wing_kirin.ability.insurance"}]
