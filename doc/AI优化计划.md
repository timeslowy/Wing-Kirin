# 🐉 Wing Kirin 函数优化计划

> Minecraft 1.21.1 NeoForge | Dragon Survival Addon  
> 分析日期：2026年5月7日  
> 最后更新：2026年5月7日

---

## 一、项目概况

共涉及 **13 个龙之能力**（`dragon_ability/`），约 **97 个 `.mcfunction` 文件**，以及升级系统、玩家死亡处理、通用库函数等。

---

## 二、优化建议清单（按优先级排列）

### 🔴 高优先级 — 性能影响显著

#### 1. `instant_invisibility/distance_notice` — 24 条逐格 distance 检查（每刻） ✅ 已完成

**位置：**
- `green_25-15.mcfunction`（10 条 → 3 条）
- `yellow_14-6.mcfunction`（9 条 → 3 条）
- `red_5-1.mcfunction`（5 条 → 4 条）

**问题：** 每个激活的隐身标记实体，每刻都要执行最多 24 条 `execute if entity @s[distance=X..X.99]` 判断来显示距离数字。这是 O(24 × 活跃标记数) 每刻的指令开销。

**已实施方案（2026-05-07）：**
由于 Minecraft 1.21 无法直接通过 `data get` 获取实体间的 distance 数值，采用了**区间合并 + 路由短路**策略：
- 绿色区域（1..10.99）：10 档位 → 3 档位（`>21`, `>18`, `15`）
- 黄色区域（11..19.99）：9 档位 → 3 档位（`>11`, `>8`, `6`）
- 红色区域（20..24.99）：5 档位 → 4 档位，末尾用无条件 `return run` 裁断，保持精确倒计时
- `main.mcfunction` 的路由逻辑不变

**实际效果：**
| 区域 | 原最坏检查数 | 现最坏检查数 | 减少 |
|------|:-----------:|:-----------:|:----:|
| 绿色 | 12 | 5 | **58%** |
| 黄色 | 12 | 6 | **50%** |
| 红色 | 9 | 8 | **11%** |
| **总指令行数** | **28** | **14** | **50%** |

> 注：红色区域保持精确值（5→1 倒计时），仅减少 1 条冗余检查

---

#### 2. `invincible_benevolence/particle.mcfunction` — 36 条硬编码粒子环 ✅ 已完成

**问题：** 36 条 `particle end_rod ~ ~ ~ <dx> 0 <dz> 9 0 force` 命令，每条只有 x/z 偏移不同。

**已实施方案（2026-05-08）：** Minecraft 粒子系统无法原生生成环形粒子，因此采用了**等角度降采样**策略：从 36 等分（每 10°）降为 12 等分（每 30°）。每条粒子的方向向量、速度、扩散参数完全保留。环的视觉效果基本不变——12 条 `end_rod` 射线的径向扩散仍然形成清晰的圆形轮廓。

| 指标 | 优化前 | 优化后 | 减少 |
|------|:-----:|:-----:|:----:|
| 粒子命令数 | 36 | **12** | **67%** |
| 文件行数 | 38 | **14** | **63%** |

---

#### 3. `tick.mcfunction` — 无条件每刻执行所有 tick 子函数 ✅ 已完成

**问题：** 主 tick 函数无条件执行 8 条 `execute as @a/@e run function ...`，即使服务器无玩家或能力未激活。

**已实施方案（2026-05-07）：**

为每条 `execute as @a` 指令添加了计分板守卫条件，为 `function` 直接调用添加了实体存在性守卫：

| 能力 | 原入口 | 新增守卫 | 空闲时行为 |
|------|--------|---------|-----------|
| stasia_hex | 直接 `function` | `if entity @e[tag=being_frozen]` → 已随双轨合并简化 | 无冻结实体→跳过函数调用 |
| empyrean_wine | `as @a[predicate]` | `if score matches 0..` | 无激活酒壶→跳过函数调用 |
| last_stand | `as @a`（无过滤） | `if score matches 0..` | 无回光返照→跳过函数调用 |
| indestructible_body | `as @a[predicate]` | `if score matches ..26` | 无激活金身→跳过函数调用 |
| heavenly_justice data | `as @a[predicate]` | `if score matches 0..` | 无定位中→跳过函数调用 |
| instant_invisibility | `as @e[tag]` | 已充分过滤 | 无需修改 |
| signal_arrow | `as @e[tag]` | 已充分过滤 | 无需修改 |
| heavenly_justice tick | `as @e[tag]` | 已充分过滤 | 无需修改 |
| player_death | `as @a if score` | 已有 per-player 过滤 | 无需修改 |

**stasia_hex 已后续简化：** 随双轨合并（`stasia_hex_merge_analysis.md`），守卫已从 2 条简化为 1 条：`if entity @e[tag=being_frozen]`

**实际效果：** 空闲服务器上，原 9 条 tick 指令中有 5 条被有效守卫跳过，显著减少无意义的 `execute as @a` 遍历和函数调用开销。

