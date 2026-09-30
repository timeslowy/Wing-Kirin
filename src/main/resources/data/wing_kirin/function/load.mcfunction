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

# 唯快不破 单次攻击计数
scoreboard objectives add wk.unstoppable_speed.attack_count dummy

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
scoreboard players set #256 wk.math 256


data modify storage wing_kirin:uuid hex_map set value ["00","01","02","03","04","05","06","07","08","09","0a","0b","0c","0d","0e","0f","10","11","12","13","14","15","16","17","18","19","1a","1b","1c","1d","1e","1f","20","21","22","23","24","25","26","27","28","29","2a","2b","2c","2d","2e","2f","30","31","32","33","34","35","36","37","38","39","3a","3b","3c","3d","3e","3f","40","41","42","43","44","45","46","47","48","49","4a","4b","4c","4d","4e","4f","50","51","52","53","54","55","56","57","58","59","5a","5b","5c","5d","5e","5f","60","61","62","63","64","65","66","67","68","69","6a","6b","6c","6d","6e","6f","70","71","72","73","74","75","76","77","78","79","7a","7b","7c","7d","7e","7f","80","81","82","83","84","85","86","87","88","89","8a","8b","8c","8d","8e","8f","90","91","92","93","94","95","96","97","98","99","9a","9b","9c","9d","9e","9f","a0","a1","a2","a3","a4","a5","a6","a7","a8","a9","aa","ab","ac","ad","ae","af","b0","b1","b2","b3","b4","b5","b6","b7","b8","b9","ba","bb","bc","bd","be","bf","c0","c1","c2","c3","c4","c5","c6","c7","c8","c9","ca","cb","cc","cd","ce","cf","d0","d1","d2","d3","d4","d5","d6","d7","d8","d9","da","db","dc","dd","de","df","e0","e1","e2","e3","e4","e5","e6","e7","e8","e9","ea","eb","ec","ed","ee","ef","f0","f1","f2","f3","f4","f5","f6","f7","f8","f9","fa","fb","fc","fd","fe","ff"]

# 测试用，无实际意义
scoreboard objectives add wk.test dummy



