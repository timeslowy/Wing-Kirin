##加载计分板

# 定身术使用
# 统一计时器（v3.0 合并了 arrow 和 area 两套计时系统）
scoreboard objectives add wk.stasis_hex.freezeTimer dummy
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


# 通仙心 1.技能获取所用随机数 2.判断技能是否存在 3.保险补偿 30% 判定结果
scoreboard objectives add wk.ability_add.random_value dummy
scoreboard objectives add wk.ability_add.has_ability dummy
scoreboard objectives add wk.ability_add.insurance_roll dummy

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
# 天降正义 施法者与标志实体的配对 id（1.20.1 无法按 UUID 选玩家，改用计分板配对寻址）
scoreboard objectives add wk.heavenly_justice.cast_id dummy

# 唯快不破 单次攻击计数
scoreboard objectives add wk.unstoppable_speed.attack_count dummy

# 玩家死亡发生
scoreboard objectives add wk.death_check deathCount

# 化朽为奇 掉落物队伍（1.20.1无法检测标签真操蛋）
team add midas_touch


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
# 通仙心随机数取模用（1.20.1 无 /random value，改用 UUID %= 100）
scoreboard players set #100 wk.math 100

# 测试用，无实际意义
scoreboard objectives add wk.test dummy



