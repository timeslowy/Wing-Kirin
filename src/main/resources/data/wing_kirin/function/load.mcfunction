#> wing_kirin:load
# @executor server
# @frequency 世界加载与 /reload（每次数据包载入）
# @input storage wing_kirin:lifecycle.schema
# @output 计分板 objective（仅首次）、wk.math 常量与 uuid 查表（每次）
# @cleanup wing_kirin:lifecycle/uninstall
## 安装闸门：scoreboard objectives add 不能重复执行，而 /reload 会重跑本文件。
## 闸门依据 storage 中的 schema 标记，只有缺少该标记时才运行 install。
## 新增 objective 时：在 lifecycle/install 追加一条，并把两处版本号从 1 递增。
execute unless data storage wing_kirin:lifecycle {schema:1} run function wing_kirin:lifecycle/install

# 常量与查表为幂等操作，每次加载都重写
function wing_kirin:lifecycle/constants
