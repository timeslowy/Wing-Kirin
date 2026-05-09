# 1.5.0

> **九天来客，鸿蒙初开。**

1. 下调解锁条件；  
2. 大幅上调大部分技能等级的升级条件；  
3. 「从天而降」技能升级条件改为龙种成长；  
4. 略微增强「罡风之息」的伤害；  
5. 修正了「聚形散气」中异常的属性调整效果；  
6. 「利爪与獠牙」的击退效果将随成长等级提升；  
7. 给「罡风之息」加入较长的持续时间限制；  
8. 再更新一些描述；  
9. 新增英文翻译！

# 1.8.0

> **拨乱反正，绿芽新雨。**

1. 新增后天技能「不坏金身」；  
2. 修复技能标签注册json文件内“replace”项被意外设成“true”导致的其余龙种技能错乱问题；  
3. 重新排列技能的展示顺序；  
4. 将「龙吼功」改为先天技能；  
5. 更改「通仙心」里后天技能的获取种类；  
6. 更新对解锁条件的描述；  
7. 删去「聚形散气」的血量限制，改动相关描述；  
8. 为所有技能添加龙种判断防止冒用；  
9. 将「通仙心」的食用冷却时间缩短至3秒。  

---

1. Add new acquired ability :「Indestructible Body」;  
2. Fixed wrong replace "true" in the ability register tag json,changed it to "false"  ;  
3. Arranged the ability again;  
4. Changed 「Thunderous Shout」 to "Innate Ablity";  
5. Changed the pool of "Innate Ability" of「Heart Connectng Memories」;  
6. Deleted the limitness of health percentage of 「Instant Invisibility」,and changed its description;  
7. Adding dragon species for all Wing Kirin's abilities to avoid abilities be used by other species;  
8. Modify the cooldown of 「Heart Connecting Memories 」to 3 seconds.  

# 1.9.0

> **投石问路，抛砖引玉。**

1. 将技能「聚形散气」的“破隐一击”效果改成用龙生接口实现；  
2. 「金风玉露」的“尝试接引次数”现在将会在停止施法后重置；  
3. 「不坏金身」施法期间将在动作栏展示反震次数；  
4. 「天降正义」更名为「天地同寿」；  
5. 「天地同寿」现施法完后将在一段时间内抑制法力再生；  
6. 「平步青云」的饥饿值减耗所用方法已改为使用属性“飞行耐力”实现；  
7. 「翼麒麟的佳肴」使用所给予的「魔源涌动」效果等级提升为3级  

---

1. The effect "Breaking Invisibility  strike" of「Instant Invibility」 now it is achieved by using Dragon Survival's API but not potion effect;  
2. The "Attempted Connection Times" of 「Empyrean Wine」ability will reset it self after end casting now;  
3. The "Counter Shock Times" will be shown at actionbar when casting 「Indestructible Body」;  
4. 「Heavenly Justicce」 now will inhibit mana regeneration within a period of time after casting;  
5. The method of reducing hunger consumption of 「Fly higher」 is changed to achieve by modifing the attribute "dragonsurvival:flight_stamina";  
6. The effect "Source of Magic" from 「Wing Kirin Treat」is upgraded to amplifier 2.  

# 2.0.0

> **吐故纳新，顺势而为。**

1. 删去技能「不坏金身」里的治愈效果；  
2. 新增后天技能「回光返照」;  
3. “天地同寿“退回此前名称「天降正义」；  
4. 大幅降低「天降正义」弹射物的伤害；  
5. 「天降正义」发射成功时将显示"消息"；  
6. 调整「通仙心」的后天技能池；  
7. 将「通仙心」的冷却时间缩短至2秒；  
8. 更换了祭坛龙种选择界面中的默认外观；  
9. 信标效果里添加了龙生新版的「灵魂充盈」效果;  
10. 翼麒麟的龙魂现在有显示自定义图标；  
11. 添加新进度;  
12. 更新了种族缺陷的效果，现在还会扣除法力上限。  

---

1. Deleted the heal effect of 「Indestructible Body」;  
2. Add new acquired ability:「Last Stand」;  
3. Reduced the damage of projectile of 「Heavenly Justice」 significantly;  
4. There wii show a interesting message at actionbar after 「Heavenly Justice」 casted successfully (doge);  
5. Adjusted the pool of acquired ability of 「Heart Connecting to Memories」;  
6. Reduced the cooldown of 「Heart connecting to Memories」 to 2 seconds;  
7. Changed the default skin display at dragon altar-species choices interface. ;  
8. Add new beacon effect 「Empowered Soul」 from Dragon Survival update.  
9. Wing Kirin's soul have custom icon now;  
10. Add new advancement;  
11. Updated the penalty effect,now it will also reduce the max mana.  

