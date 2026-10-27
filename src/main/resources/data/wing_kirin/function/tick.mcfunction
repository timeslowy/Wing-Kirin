#> wing_kirin:tick
# @executor server
# @frequency tick（每个服务器 tick，由 minecraft/tags/function/tick.json 触发）
# @input 无：各 feature 自行读取计分板 / 实体标签 / marker data
# @output 无：仅转发
# @cleanup 无：清理写在各 feature 入口文件内
## 全局 tick 调度器。本文件只做转发，不含任何选择器、计分板或条件判断。
## 新增常驻技能 = 在此追加一行 function wing_kirin:tick/<feature>，
## 并把执行对象、频率与空闲守卫写进对应的入口文件。

# 定身术：计时推进与定字展示实体校验
function wing_kirin:tick/stasia_hex

# 金风玉露：运行标志递减与归零重置
function wing_kirin:tick/empyrean_wine

# 回光返照：死亡倒计时
function wing_kirin:tick/last_stand

# 不坏金身：运行标志递增与到点重置
function wing_kirin:tick/indestructible_body

# 聚形散气：标志实体计时与失效检测
function wing_kirin:tick/instant_invisibility

# 一支穿云箭：箭雨生成
function wing_kirin:tick/signal_arrow

# 天降正义：marker 计时与发射者倒计时
function wing_kirin:tick/heavenly_justice

# 玩家死亡：跨技能状态清理钩子
function wing_kirin:tick/player_death
