# Wing Kirin 命令存储字段表

> 适用版本：1.21.1 NeoForge 分支（`wing_kirin`）
> 生成依据：对 `src/main/resources/data/wing_kirin/function/**` 的全量静态扫描（2026-10-27）
> 维护规则：**改动任何 `storage` 字段前，先更新本表**；本表与源码不一致时以本表为排查起点。

## 1. 总则

命令存储只有两个根：

| 根 | 归属 | 用途 |
|---|---|---|
| `wing_kirin:ram` | 技能运行期 | 单次施法的参数中转（等级、时长、坐标、伤害中间值） |
| `wing_kirin:uuid` | 通用库 + 配置 | UUID 十进制↔十六进制转换的工作区与查表 |

三条硬约束：

1. **不跨 tick**。`ram` 中任何字段都只在「生产者写入 → 消费者读走」的同一调用链内有效。需要跨 tick 存活的状态一律放实体 `data`（marker / item_display）或计分板。
2. **不跨玩家**。同一 tick 内多名玩家相继施法会依次覆写同一个字段；因此写入后必须**立即**消费，不得在中间插入其他施法链。
3. **根名 = 技能目录名**。新增技能时在 `ram` 下用与 `dragon_ability/<id>` 一致的根名，不建二级共享根。

## 2. `wing_kirin:ram` 字段

| 根 | 字段 | 类型 | 生产者 | 消费者 | 清理点 |
|---|---|---|---|---|---|
| `empyrean_wine` | `level` | int | `empyrean_wine/probability:2` | 同文件 5 条等级匹配 | `empyrean_wine/main` 末尾 |
| `signal_arrow` | `level` | int | `radiu_display/charging/main:4` | 同文件 5 条等级匹配 | 下一次成功施法时被 `spawn_marker/main` 整根清理 |
| `signal_arrow` | `Owner` | IntArray(UUID) | `signal_arrow_spawn_marker/main:6` | `set-marker:10` | `signal_arrow_spawn_marker/main` 末尾 |
| `signal_arrow` | `projectile_level` | int | `signal_arrow_spawn_marker/main:8` | `set-marker:12`、`set-marker:25-49` 等级表 | 同上 |
| `signal_arrow` | `result_x` / `result_y` | int | `arrow_rain_spwan/random_tp:3,4` | `arrow_rain_spwan/tp`（`with storage`） | 下一 tick 被 marker 镜像整根覆盖 |
| `signal_arrow` | 整根镜像 | Compound | `arrow_rain_spwan/main:4`（`set from entity @s data`） | `arrow_rain_spwan/*`（`with storage`） | 同上 |
| `stasis_hex` | `level` | int | `looking_at/initial_hit:4`、`superposition:4` | `apply_effects`（经 `set_duration`） | `apply/set_duration` 末尾 |
| `stasis_hex` | `duration` | int | `apply/set_duration:7` | `apply_effects` 的 `$(duration)` | 同上 |
| `instant_invisibility` | `level` | int | `instant_invisibility/main:9` | `init_marker:11` | `init_marker` 末尾 |
| `heavenly_justice` | `level` | int | `ability_data/player:3` | `ability_data/marker:9` | `ability_data/marker` 末尾 |
| `last_stand` | `level` | int | `last_stand/countdown:4` | `countdown:6` | `countdown` 末尾 |
| `invincible_benevolence` | `level` | int | `caculate/main:5` | `caculate/ability_level` 选档 | `invincible_benevolence/main` 末尾 |
| `invincible_benevolence` | `beneficiary_amount` | int | `caculate/ability_level:3,7,11,15,19` | `apply_effect` 的 `$(beneficiary_amount)` | 同上 |
| `math` | `result` | int/float | `caculate_damage:2`、`damage_bounce/*:16,17,26,31` | `damage_bounce/*` 链、`caculate_damage:17` | `caculate_damage` 末尾 |
| `transposition` | `x` / `y` / `z` | double | `transposition/ready:12-14` | `tp_target` 的 `$(x)/(y)/(z)` | `transposition/cleanup` |
| `transposition` | `caster` | string(UUID) | `transposition/ready:19`（经 `lib/uuid/get_caster-player`） | `cleanup` 的 `$(caster)` | 同上 |

所有清理均为**守卫式**，避免 `data remove` 对不存在路径抛 `ERROR_MERGE_UNCHANGED`：

```mcfunction
execute if data storage wing_kirin:ram <root> run data remove storage wing_kirin:ram <root>
```

## 3. `wing_kirin:uuid` 字段

| 字段 | 类型 | 生产者 | 消费者 | 清理点 |
|---|---|---|---|---|
| `main.in` | IntArray（4×int） | 调用方，或 `lib/uuid/get_caster-player` / `get_caster-marker` | `lib/uuid/dec_to_hex` | `lifecycle/reset` |
| `main.out` | string（带连字符 UUID） | `lib/uuid/dec_to_hex` | 调用方**紧随其后**读取 | 同上 |
| `temp` | Compound（16 个 int） | `dec_to_hex` | `zzz/dec_to_hex/{to_hex,output}` | 同上 |
| `hex_map` | String[256] | `lifecycle/constants` | `zzz/dec_to_hex/to_hex` | `lifecycle/uninstall` |

**契约**：`dec_to_hex` 是纯转换器，输入 `main.in`、输出 `main.out`。因为只有一个输出槽，调用方必须**在调用后立即** `data modify ... set from storage wing_kirin:uuid main.out`（或经 `with storage` 作为宏参数），中间不得插入任何其他 UUID 转换。

## 4. 共享管线：`ram math.result`

`ram math.result` 不属于任何技能，是 `lib/entity/damage_bounce` 四件套的**入参兼出参**：

```
caculate_damage:  math.result ← projectile_level × 15
  → damage_through_strength_effect     (可 return fail，失败则不改写)
  → damage_through_sharpness_enchantment
  → damage_through_density_enchantment
  → damage_through_great_zhengqi_effect
  → 读回 math.result 写入实体 data.damage
```

四条 helper 顺序执行、互不嵌套，因此同名槽安全。**约束**：任何新增调用方都必须「先写后读、单链闭合」，不得在 helper 之间插入其他读写 `math.result` 的逻辑，也不得跨 tick 持有该值。

## 5. 已知残留（已评估，可接受）

| 残留 | 原因 | 影响 |
|---|---|---|
| `signal_arrow.level` | 充能显示函数 `radiu_display/charging/main` 内含 5 条 `run return` 早退，无法在函数内收口；清理放在下一次成功施法的 `spawn_marker/main` | 充能中断后残留 ≤1 个施法周期，无消费者 |
| `uuid.main.in/out/temp` | 库为共享单槽，消费者需「即读即用」，无法在库内清理 | 由 `lifecycle/reset` 统一清空 |
| `transposition.x/y/z` 不记维度 | 未存维度字段 | 换维后旧坐标不会复用（`cleanup` 已清缓存）；如未来需要跨维转位，须先补维度字段 |

## 6. 新增技能的落地步骤

1. 在 `ram` 下用技能 ID 作根名，例如 `ram.<ability_id>.<field>`。
2. 生产者写完、消费者读走后，在**最后一个消费者**末尾加一行守卫式清理。
3. 若消费者内部有多条 `run return` 早退，把清理上移到调用方收口，并在注释中写明原因。
4. 需要跨 tick 的状态：写进 marker / item_display 的 `data`，或计分板；**不要**写 storage。
5. 同步更新本表第 2 节。
