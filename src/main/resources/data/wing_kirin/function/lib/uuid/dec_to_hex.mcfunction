#> wing_kirin:lib/uuid/dec_to_hex
# @summary
#   将传入的 整形整数UUID 转换成 带连字符UUID
# @executor
#   Any
# @input
#   storage wing_kirin:uuid main.in: 传入的整形整数UUID<[I:int,int,int,int]>
# @output
#   storage wing_kirin:uuid main.out: 转换后的带连字符UUID<string>


execute store result score #t0 wk.math store result score #t1 wk.math run data get storage wing_kirin:uuid main.in[0]
execute store result storage wing_kirin:uuid temp.0 int 1 run scoreboard players operation #t0 wk.math %= #256 wk.math
execute store result score #t2 wk.math run scoreboard players operation #t1 wk.math /= #256 wk.math
execute store result storage wing_kirin:uuid temp.1 int 1 run scoreboard players operation #t1 wk.math %= #256 wk.math
execute store result score #t3 wk.math run scoreboard players operation #t2 wk.math /= #256 wk.math
execute store result storage wing_kirin:uuid temp.2 int 1 run scoreboard players operation #t2 wk.math %= #256 wk.math
execute store result storage wing_kirin:uuid temp.3 int 1 run scoreboard players operation #t3 wk.math /= #256 wk.math

execute store result score #t0 wk.math store result score #t1 wk.math run data get storage wing_kirin:uuid main.in[1]
execute store result storage wing_kirin:uuid temp.4 int 1 run scoreboard players operation #t0 wk.math %= #256 wk.math
execute store result score #t2 wk.math run scoreboard players operation #t1 wk.math /= #256 wk.math
execute store result storage wing_kirin:uuid temp.5 int 1 run scoreboard players operation #t1 wk.math %= #256 wk.math
execute store result score #t3 wk.math run scoreboard players operation #t2 wk.math /= #256 wk.math
execute store result storage wing_kirin:uuid temp.6 int 1 run scoreboard players operation #t2 wk.math %= #256 wk.math
execute store result storage wing_kirin:uuid temp.7 int 1 run scoreboard players operation #t3 wk.math /= #256 wk.math

execute store result score #t0 wk.math store result score #t1 wk.math run data get storage wing_kirin:uuid main.in[2]
execute store result storage wing_kirin:uuid temp.8 int 1 run scoreboard players operation #t0 wk.math %= #256 wk.math
execute store result score #t2 wk.math run scoreboard players operation #t1 wk.math /= #256 wk.math
execute store result storage wing_kirin:uuid temp.9 int 1 run scoreboard players operation #t1 wk.math %= #256 wk.math
execute store result score #t3 wk.math run scoreboard players operation #t2 wk.math /= #256 wk.math
execute store result storage wing_kirin:uuid temp.a int 1 run scoreboard players operation #t2 wk.math %= #256 wk.math
execute store result storage wing_kirin:uuid temp.b int 1 run scoreboard players operation #t3 wk.math /= #256 wk.math

execute store result score #t0 wk.math store result score #t1 wk.math run data get storage wing_kirin:uuid main.in[3]
execute store result storage wing_kirin:uuid temp.c int 1 run scoreboard players operation #t0 wk.math %= #256 wk.math
execute store result score #t2 wk.math run scoreboard players operation #t1 wk.math /= #256 wk.math
execute store result storage wing_kirin:uuid temp.d int 1 run scoreboard players operation #t1 wk.math %= #256 wk.math
execute store result score #t3 wk.math run scoreboard players operation #t2 wk.math /= #256 wk.math
execute store result storage wing_kirin:uuid temp.e int 1 run scoreboard players operation #t2 wk.math %= #256 wk.math
execute store result storage wing_kirin:uuid temp.f int 1 run scoreboard players operation #t3 wk.math /= #256 wk.math

function wing_kirin:lib/uuid/zzz/dec_to_hex/to_hex with storage wing_kirin:uuid temp
function wing_kirin:lib/uuid/zzz/dec_to_hex/output with storage wing_kirin:uuid temp