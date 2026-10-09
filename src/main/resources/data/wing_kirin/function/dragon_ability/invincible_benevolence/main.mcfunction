## 执行者：施法者
# 主函数
# 进行循环搜索
function wing_kirin:dragon_ability/invincible_benevolence/select/search_beneficiary

# 若搜索函数返回0则计算
execute unless function wing_kirin:dragon_ability/invincible_benevolence/select/search_beneficiary run function wing_kirin:dragon_ability/invincible_benevolence/caculate/main

# 根据被治疗实体数应用效果
function wing_kirin:dragon_ability/invincible_benevolence/apply_effect with storage wing_kirin:ram invincible_benevolence