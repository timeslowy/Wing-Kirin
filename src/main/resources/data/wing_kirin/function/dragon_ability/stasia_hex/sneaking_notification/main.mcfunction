## 执行者：玩家
# 动作栏显示信息

# 潜行提示
execute if predicate wing_kirin:player_isnt_sneaking run return \
    run title @s actionbar [{"translate": "actionbar.wing_kirin.ability.stasis_hex.non-sneaking_notification"}]

# 非潜行提示
title @s actionbar [{"translate": "actionbar.wing_kirin.ability.stasis_hex.sneaking_notification"}]