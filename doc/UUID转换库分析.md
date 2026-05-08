# UUID 转换库优化分析

> 目标：`lib/math/uuid-dec_to_hex` — 十进制 int[] UUID → 十六进制字符串  
> 日期：2026年5月8日  
> ⚠ 本文仅为分析，不做任何代码修改

---

## 一、库概况

### 1.1 功能

将 Minecraft 实体的 int[] 格式 UUID（`[I; a, b, c, d]`，4 个有符号 32 位整数）转换为标准 hex 字符串格式（`xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx`），以便在 `$()` 宏中作为玩家选择器使用。

### 1.2 文件结构（8 个文件）

| 文件 | 作用 | 行数 |
|------|------|:---:|
| `main.mcfunction` | 入口：初始化 hex_map 和 hex_uuid，调用 4 个 block 计算 | 16 |
| `calculate_uuid_block.mcfunction` | 处理一个 32-bit int 块：获取值、处理符号、分 2 组各 4 次 bit 计算 | 32 |
| `calculate_uuid_bit.mcfunction` | 处理一个 hex 位：取余→查表→拼接→递除 | 22 |
| `hex_complement.mcfunction` | 负数补码计算（含溢出处理） | 13 |
| `hex_map.mcfunction` | 查表：0-15 → '0'-'f' | 1 |
| `insert-hex_uuid_bit.mcfunction` | 字符串拼接：bit 插到 block 前 | 2 |
| `insert-hex_uuid_block.mcfunction` | 字符串拼接：block 插到 uuid 前 | 2 |
| `insert-separator.mcfunction` | 插入 '-' 分隔符 | 2 |

### 1.3 核心算法

这是 Minecraft mcfunction 中实现的**完整大整数十进制→十六进制转换**，包括：
- 4 个 32-bit int 块逐一处理
- 有符号数（负数）处理：通过**补码（two's complement）**转换
- 逐位除法取余（÷16）映射到 hex 字符
- 字符串拼接（前插法）和 '-' 分隔符插入

---

## 二、调用关系

### 2.1 三个调用点（均为一次性初始化，非每刻）

| 调用者 | 场景 | 频率 |
|--------|------|:---:|
| `instant_invisibility/main.mcfunction` | 每次施展聚形散气 | 1 次/施法 |
| `signal_arrow/signal_arrow_spawn_marker/set-marker.mcfunction` | 每次射出穿云箭 | 1 次/施法 |
| `heavenly_justice/ability_data/marker.mcfunction` | 每次天降正义定位成功 | 1 次/施法 |

### 2.2 hex UUID 的下游使用者

| 使用者 | NBT 字段名 | 用途 |
|--------|-----------|------|
| `instant_invisibility/tick.mcfunction` | `data.Owner_hex` | 每刻用 `$(Owner_hex)` 选择器检查玩家距离、死亡状态 |
| `instant_invisibility/distance_notice/main.mcfunction` | `data.Owner_hex` | 距离提示路由（已优化为 3 档） |
| `signal_arrow/.../caculate_damage.mcfunction` | `data.hex_Owner` | 一次性伤害计算（力量/锋利/致密加成） |
| `heavenly_justice/message/successful_message.mcfunction` | `data.hex_Owner` | 一次性成功消息 + 声音 + 统计 |

> 核心结论：hex UUID **只在标志实体初始化时计算一次**，之后被缓存于实体 NBT 中反复使用。并非每 tick 重算。

---

## 三、函数调用开销分析

### 3.1 单次转换的函数调用链

```
main (1次)
 ├─ calculate_uuid_block {num:0} (1次)
 │   ├─ calculate_uuid_bit ×8
 │   │   ├─ hex_complement (仅负数时)
 │   │   ├─ hex_map
 │   │   └─ insert-hex_uuid_bit
 │   ├─ insert-separator (仅 block 0-1)
 │   └─ insert-hex_uuid_block
 ├─ calculate_uuid_block {num:1} (同上)
 ├─ calculate_uuid_block {num:2} (同上)
 └─ calculate_uuid_block {num:3} (同上)
```

**单次转换统计：**

