#> wing_kirin:lifecycle/install
# @executor server
# @frequency once（首次加载，或 schema 版本变更时由 wing_kirin:load 的闸门调用）
# @input storage wing_kirin:lifecycle.schema
# @output 21 个计分板 objective；storage wing_kirin:lifecycle.schema
# @cleanup 见 wing_kirin:lifecycle/uninstall
## 非幂等段：scoreboard objectives add 对已存在的 objective 会报错，
## 因此本文件只允许由 load 的闸门调用。/reload 不会重复执行。
## 已经存在计分板的旧世界升级到本版本时，首次加载会打印一批「已存在」错误，
## 属于预期的一次性现象；闸门写好后不会再出现。
## 新增 objective 时：在此追加一条，并把 wing_kirin:load 闸门与本文件末尾的版本号递增。

# 定身术：1.统一计时器；2.单个实体最大定身次数；3.临时存储
scoreboard objectives add wk.stasis_hex.freezeTimer dummy
scoreboard objectives add wk.stasis_hex.freezeTimer-max_count dummy
scoreboard objectives add wk.stasis_hex.temp dummy

# 聚形散气：1.持续时间；2.移除检查
scoreboard objectives add wk.instant_invisibility.duration dummy
scoreboard objectives add wk.instant_invisibility.remove_check dummy

# 金风玉露：1.尝试次数；2.运行标志
scoreboard objectives add wk.empyrean_wine.attempt_count dummy
scoreboard objectives add wk.empyrean_wine.working_symbol dummy

# 一支穿云箭：1.标志实体存活时间；2.生成箭的数量
scoreboard objectives add wk.signal_arrow.life dummy
scoreboard objectives add wk.signal_arrow_spawn_count dummy

# 通仙心：1.随机值；2.是否已拥有
scoreboard objectives add wk.ability_add.random_value dummy
scoreboard objectives add wk.ability_add.has_ability dummy

# 不坏金身：1.运行标志；2.反震次数
scoreboard objectives add wk.indestructible_body.working_symbol dummy
scoreboard objectives add wk.indestructible_body.counter_shock_count dummy

# 回光返照：1.倒计时；2.单次死亡计数（防重复触发）
scoreboard objectives add wk.last_stand.death_countdown dummy
scoreboard objectives add wk.last_stand.death_check dummy

# 仁者无敌：受惠实体数
scoreboard objectives add wk.invincible_benevolence.beneficiary_amount dummy

# 天降正义：倒计时
scoreboard objectives add wk.heavenly_justice.countdown dummy

# 唯快不破：单次攻击计数
scoreboard objectives add wk.unstoppable_speed.attack_count dummy

# 玩家死亡发生（deathCount 类型）
scoreboard objectives add wk.death_check deathCount

# UUID 转换库：数学运算与常量缓存
scoreboard objectives add wk.math dummy

# 测试残留（P0-2 待清理，当前保留以维持行为一致）
scoreboard objectives add wk.test dummy

# 安装标记：load 的闸门据此跳过重复注册
data modify storage wing_kirin:lifecycle schema set value 1
