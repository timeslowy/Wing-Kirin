#> wing_kirin:lifecycle/reset_player
# @executor 单个玩家
# @frequency event（玩家死亡、全局 reset 共用；每次调用作用于执行者一人）
# @input 执行者身上的 wk.* 计分板与模组标签
# @output 全部玩家态归零；wk.death_check 置 0（deathCount 类型，不 reset）
# @cleanup 计分板 wk.*（除 wk.math 常量）、tag transposition_ready、模组药水效果
## 单玩家状态归零。死亡清理与 wing_kirin:lifecycle/reset 共用本文件，
## 保证「死亡 / 重置」只有一份清单，不会各自漂移。新增玩家态技能时在此追加一行。
## 注意：定身（being_frozen / NoAI / 定字展示实体）属实体级状态，
## 由 dragon_ability/stasia_hex/remove_effects 处理，不在此处重复。

# 定身术
scoreboard players reset @s wk.stasis_hex.freezeTimer
scoreboard players reset @s wk.stasis_hex.freezeTimer-max_count
scoreboard players reset @s wk.stasis_hex.temp

# 聚形散气（标志实体的 duration 随实体销毁，无需在此处理）
scoreboard players reset @s wk.instant_invisibility.remove_check

# 金风玉露
scoreboard players reset @s wk.empyrean_wine.attempt_count
scoreboard players reset @s wk.empyrean_wine.working_symbol

# 不坏金身
scoreboard players reset @s wk.indestructible_body.working_symbol
scoreboard players reset @s wk.indestructible_body.counter_shock_count

# 回光返照（wk.death_check 为 deathCount 类型：置 0 而非 reset，语义等价且保留类型）
scoreboard players reset @s wk.last_stand.death_countdown
scoreboard players reset @s wk.last_stand.death_check
scoreboard players set @s wk.death_check 0

# 天降正义
scoreboard players reset @s wk.heavenly_justice.countdown

# 唯快不破
scoreboard players reset @s wk.unstoppable_speed.attack_count

# 仁者无敌
scoreboard players reset @s wk.invincible_benevolence.beneficiary_amount

# 通仙心（每次投掷都会重写，此处仅防跨次残留）
scoreboard players reset @s wk.ability_add.random_value
scoreboard players reset @s wk.ability_add.has_ability

# 转位：标签可能因目标超距/消失而残留，必须清
tag @s remove transposition_ready

# 模组授予的药水效果（不处理原版 invisibility，避免误清玩家自饮药水）
effect clear @s dragonsurvival:broken_wings
effect clear @s wing_kirin:ding_shen