| 子函数 | 最少调用次数 | 最坏调用次数 |
|--------|:-----------:|:-----------:|
| `main` | 1 | 1 |
| `calculate_uuid_block` | 4 | 4 |
| `calculate_uuid_bit` | 32 | 32 |
| `hex_map` | 32 | 32 |
| `hex_complement` | 0 | 32 |
| `insert-hex_uuid_bit` | 32 | 32 |
| `insert-separator` | 2 | 2 |
| `insert-hex_uuid_block` | 4 | 4 |
| **总计** | **~107** | **~139** |

> 每次 UUID 转换触发约 **107~139 次嵌套函数调用**。

### 3.2 实际性能影响

由于只在**施法瞬间**执行（每秒最多几次），而非每 tick，对服务器 tick 的影响**微乎其微**。主要问题是：
- **代码复杂度高**：8 个文件、5 层嵌套调用，维护和理解困难
- **施法瞬间有微小卡顿可能**：如果短时间内大量施法（如多个玩家同时使用），瞬时函数调用数激增

---

## 四、优化方案

### 方案 A：Java 侧预计算（🥇 推荐，性能最佳）

**思路**：在 NeoForge 模组 Java 代码中，利用 Java 自带的 `UUID.toString()` 能力，将 hex UUID 预先写入实体的 NBT 或全局 storage。

**实施方式**：
- 方案 A1：在 `dragonsurvival:summon_data` 附件中增加 `owner_hex` 字段。当 Dragon Survival 框架生成标记实体时，顺便写入 hex UUID。datapack 侧直接用宏读取。
- 方案 A2：注册一条简单的自定义命令（如 `/wk-uuid @s`），将执行者的 hex UUID 存入 `wing_kirin:ram` storage。

**伪代码（A2）**：
```java
// Java 侧
context.getSource().sendSuccess(() -> 
    "data modify storage wing_kirin:ram math.hex_uuid set value \"" 
    + player.getUUID().toString() + "\"", false);
```

**对 datapack 的影响**：
```mcfunction
# 之前（3行 + 139次函数调用）
data modify storage wing_kirin:ram math.UUID set from entity @s UUID
function wing_kirin:lib/math/uuid-dec_to_hex/main

# 之后（可能简化为 1 行，取决于实现方式）
execute as @s run function wing_kirin:lib/get_hex_uuid
# 或直接用 Dragon Survival 框架提供的字段
data modify entity @s data.Owner_hex set from entity @s "neoforge:attachments"."dragonsurvival:summon_data".data.owner_hex
```

**优点**：
- 🚀 **消除全部 107~139 次函数调用**
- 🧹 可删除整个 `lib/math/uuid-dec_to_hex` 目录（8 个文件）
- 🛡️ 零精度损失（Java `UUID.toString()` 是标准实现）
- 🏗️ 一劳永逸

**缺点**：
- ⚠️ 需要修改 Java 代码（涉及 NeoForge 模组开发）
- ⚠️ 如果 Dragon Survival 是第三方依赖且不可修改，则需要在自己的 mod 中添加

---

### 方案 B：用实体 Tag 替代 hex UUID（🥈 纯 datapack，中等改动）

**思路**：在生成标志实体时，给所有者玩家打上一个**唯一临时标签**（如 `ii_owner_<random>`），标志实体存储这个标签名，后续用 `@a[tag=...]` 代替 `$(hex_uuid)` 选择器。

**实施方式**：
```mcfunction
# instant_invisibility/main.mcfunction
# 生成唯一标签
scoreboard players add #ii_id wk.math 1
execute store result storage wing_kirin:ram ii.tag int 1 run scoreboard players get #ii_id wk.math
tag @s add ii_owner_$(ii.tag)
summon marker ... {Tags:[...,"ii_owner_$(ii.tag)"]}
# 标志实体存储标签名而非 hex UUID
```

**下游使用**：
```mcfunction
# 之前
$execute as $(Owner_hex) run ...
# 之后
$execute as @a[tag=ii_owner_$(Owner_tag),limit=1] run ...
```

**优点**：
- ✅ 纯 datapack 实现，无需改 Java
- ✅ 消除 139 次函数调用
- ✅ 删除 8 个库文件

