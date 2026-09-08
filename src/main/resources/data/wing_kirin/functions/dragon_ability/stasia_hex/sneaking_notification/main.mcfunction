## 执行者：玩家
# 动作栏显示信息
# 1.20.1 移植：无 return run，改为 if/unless 互斥两行

# 非潜行提示（将施放箭击定身）
execute if predicate wing_kirin:player_isnt_sneaking run title @s actionbar [{"translate": "actionbar.wing_kirin.ability.stasis_hex.non-sneaking_notification"}]

# 潜行提示（将施放范围定身）
execute unless predicate wing_kirin:player_isnt_sneaking run title @s actionbar [{"translate": "actionbar.wing_kirin.ability.stasis_hex.sneaking_notification"}]
