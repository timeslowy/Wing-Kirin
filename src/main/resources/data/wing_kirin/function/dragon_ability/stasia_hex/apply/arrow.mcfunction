## 箭击定身
# 获取计时器分数并存入命令存储：转存入临时计分板，除以20后+1存入命令存储，再重置临时计分板
scoreboard players operation @s wk.stasis_hex.temp = @s wk.stasis_hex.freezeTimer-arrow
scoreboard players operation @s wk.stasis_hex.temp /= #20 wk.math
execute store result storage wing_kirin:ram stasis_hex.duration int 1 \
    run scoreboard players operation @s wk.stasis_hex.temp += #01 wk.math
scoreboard players reset @s wk.stasis_hex.temp

# 效果应用
execute at @s run function wing_kirin:dragon_ability/stasia_hex/apply/apply_effects with storage wing_kirin:ram stasis_hex