##执行者：被定身实体
## 执行位置：实体
# 传入：wk.stasis_hex.temp（秒，由 set_duration 计算）
# 1.20.1 移植：三效果的分支表给予移至 give_effects（方案C）

# 给予各种效果（定身、断翅、发光）
function wing_kirin:dragon_ability/stasia_hex/apply/give_effects

# 禁用生物AI（双保险）
data modify entity @s NoAI set value true

# 视声效果
function wing_kirin:dragon_ability/stasia_hex/display/main
