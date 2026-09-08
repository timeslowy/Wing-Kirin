## 执行者：被定身实体（统一标签 being_frozen）
## 将统一计时器 freezeTimer 转换为秒并存入临时计分板，再应用效果
# 1.20.1 移植：无 storage/宏，纯计分板运算（÷20后+1，与 1.21.1 宏的秒级量化一致）

# 获取计时器分数并转秒：freezeTimer ÷ 20 + 1
scoreboard players operation @s wk.stasis_hex.temp = @s wk.stasis_hex.freezeTimer
scoreboard players operation @s wk.stasis_hex.temp /= #20 wk.math
scoreboard players operation @s wk.stasis_hex.temp += #01 wk.math

# 效果应用
execute at @s run function wing_kirin:dragon_ability/stasia_hex/apply/apply_effects

# 重置临时计分板
scoreboard players reset @s wk.stasis_hex.temp