---

#### 4. `signal_arrow/arrow_rain_spwan/cycle_spawn.mcfunction` — 递归循环生成箭矢

**问题：** 每 tick 通过递归函数调用自身来消耗 `wk.signal_arrow_spawn_count` 计分板。若 tick_spawn_count=14，则每 tick 递归 14 次函数调用。

**优化方案：**
- 可以使用 Minecraft 1.21 的 `return run function` 尾递归优化（已在使用）
- 或者将生成逻辑移到 Java 侧处理（若可行）
- 当前实现已较优，主要是需注意：箭矢的 NBT 数据非常庞大（约 80 行 JSON），每次 summon 都需解析
- **潜在改进**：将 spawn.mcfunction 中庞大的 NBT JSON 预设为实体模板，减少运行时 NBT 解析开销
- **预计减少指令行数**：spawn.mcfunction 约 80 行 → 可尝试使用 `data modify ... from storage` 模板化

---

### 🟡 中优先级 — 代码质量 & 可维护性

#### 5. 多个文件中的 5 级硬编码分支

**涉及文件：**
- `empyrean_wine/probability.mcfunction`（5 条 level 检查）
- `signal_arrow/signal_arrow_spawn_marker/set-marker.mcfunction`（5 条 level 检查）
- `invincible_benevolence/caculate/ability_level.mcfunction`（5 条 level 检查）
- `signal_arrow/radiu_display/success/main.mcfunction`（5 条 level 检查）

**问题：** 每个文件对 level 1~5 分别写了 5 条几乎相同的 `execute if data` 分支。虽然执行时只有一条命中，但增加了维护成本。

**优化方案：**
- 使用宏参数化：将等级值直接作为宏参数传入，函数内部用 `$(level)` 引用
- 或者使用计分板 + 数学运算替代逐级判断
- 例如 `radius` 可表示为 `10 + level * 2`，用 `scoreboard players operation` 计算
- **注意事项**：部分逻辑确实需要分等级处理不同效果（如 probability 的百分比概率），此种情况保留分支是合理的，但可改用宏简化

---

#### 6. `stasia_hex/apply/arrow.mcfunction` 与 `area.mcfunction` — 高度重复代码 ✅ 已完成（双轨合并）

**问题：** 两个文件内容几乎完全相同（4 行计分板操作 + 1 行 apply 调用），仅使用的计分板名不同（`freezeTimer-arrow` vs `freezeTimer-area`）。

**已实施方案（2026-05-07）：** 随定身术双轨合并统一为 `apply/set_duration.mcfunction`，直接引用统一后的 `freezeTimer` 计分板。原 `apply/main.mcfunction`（路由）、`apply/arrow.mcfunction`、`apply/area.mcfunction` 均已删除。详见 `stasia_hex_merge_analysis.md`。

---

#### 7. `stasia_hex/desctuor/remove_effects-arrow.mcfunction` 与 `remove_effects-area.mcfunction` — 高度重复 ✅ 已完成（双轨合并）

**问题：** 两个文件几乎完全相同，仅 tag 名称不同（`being_frozen-arrow` vs `being_frozen-area`）。

**已实施方案（2026-05-07）：** 随定身术双轨合并统一为 `desctuor/remove_effects.mcfunction`，使用统一标签 `being_frozen` 和统一计时器 `freezeTimer`。原两个文件已删除。详见 `stasia_hex_merge_analysis.md`。

---

#### 8. `heavenly_justice/ability_data/marker.mcfunction` — 极长 NBT 路径

**问题：**
```
data modify entity @s data.Owner set from entity @s "neoforge:attachments"."dragonsurvival:summon_data".data.owner_uuid
```
这条 NBT 路径非常长且使用了引号包裹的复合键名。

**优化方案：**
- 可以将常见的 NBT 路径预存到 storage 中
- 或封装为独立的小函数以便复用
- 此类长路径在多个地方重复出现，统一管理可减少出错

---

### 🟢 低优先级 — 微优化 & 规范

#### 9. `instant_invisibility/main.mcfunction` — 重复的 `@n` 选择器 ✅ 已完成

**问题：** 
```
execute as @n[type=marker,tag=new_spawn] run ...
execute as @n[type=marker,tag=new_spawn] run ...
```
该选择器被调用了 3 次。虽然执行效率尚可，但更好的做法是先 `execute as @n[...] run function ...` 一次性切换执行者，内部使用 `@s`。

**已实施方案（2026-05-08）：** 创建 `instant_invisibility/init_marker.mcfunction`，将 3 条分散的 `@n` 操作（UUID设置、计时器初始化、临时标签清除）合并为一次 `execute as @n[...] run function ...` 调用。`@n` 选择从 3 次减为 1 次。

---

#### 10. `cloudy_spin/main.mcfunction` — 重复选择器

**问题：** 同样两次 `execute as @e[distance=..5,...] run effect give @s ...`，可以合并为一条 `execute as @e[...] run function ...`

**优化方案：** 合并两个 effect 命令到一个子函数中

