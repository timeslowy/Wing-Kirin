## 执行者：玩家
# 钠，天气要开始放晴了哟~
weather clear

# 用于触发进度————云开雾散
execute if entity @s[advancements={wing_kirin:wing_kirin/weather_clear=false}] run advancement grant @s only wing_kirin:wing_kirin/weather_clear

# 增加“施法云销雨霁次数”统计数据值
wk-stats add @s wing_kirin:casted_weather_clear_times 1