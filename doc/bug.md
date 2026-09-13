### 长矛`突进`附魔对龙玩家不起作用

龙玩家无法触发长矛的`突进`作用附魔。  
~（当然，也许龙玩家本就不该能在生存模式下持有长矛，但目前可以）~

### 龙玩家无法被技能的实体效果（Entity Effect）`dragonsurvival:glow`所应用特定颜色发光效果

一个简单的技能示例：

```Json
    {
        "actions": [
            {
                "target_selection": {
                    "applied_effects": {
                        "entity_effect": [
                            {
                                "effect_type": "dragonsurvival:glow",
                                "glows": [
                                    {
                                        "base": {
                                            "id": "dragonsurvival:test_glow",
                                            "duration": 40
                                        },
                                        "color": "#fbdc92"
                                    }
                                ]
                            }
                        ],
                        "targeting_mode": "allies_and_self"
                    },
                    "target_type": "dragonsurvival:area",
                    "radius": 5
                }
            }
        ],
        "activation": {
            "activation_type": "dragonsurvival:simple",
            "cast_time": 60,
            "initial_mana_cost": 1,
            "animations": {
                "start_and_charging": {
                    "animation_key": "cast_mass_buff",
                    "layer": "BASE",
                    "locks_neck": false,
                    "locks_tail": false,
                    "transition_length": 5
                },
                "end": {
                    "animation_key": "mass_buff",
                    "layer": "BASE",
                    "locks_tail": false,
                    "locks_neck": false,
                    "transition_length": 5
                }
            }
        },
        "icon": {
            "texture_entries": [
                {
                    "from_level": 0,
                    "texture_resource": "dragonsurvival:test"
                }
            ]
        }
    }

```
其他生物会正确显示发光效果，但龙玩家自身则毫无反应。

### 食谱(Diet Entries)覆盖的食物会同时修改其食用动画和音效

比如这个例子：

```Java
    public class ExampleDrinkItem extends Item {
        public ExampleDrinkItem(Identifier id) {
            super(new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(16)
                .rarity(Rarity.UNCOMMON)
                .food(new FoodProperties.Builder()
                        .nutrition(4)
                        .saturationModifier(2f)
                        .alwaysEdible()
                        .build(),
                        Consumable.builder()
                                .consumeSeconds(0.8F)
                                .animation(ItemUseAnimation.DRINK)
                                .sound(SoundEvents.GENERIC_DRINK)
                                .build())
                .usingConvertsTo(Items.GLASS_BOTTLE));
        }
    }
```

此物品指定了饮用的食用方式为`饮用`及其对应声音，但在龙食谱覆盖的情况下变为默认的`吃`动作，而此问题在`1.21.1`版本中并不存在。

### (通用)同时作为食物和生长物的物品会导致食用效果重叠

假如一个物品既是龙玩家的特定食谱中食物又是其阶段生长的物品，导致试图食用时会**消耗双份**，一份吃掉，另一份变为生长值。  
希望能通过增加专门配置来分离两种行为，例如，在食用同时拥有双重属性的食物时，需要同时按下`鼠标右键`和`左Shift`键才能使用其生长作用，且同时不触发食用作用。

---

### The Spear's `Lunge` enchantment does not work on dragon players

Dragon players cannot trigger the Trident's `Impaling` effect enchantment.  
~(Of course, dragon players perhaps shouldn't be able to hold a spear in survival mode in the first place, but currently they can)~

### Dragon players cannot have the specific-colored glow effect applied via the ability's Entity Effect `dragonsurvival:glow`

A simple ability example:

```Json
    {
        "actions": [
            {
                "target_selection": {
                    "applied_effects": {
                        "entity_effect": [
                            {
                                "effect_type": "dragonsurvival:glow",
                                "glows": [
                                    {
                                        "base": {
                                            "id": "dragonsurvival:test_glow",
                                            "duration": 40
                                        },
                                        "color": "#fbdc92"
                                    }
                                ]
                            }
                        ],
                        "targeting_mode": "allies_and_self"
                    },
                    "target_type": "dragonsurvival:area",
                    "radius": 5
                }
            }
        ],
        "activation": {
            "activation_type": "dragonsurvival:simple",
            "cast_time": 60,
            "initial_mana_cost": 1,
            "animations": {
                "start_and_charging": {
                    "animation_key": "cast_mass_buff",
                    "layer": "BASE",
                    "locks_neck": false,
                    "locks_tail": false,
                    "transition_length": 5
                },
                "end": {
                    "animation_key": "mass_buff",
                    "layer": "BASE",
                    "locks_tail": false,
                    "locks_neck": false,
                    "transition_length": 5
                }
            }
        },
        "icon": {
            "texture_entries": [
                {
                    "from_level": 0,
                    "texture_resource": "dragonsurvival:test"
                }
            ]
        }
    }

```
Other creatures correctly display the glow effect, but the dragon player themselves shows no reaction.

### Foods overridden by Diet Entries also modify their eating animation and sound

For example, this case:

```Java
    public class ExampleDrinkItem extends Item {
        public ExampleDrinkItem(Identifier id) {
            super(new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(16)
                .rarity(Rarity.UNCOMMON)
                .food(new FoodProperties.Builder()
                        .nutrition(4)
                        .saturationModifier(2f)
                        .alwaysEdible()
                        .build(),
                        Consumable.builder()
                                .consumeSeconds(0.8F)
                                .animation(ItemUseAnimation.DRINK)
                                .sound(SoundEvents.GENERIC_DRINK)
                                .build())
                .usingConvertsTo(Items.GLASS_BOTTLE));
        }
    }
```

This item specifies the `drinking` consumption method and its corresponding sound, but when overridden by a dragon diet entry, it changes to the default `eat` action. This issue does not exist in version `1.21.1`.

### (General) Items that are both food and growth items cause overlapping consumption effects

If an item is both food in a dragon player's specific diet and a stage-growth item, attempting to consume it will **consume double** — one portion eaten, the other converted into growth value.  
It is hoped that a dedicated configuration could separate the two behaviors. For example, when consuming a food with both properties above, the player would need to hold both `right click` and `left Shift` to use its growth effect, without triggering the eating effect.
