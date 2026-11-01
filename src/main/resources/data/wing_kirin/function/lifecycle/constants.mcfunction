#> wing_kirin:lifecycle/constants
# @executor server
# @frequency 每次加载（幂等，可重复执行）
# @input 无
# @output 计分板 wk.math 的常量缓存；storage wing_kirin:uuid.hex_map
# @cleanup 常量本身即长期状态，由 uninstall 随 objective 一并移除
## 幂等段：常量与查表每次加载都重写，不依赖 install 是否刚执行过。
## 若 wk.math 被外部删除，本文件会报错——用 /function wing_kirin:lifecycle/uninstall
## 后 /reload 即可重新安装。

# 数学运算常量
scoreboard players set #10 wk.math 10
scoreboard players set #12 wk.math 12
scoreboard players set #16 wk.math 16
scoreboard players set #-1 wk.math -1
scoreboard players set #20 wk.math 20
scoreboard players set #15 wk.math 15
scoreboard players set #01 wk.math 01
scoreboard players set #19 wk.math 19
scoreboard players set #256 wk.math 256

# UUID 十六进制查表（@Dragon_Linfeng 的转换库使用）
data modify storage wing_kirin:uuid hex_map set value ["00","01","02","03","04","05","06","07","08","09","0a","0b","0c","0d","0e","0f","10","11","12","13","14","15","16","17","18","19","1a","1b","1c","1d","1e","1f","20","21","22","23","24","25","26","27","28","29","2a","2b","2c","2d","2e","2f","30","31","32","33","34","35","36","37","38","39","3a","3b","3c","3d","3e","3f","40","41","42","43","44","45","46","47","48","49","4a","4b","4c","4d","4e","4f","50","51","52","53","54","55","56","57","58","59","5a","5b","5c","5d","5e","5f","60","61","62","63","64","65","66","67","68","69","6a","6b","6c","6d","6e","6f","70","71","72","73","74","75","76","77","78","79","7a","7b","7c","7d","7e","7f","80","81","82","83","84","85","86","87","88","89","8a","8b","8c","8d","8e","8f","90","91","92","93","94","95","96","97","98","99","9a","9b","9c","9d","9e","9f","a0","a1","a2","a3","a4","a5","a6","a7","a8","a9","aa","ab","ac","ad","ae","af","b0","b1","b2","b3","b4","b5","b6","b7","b8","b9","ba","bb","bc","bd","be","bf","c0","c1","c2","c3","c4","c5","c6","c7","c8","c9","ca","cb","cc","cd","ce","cf","d0","d1","d2","d3","d4","d5","d6","d7","d8","d9","da","db","dc","dd","de","df","e0","e1","e2","e3","e4","e5","e6","e7","e8","e9","ea","eb","ec","ed","ee","ef","f0","f1","f2","f3","f4","f5","f6","f7","f8","f9","fa","fb","fc","fd","fe","ff"]
