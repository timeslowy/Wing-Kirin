## 执行者: 被定身的生物
# 执行位置：当前实体
# 1.20.1 移植：无 return fail，改为 unless 守卫后调用 summon；
# 重复骑乘检查由谓词 wing_kirin:has_ding_display_passenger 承担（替代原 ding_display_check 函数）

# 玩家由 Java 侧处理（原版 /ride 指令不支持实体骑乘玩家）；已被"定"展示实体乘骑的跳过
execute unless entity @s[type=player] unless predicate wing_kirin:has_ding_display_passenger run function wing_kirin:dragon_ability/stasia_hex/display/summon