---

#### 11. `indestructible_body/tick.mcfunction` — 重复条件合并 ✅ 已完成

**问题：**
```
execute if score @s wk.indestructible_body.working_symbol matches 26 run scoreboard players reset @s wk.indestructible_body.counter_shock_count
execute if score @s wk.indestructible_body.working_symbol matches 26 run scoreboard players reset @s wk.indestructible_body.working_symbol
```
两条命令条件完全相同，可以合并到子函数中一起执行。

**已实施方案（2026-05-08）：** 创建 `indestructible_body/reset.mcfunction` 内含两条 reset 命令，tick 中合并为单条 `execute if ... matches 26 run function .../reset`。条件判断从 2 次减为 1 次。

---

#### 12. `empyrean_wine/tick.mcfunction` — 同样的重复条件 ✅ 已完成

**问题：** 和 #11 类似，两个 `matches 0` 条件重复。

**已实施方案（2026-05-08）：** 创建 `empyrean_wine/reset.mcfunction`，tick 中合并为单条 `execute if ... matches 0 run function .../reset`。

---

#### 13. `lib/math/uuid-dec_to_hex` — UUID 转换库调用频繁

**问题：** `uuid-dec_to_hex/main.mcfunction` 在多个能力中被调用（instant_invisibility, signal_arrow, heavenly_justice），每次调用触发至少 6 个子函数调用。

**优化方案：**
- 如果可能，在 Java 侧预先计算 hex UUID 并存入实体 NBT，避免每 tick 重复转换
- 或者在 summon marker 时一次性计算，而不是每 tick 重算（目前部分实现已如此，但可审查是否有重复计算）

---

#### 14. `heavenly_justice/guided_missle/summon_missle.mcfunction` — 超大 NBT JSON

**问题：** 约 70 行的 summon NBT 数据直接写在函数中，包含大量嵌套 JSON。

**优化方案：**
- 考虑将通用模板预存到 `storage` 中，用 `data modify ... from storage` 引用
- 减少函数文件体积，也减少 Minecraft 解析 JSON 的开销

---

#### 15. `stasia_hex/display/main.mcfunction` — summon item_display 的 NBT 模板化

**问题：** 每次定身都 summon 一个 item_display 展示"定"字，NBT 约 20 行。可以考虑预存模板。

**优化方案：** 同上，预存到 storage 模板

---

## 三、总结与优先级矩阵

| 编号 | 优化项 | 类型 | 预计节省指令 | 性能影响 | 难度 |
|------|--------|------|:-----------:|:--------:|:----:|
| 1 | distance_notice 24条→2条 | 性能 | ~22条/刻/实体 | ⭐⭐⭐⭐⭐ | 中 |
| 2 | 36条粒子环→1~4条 | 性能 | ~32条/次 | ⭐⭐⭐⭐ | 低 |
| 3 | tick无条件守卫 | 性能 | 5~8条/刻 | ⭐⭐⭐ | 低 |
| 4 | 箭矢NBT模板化 | 性能 | ~70行 | ⭐⭐⭐ | 中 |
| 5 | 5级硬编码→宏 | 代码质量 | 若干 | ⭐ | 中 |
| 6 | stasia apply合并 | 代码质量 | 减少文件 | ⭐ | 低 |
| 7 | remove_effects合并 | 代码质量 | 减少文件 | ⭐ | 低 |
| 8 | 长NBT路径封装 | 可维护性 | - | - | 低 |
| 9 | @n选择器合并 | 微优化 | 1~2条 | ⭐ | 低 |
| 10 | cloudy_spin合并 | 微优化 | 1条 | ⭐ | 低 |
| 11 | indestructible_body合并 | 微优化 | 1条 | ⭐ | 低 |
| 12 | empyrean_wine合并 | 微优化 | 1条 | ⭐ | 低 |
| 13 | UUID转换缓存 | 微优化 | 若干 | ⭐⭐ | 中 |
| 14 | 导弹NBT模板化 | 可维护性 | ~60行 | ⭐⭐ | 中 |
| 15 | 定字展示NBT模板化 | 可维护性 | ~15行 | ⭐ | 低 |

---

## 四、建议执行顺序

1. **第一轮（低风险高回报）**：优化项 #2、#3、#9、#10、#11、#12
2. **第二轮（核心性能）**：优化项 #1
3. **第三轮（代码整理）**：优化项 #5、#6、#7、#8
4. **第四轮（NBT模板化）**：优化项 #4、#14、#15
5. **第五轮（深度优化）**：优化项 #13

---

## 五、注意事项

- 所有修改前建议先在测试环境验证
- `$()` 宏是 Minecraft 1.20.5+ 的特性，本项目已是 1.21.1，可安全使用
- 合并函数时注意 `return run` 的语义，避免意外中断调用链
- NBT 模板化需注意 `data modify ... from` 的引用路径正确性
- 部分重复代码（如逐级 `if data` 检查）在当前规模下性能影响可忽略，更侧重于可维护性