**缺点**：
- ⚠️ 标签可能被意外清除（`/tag @a remove *` 等操作）
- ⚠️ `@a[tag=,limit=1]` 比直接的 UUID 选择器稍慢（需遍历全部玩家匹配 tag）
- ⚠️ 需要一个全局递增计数器来生成唯一标签

---

### 方案 C：存储 int[] UUID，用 `@e[nbt=]` 选择（🥉 最简单但有性能风险）

**思路**：不转换 hex，直接在标志实体存储原始 `int[] UUID`，下游使用 NBT 匹配选择器。

**下游使用**：
```mcfunction
# 之前
$execute as $(Owner_hex) run ...
# 之后（每次都要用 execute store 匹配 UUID）
$execute as @a[nbt={UUID:$(Owner_UUID)}] run ...
```

**优点**：
- ✅ 零额外代码，直接删除整个 uuid-dec_to_hex 目录

**缺点**：
- ❌ **`@a[nbt=]` 是 Minecraft 中最慢的选择器之一**（需对每个玩家序列化 NBT 比对）
- ❌ 在 `tick.mcfunction` 每刻执行的路径上（`instant_invisibility/tick`）使用 NBT 选择器会造成显著的性能退化
- ❌ 不可取

---

### 方案 D：优化现有算法（不推荐，改动大收益小）

**思路**：减少递归深度、合并小函数、预计算 hex_map 到 storage 等。

**分析**：当前算法已经是最优的 mcfunction 实现。每次 hex digit 计算需要取余→查表→拼接→递除，这些步骤无法在一条命令中完成。即使合并几个小函数（如把 1 行的 `hex_map` 内联到 `calculate_uuid_bit`），也只能减少 32 次调用（107→75），但代码会更难维护。

**不建议采用**。

---

## 五、方案对比矩阵

| 维度 | A（Java 预计算） | B（Tag 替代） | C（NBT 选择器） | D（优化算法） |
|------|:---:|:---:|:---:|:---:|
| **函数调用减少** | 100%（139→0） | 100%（139→0） | 100%（139→0） | ~30%（139→100） |
| **可删除文件数** | 8 | 8 | 8 | 2~3 |
| **需改 Java** | ✅ 是 | ❌ 否 | ❌ 否 | ❌ 否 |
| **运行时性能** | 🥇 最优 | 🥈 tag遍历略慢 | ❌ NBT极慢 | 🥉 微改善 |
| **健壮性** | 🥇 UUID不变 | ⚠️ 标签可被清除 | 🥇 UUID不变 | 🥇 UUID不变 |
| **维护性** | 🥇 标准方案 | 🥈 需维护计数器 | 🥉 简单但慢 | ❌ 更复杂 |
| **实施难度** | 中（需Java知识） | 低 | 极低 | 中 |

---

## 六、建议

1. **如果可以改 Java** → **方案 A** 是最佳选择。`UUID.toString()` 是 JDK 标准 API，一行代码解决问题，彻底消除整个转换库。

2. **如果只能改 datapack** → **方案 B** 是合理的折中。用递增计数器 + 实体 tag 替代 hex UUID，注意要加 `limit=1` 防止多选。

3. **不建议**方案 C（NBT 选择器太慢）和方案 D（投入产出比太低）。

---

## 七、方案 A 的具体实施路线（如果选择）

### 需要确认的前置条件

- Wing Kirin 模组是否有自己的 Mixin 或事件处理器？（从 `mixins/` 目录看应该有）
- Dragon Survival 的 `summon_data` 附件结构是否可以扩展？

### 实施步骤

1. 在 Java 侧添加一个工具方法，输入 `Player`，输出 hex UUID 字符串
2. 选择一个写入点：
   - 写入全局 `wing_kirin:ram` storage（简单但需要命令权限）
   - 直接在 `instant_invisibility/main`、`signal_arrow`、`heavenly_justice` 的 marker 创建处，通过 DS 框架或自定义命令写入
3. 修改 3 个调用点 + 多个下游引用（Owner_hex → 统一字段名）
4. 删除 `lib/math/uuid-dec_to_hex/` 目录
5. 验证伤害来源功能正常
