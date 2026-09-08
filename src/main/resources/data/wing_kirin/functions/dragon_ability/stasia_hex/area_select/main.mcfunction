## 初始执行者:自己
## 执行位置：自己
# 范围施法用
# 1.20.1 移植：无 return fail，双重保险改为每行 unless 守卫（非潜行时全部跳过）

# 依据等级打标签
execute unless predicate wing_kirin:player_isnt_sneaking run function wing_kirin:dragon_ability/stasia_hex/area_select/area_tag

# 伤害以触发成就（成就暂未移植，伤害行按用户要求保留）
execute unless predicate wing_kirin:player_isnt_sneaking run damage @e[tag=stasis_hex-area,limit=1,sort=nearest] 0.1 wing_kirin:trace_ding by @s from @s

# 定身设置
execute unless predicate wing_kirin:player_isnt_sneaking as @e[tag=stasis_hex-area,distance=..28] at @s run function wing_kirin:dragon_ability/stasia_hex/area_select/setting
