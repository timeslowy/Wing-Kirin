#> wing_kirin:lifecycle/uninstall
# @executor server
# @frequency 手动（/function wing_kirin:lifecycle/uninstall）；仅在删除本模组前使用
# @input storage wing_kirin:lifecycle.schema（存在即视为已安装）
# @output 全部 objective、storage 根与运行态实体被移除
# @cleanup 本文件即 cleanup 终点
## 硬卸载：先软重置收敛运行态，再移除全部 objective 与 storage 根。
## 卸载后需 /reload（或重进世界）才会由 wing_kirin:load 重新安装。
## 重复执行为空操作：schema 标记已删则首行即返回，不会对不存在的 objective 刷错。

# 未安装则直接结束
execute unless data storage wing_kirin:lifecycle schema run return 0

# 先收敛运行态（玩家态、定身生物、marker/display、临时标签、storage 运行段）
function wing_kirin:lifecycle/reset

# 移除全部 objective（分数随之删除）
scoreboard objectives remove wk.stasis_hex.freezeTimer
scoreboard objectives remove wk.stasis_hex.freezeTimer-max_count
scoreboard objectives remove wk.stasis_hex.temp
scoreboard objectives remove wk.instant_invisibility.duration
scoreboard objectives remove wk.instant_invisibility.remove_check
scoreboard objectives remove wk.empyrean_wine.attempt_count
scoreboard objectives remove wk.empyrean_wine.working_symbol
scoreboard objectives remove wk.signal_arrow.life
scoreboard objectives remove wk.signal_arrow_spawn_count
scoreboard objectives remove wk.ability_add.random_value
scoreboard objectives remove wk.ability_add.has_ability
scoreboard objectives remove wk.indestructible_body.working_symbol
scoreboard objectives remove wk.indestructible_body.counter_shock_count
scoreboard objectives remove wk.last_stand.death_countdown
scoreboard objectives remove wk.last_stand.death_check
scoreboard objectives remove wk.invincible_benevolence.beneficiary_amount
scoreboard objectives remove wk.heavenly_justice.countdown
scoreboard objectives remove wk.unstoppable_speed.attack_count
scoreboard objectives remove wk.death_check
scoreboard objectives remove wk.math
scoreboard objectives remove wk.test

# 移除剩余 storage 根（ram 与 uuid.main/temp 已在 reset 中清除）
data remove storage wing_kirin:uuid hex_map
data remove storage wing_kirin:lifecycle schema
