##加载计分板

# 定身术使用
# 箭击生物定身计时器
scoreboard objectives add wk.stasis_hex.freezeTimer-arrow dummy
# 范围施法定身计时器
scoreboard objectives add wk.stasis_hex.freezeTimer-area dummy
# 单个实体最大定身次数上限
scoreboard objectives add wk.stasis_hex.freezeTimer-max_count dummy
# 临时存储
scoreboard objectives add wk.stasis_hex.temp dummy


# 聚形散气 1.持续时间；2.移除检查
scoreboard objectives add wk.instant_invisibility.duration dummy
scoreboard objectives add wk.instant_invisibility.remove_check dummy


# 金风玉露 1.统计玩家执行尝试的次数；2.技能的运行标志
scoreboard objectives add wk.empyrean_wine.attempt_count dummy
scoreboard objectives add wk.empyrean_wine.working_symbol dummy


# 一支穿云箭 标志实体计分版 1.标志实体存活时间（即箭雨存货时间）；2.生成箭的数量
scoreboard objectives add wk.signal_arrow.life dummy
scoreboard objectives add wk.signal_arrow_spawn_count dummy


# 通仙心 1.技能获取所用随机数 2.判断技能是否存在
scoreboard objectives add wk.ability_add.random_value dummy
scoreboard objectives add wk.ability_add.has_ability dummy

# 不坏金身 1.运行标志计分板；2.反震次数计分板
scoreboard objectives add wk.indestructible_body.working_symbol dummy
scoreboard objectives add wk.indestructible_body.counter_shock_count dummy

# 回光返照 1.倒计时 2.一次死亡计数（防止重复触发）
scoreboard objectives add wk.last_stand.death_countdown dummy
scoreboard objectives add wk.last_stand.death_check dummy

# 仁者无敌 受惠实体数
scoreboard objectives add wk.invincible_benevolence.beneficiary_amount dummy

# 天降正义 倒计时
scoreboard objectives add wk.heavenly_justice.countdown dummy

# 三连发延迟炮
scoreboard objectives add wk.explosion_arrow.countdown dummy

# 玩家死亡发生
scoreboard objectives add wk.death_check deathCount


# @Dragon_Linfeng 的UUID转换库使用
# 用于数学运算
scoreboard objectives add wk.math dummy
# 缓存值
scoreboard players set #10 wk.math 10
scoreboard players set #12 wk.math 12
scoreboard players set #16 wk.math 16
scoreboard players set #-1 wk.math -1
scoreboard players set #20 wk.math 20
scoreboard players set #15 wk.math 15
scoreboard players set #01 wk.math 01
scoreboard players set #19 wk.math 19

# 测试用，无实际意义
scoreboard objectives add wk.test dummy



