#anchored eyes 把执行位置改到眼睛上 pos：^ 为实体相对坐标专用
# life：（自定义）箭矢落地后开始计算，超过1200直接消失，1198可以保留2tick
# pickup:是否可以捡起，0b=false
# uuid:{UUID:[I;0,0,0,0]}必须是这样子，否则可能初始化失败
# 将龙生自定义弹射物的json结构照搬过来
# 采用了龙生提供的自定义谓词接口里的has_uuid做归属者判断来避免友伤

execute anchored eyes run summon dragonsurvival:generic_arrow_entity ^ ^ ^ { \
    Tags:["signal_arrow","new_arrow"], \
    pickup:0b, \
    PierceLevel:0, \
    Motion:[0d,-10d,0d], \
    FallDistance: 0.0f, \
    life: 1180, \
    "general_data": { \
        "name": "dragonsurvival:pear_blossom_needles", \
        "ticking_effects": [ \
        { \
                "general_data": { \
                    "effects": [ \
                        { \
                            "effect": { \
                                "world_effect": "dragonsurvival:particle", \
                                "particle_count": 10, \
                                "particle_data": { \
                                    "particle": { \
                                        "type": "dust", \
                                        "color": [0.98,0.86,0.57], \
                                        "scale": 1.0 \
                                    }, \
                                    "horizontal_position": { \
                                        "type": "in_bounding_box" \
                                    }, \
                                    "vertical_position": { \
                                        "type": "in_bounding_box" \
                                    }, \
                                    "horizontal_velocity": { \
                                        "base": 1 \
                                    }, \
                                    "vertical_velocity": { \
                                        "base": 1 \
                                    } \
                                } \
                            } \
                        } \
                    ] \
                }, \
                "target_type": "dragonsurvival:point" \
            } \
        ], \
        "common_hit_effects": [ \
            { \
                "general_data": { \
                    "effects": [ \
                        { \
                            "effect": { \
                                "damage_type": "wing_kirin:rainstorm_arrow", \
                                "amount": 1, \
                                "entity_effect": "dragonsurvival:damage" \
                            }, \
                            "condition": { \
                                "condition":"inverted", \
                                "term":{ \
                                    "condition":"entity_properties", \
                                    "entity":"this", \
                                    "predicate": { \
                                        "type_specific": { \
                                            "type":"dragonsurvival:custom_predicates", \
                                            "has_uuid": [0,0,0,0]\
                                        } \
                                    } \
                                } \
                            } \
                        } \
                    ] \
                }, \
                "target_type": "dragonsurvival:area", \
                "radius": 2.0f \
            }, \
        ], \
        "block_hit_effects": [ \
        { \
                "block_effect": "dragonsurvival:particle", \
                "particle_data": { \
                    "particle": { \
                        "type": "minecraft:crit" \
                    }, \
                    "horizontal_position": { \
                        "type": "in_bounding_box" \
                    }, \
                    "vertical_position": { \
                        "type": "in_bounding_box" \
                    }, \
                    "horizontal_velocity": { \
                        "base": 1 \
                    }, \
                    "vertical_velocity": { \
                        "base": 1 \
                    } \
                }, \
                "particle_count": 10 \
            } \
        ], \
        "entity_hit_effects": [], \
        "entity_hit_condition": { \
            "predicate": { \
                "type_specific": { \
                    "check_for": "living_entity", \
                    "type": "dragonsurvival:entity_check_predicate" \
                } \
            }, \
            "condition": "minecraft:entity_properties", \
            "entity": "this" \
        } \
    }, \
    "type_data": { \
        "texture": { \
            "texture_entries": [ \
                { \
                    "texture_resource": "dragonsurvival:ding_arrow", "from_level": 1 \
                } \
            ] \
        } \
    } \
}

# 给上面定义的单个箭雨传入参数等
execute as @n[type=dragonsurvival:generic_arrow_entity,tag=new_arrow] at @s \
    run function wing_kirin:dragon_ability/signal_arrow/arrow_rain_spwan/set-generic_arrow_entity