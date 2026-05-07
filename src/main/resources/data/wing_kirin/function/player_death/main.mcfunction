## 执行者：死亡的玩家
# 此时计分板 wk.death_check 的值为1

# 定身术：清除定身效果与计分板（已统一标签和计时器）
function wing_kirin:dragon_ability/stasia_hex/desctuor/remove_effects

# 回光返照：将玩家的倒计时计分板和死亡计数计分板重置
scoreboard players reset @s wk.last_stand.death_countdown
scoreboard players reset @s wk.last_stand.death_check

# 聚形散气：
# 见 wing_kirin:dragon_ability/instant_invisibility/tick ，清除属于玩家的标志实体
# 删除玩家的死亡检测计分板
scoreboard players reset @s wk.instant_invisibility.remove_check

# 死亡检查计分板归零
scoreboard players set @s wk.death_check 0
