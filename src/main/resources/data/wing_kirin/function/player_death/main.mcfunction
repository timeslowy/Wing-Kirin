#> wing_kirin:player_death/main
# @executor 死亡的玩家
# @frequency event（由 tick/player_death 在 wk.death_check ≥ 1 时触发）
# @input 计分板 wk.death_check = 1；玩家身上的各技能状态
# @output 玩家全部技能状态归零；death_check 置 0
# @cleanup 实体级定身状态由 stasia_hex/remove_effects 处理；
#          玩家级状态统一交给 lifecycle/reset_player（与全局 reset 同一份清单）
## 死亡清理入口：先清实体级定身，再调单玩家归零。
## 新增玩家态技能时只需往 lifecycle/reset_player 追加，不必改本文件。

# 定身术：恢复 AI、清效果、移标签、清定字展示实体（实体级）
function wing_kirin:dragon_ability/stasia_hex/remove_effects

# 单玩家状态归零（回光返照、聚形散气、天降正义、金风玉露、不坏金身、
# 唯快不破、仁者无敌、通仙心、转位残留标签，以及 death_check 置 0）
function wing_kirin:lifecycle/reset_player
