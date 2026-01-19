## 初始执行者:自己 
## 执行位置：自己
# 范围施法用
# 双重保险，若技能内判断是否蹲下的条件出现故障导致判断异常，此处可以再次阻止执行
execute if entity @s if predicate wing_kirin:player_isnt_sneaking run return fail

# 依据等级打标签
function wing_kirin:dragon_ability/stasia_hex/area_select/area_tag

# 伤害以触发成就
damage @n[tag=stasis_hex-area] 0.1 dragonsurvival:trace_ding by @s from @s

# 定身设置
execute as @e[tag=stasis_hex-area,distance=..28] at @s run function wing_kirin:dragon_ability/stasia_hex/area_select/setting