# 2.1.0

> **风起青萍，微澜成浪。**

1. 使用新接口更改了部分技能的实现函数以取得更低的性能开销；  
2. 「天降正义」现在在施法后还会使所有带冷却的技能进入冷却；  
3. 「罡风之息」的伤害现在可以完全穿透护甲，同时其攻击形式改为类似冲锋枪的单次低伤害但高伤害频率；  
4. 增加「一支穿云箭」技能的箭雨范围；  
5. 提升「聚形散气」技能“破隐一击”的伤害加成；  
6. 添加新进度。  

---

1. Used new API to reduce some abilities' performance overhead;  
2. After casted 「Heavenly Justice」 will lead to all ability into 100% cooldown now;  
3. The damage of 「Uppersky Wind Breath 」 now can bypass armor,and its single damage reduced but with high frequency;  
4. Add the radius of arrow rain of 「Signal Arrow」;  
5. Elevated the damage bonus of "Breaking Invisibility Strike" of 「Instant Invisibility」;  
6. Add new advancement.  

# 2.2.0

> **扶摇直上，鲤跃龙门。**

（详见2.2.0更新日志）

# 2.3.0

> **平步青云，九天揽月。**

1. 新增可选的改变部分UI的资源包（适用于翼麒麟龙种）；  
2. 「金风玉露」和「罡风之息」不再有施法时间限制；  
3. 若在施放「金风玉露」技能时身有「浩然正气」药水效果则缩短“保底时间”至20秒；  
4. 「回光返照」触发后到期时的伤害现在是必死的；  
5. 现在「回光返照」触发效果中新增巨龙技能伤害属性（Dragon Ability Damage）和吐息范围属性提升；  
6. 「罡风之息」、「一支穿云箭」和「天降正义」现在只要身有「浩然正气」药水效果即可在其他维度施放（不绝对）；  
7. 「不坏金身」的反震伤害现在会随巨龙技能伤害属性而提升；  
8. 「九天之法」被动技能新增随技能等级提升、身有「浩然正气」效果而生效的巨龙技能属性增益；  
9. 「生于九天」新增巨龙技能伤害属性提升增益效果。  
10. 「从天而降」新增分支施法效果，专攻重锤猛击伤害（Mace Smash ）提升  

---

1. New optional resourcepacks which changed some UI for Wing Kirin ;  
2. 「Empyrean Wine」and「uppersky Wind Breath」 now has no duration limitless ;  
3. The max duration of getting Empyrean Wine can reduce to 20 seconds if player has 「Great Zhengqi」effect ;  
4. The expired deadly damage of 「Last Stand」now is Invulnerable ;  
5. There are new bounces effect of Dragon Ability Damage and Breath Range of  「Last Stand」;  
6. 「Uppersky Wind Breath」「Signal Arrow」and「Heavenly Justice」now can cast at other dimensions if player has 「Great Zhengqi」effect(not absolutely ) ;  
7.  The countershock damage of「Indestructible Body」will improve with Dragon Ability Damage ;  
8. 「Empyrean Magic」now can improve player's Dragon Ability Damage if player have 「Great Zhengqi」effect ;  
9. Added new bounce of  Dragon Ability Damame of「Born Above Empyrean」.  
10. 「Fall like meteorite」added new magic branch, which boosts Mace Smash Damage specially.  

# 2.4.0

> **春秋一度，数说新语。**

1. 增减一些「龙吼功」能破坏的方块，降低其破坏速率；  
2. 「翼麒麟的佳肴」现在可以一直吃；  
3. 删去「聚音激能」被动，因为有了新的方法；  
4. 将「从天而降」的重锤伤害增益和「金钟」的音波伤害增益提出为属性以供调用；  
5. 现在「金钟」在副手时可以提升重锤猛击伤害；  
6. 模组描述支持中文了  
7. 新增新属性和新附魔——定身抗性，可以削减「定身」药水效果被应用时的时长；  
8. 调整「通仙心」配方和补偿机制以间接降低其代价。  

---

1. Added and removed some blocks these can be broken by 「Thunderous Shout」;
2. 「Wing Kirin;s Treat」 can be always eaten now; 
3. Removed 「Sonic Boom Upgrade」because there is a new way to achieve its function now;
4. Added two attributes: Sonic Boom Damage Multiplier and Mace Smash Damage Multiplier, to supply common interface;
5. 「Golden Bell」can improve Mace Smash Damage in offhand;
6. Added new attribute and enchantment:Dingshen Resistance,which can reduce 「Dingshen」potion effect by amplifier;
7. Adjusted either the recipe and the compensation mechanism for duplicate ability of 「Heart Connecting to Memories」to reduce the high cost indirectly.

# 2.5.0

> **兼爱非攻，条理分明。**

1. 新增后天技能「仁者无敌」；  
2. 将「不坏金身」移出后天技能池，变为先天技能；  
3. 「回光返照」触发现在可以减少「不坏金身」的冷却时间；  
4. 减少「不坏金身」的冷却时间;  
5. 添加新进度并优化进度结构；  
6. 修复「聚形散气」生效期间死亡导致的施法异常锁定问题。  

---

1. Added new acquired ability 「Invincible Benevolence」;
2. Removed 「Indestructible Body」 from acquired abilities pool, it becomes innate ability;
3. 「Last Stand」 can reduce the cooldown of 「Indestructible Body」 now;
4. Reduced the cooldown of 「Indestructible Body」;
5. Added new advancements and optimized advancements'' structure;
6. Fixed the issue which cause 「Instant Invisibility」be locked abnormally when it is activing.

# 2.6.0

> **纲举目张，声声入耳。**

1. 为许多技能更新施法声音；  
2. 拓宽可抵御「偏安一隅」缺陷的食谱；  
3. 极大拓宽食谱；  
4. 现在「仁者无敌」蓄力时会使自身发出金光；  
5. 给物品添加标签以规范之。  

---

1. Updated sounds for some abilities;
2. Expanded the range of items that can recover penalty;
3. Expanded diet entries significantly;
4. Now there will be glowed yourself when charging for 「Invincible Benevolence」;
5. Regulated items by tags.

# 2.7.0

> **丛枝新桠，气象生发。**

1. 为「龙吼功」添加专有的粒子；  
2. 新增三个可选数据包；  
3. 现在天生便可使用旋冲；  
4. 调整「龙吼功」的范围与可破坏的方块；  
5. 大量调整数据结构。  

---

1. Added unique particle for 「Thunderous Shout」;
2. Added 3 new optional datapacks;
3. Now can use spin naturally;
4. Adjusted the range and broken blocks of 「Thunderous Shout」;
5. Adjusted data structure.

# 2.8.0

> **除旧迎新，快马加鞭。**

1. 将「三连箭雷」和「无所遁形」扩展技能从本体里移除；  
2. 添加新后天技能「唯快不破」；  
3. 相应调整「后天技能池」；  
4. 「奶露之合」现在可消除缓慢效果；  
5. 调整数据结构；  
6. 删去「浩然正气」提升龙技能伤害属性的效果；  
7. 「浩然正气」现在可使得翼麒麟龙玩家施法时不再消耗法力；  
8. 增加新成就.  

---

1. Removed 「Explosion Arrow」「Entity Marker」 from this to another place;
2. Added new acquired ability 「Unstoppable Speed」;
3. Adjusted 「Acquired Abilities Pool」;
4. 「Empyrean Milkwine」 now can remove SLOWNESS effect;
5. Adjusted structure of data;
6. Removed the 「Great Zhengqi」's improvement of Dragon Ability Damage;
7. Now 「Great Zhengqi」 effect can skip Wing Kirin's mana cost;
8. Added new advancement.

# 2.9.0

> **九天风起，乱云飞渡。**

**⚠：潜在的破坏性更新，请谨慎更新！**  

1. 将成长阶段重置与默认机制做出区分；   
2. 新增先天技能「返老还童」；  
3. 新物品「天仙玉露」；  
4. 「定身」效果现在能禁用交互和龙玩家的主动技能；  
5. 「唯快不破」和「从天而降」效果现已无法被常规手段治愈。  

---

**⚠：Possible Destructive Update!**
1. Reset growth stage to different from default;
2. New innate ability 「Reverse Aging」;
3. New item 「Empyrean Essence」;
4. 「Ding」 effect will disable interaction and Dragon Player's active abilities;
5. 「Unstoppable Speed」 and 「Mace Crush」 now can not be incurred with common way.

# 3.0.0

> **夏满荷塘，风送游子。**

**⚠ 强烈的破坏性更新，请慎重考虑！！**

1. 将所有技能的命名空间全部更换！  
2. 新先天技能「移形换位」；  
3. 添加有关技能使用的统计信息；   
4. 添加与统计信息使用相关的指令；  
5. 新增服务器配置，可调节一些技能的行为；
6. 优化大量数据包性能。

---

**⚠ Strongly Destructive Update, Please Consider Carefully!!**

1. Changed the namespace of all abilities!
2. New innate ability 「Teleportation」;
3. Added statistics about ability usage;
4. Added commands related to statistics usage;
5. Added server configuration to adjust the behavior of some abilities;
6. Optimized the performance of a large number of datapacks.

