# CardAccessorySystem 配置文档

本插件包含多个配置文件，用于定义卡片、饰品、插件行为等。本文档将详细介绍每个配置文件的结构和配置选项。

## 目录结构

```
plugins/CardAccessorySystem/
├── config.yml           # 主配置文件
├── cards.yml            # 卡片定义文件
├── accessories.yml      # 饰品定义文件
└── player_data/         # 玩家数据目录（自动生成）
```

## config.yml - 主配置文件

主配置文件控制插件的基本行为。

```yaml
# 插件设置
settings:
  # 是否启用特效（粒子效果和声音）
  effects-enabled: true
```

### 配置项详解

- `settings.effects-enabled`: 
  - 类型: Boolean (true/false)
  - 默认值: true
  - 说明: 控制是否启用装备、卸下和升级时的粒子效果和声音效果

## cards.yml - 卡片配置文件

卡片提供属性加成，如生命值、攻击力等。每个卡片定义包含以下字段：

```yaml
cards:
  # 卡片ID（唯一标识符）
  fire_heart:
    # 显示名称（支持颜色代码）
    name: "&c火焰之心"
    
    # 描述Lore（支持颜色代码，每行一个元素）
    lore:
      - "&7古老的火焰之心"
      - "&7蕴含着强大的力量"
      
    # 物品标识符（支持原版物品或ItemsAdder自定义物品）
    item: "PAPER"
    # 或使用ItemsAdder物品: item: "myitems:fire_card"
    
    # 权限节点（可选，空字符串表示无需权限）
    permission: "cas.card.fire_heart"
    
    # 职业限制（可选，需要MythicLib支持）
    required_class: "mage"
    
    # 基础属性加成
    base_attributes:
      MAX_HEALTH: 10.0
      ATTACK_DAMAGE: 2.0
      
    # 升级系统配置
    upgrade:
      # 最大等级
      max_level: 5
      
      # 各等级的配置
      levels:
        # 等级2的配置
        2:
          # 该等级的属性加成
          attributes:
            MAX_HEALTH: 18.0
            ATTACK_DAMAGE: 3.0
            
          # 升级到下一级所需的消耗
          cost_to_next:
            # 物品消耗示例
            - type: "item"
              id: "EMERALD"
              amount: 2
              
            # PlayerPoints消耗示例
            - type: "currency"
              id: "playerpoints"
              amount: 50
              
            # Vault经济消耗示例
            - type: "currency"
              id: "coins"
              amount: 1000
              
            # ItemsAdder自定义物品消耗示例
            - type: "item"
              id: "myitems:fire_gem"
              amount: 1
              
        # 等级3的配置
        3:
          attributes:
            MAX_HEALTH: 28.0
            ATTACK_DAMAGE: 5.0
          cost_to_next:
            - type: "item"
              id: "DIAMOND"
              amount: 1
            - type: "currency"
              id: "playerpoints"
              amount: 100
```

### 字段详解

#### 基本字段

- `name`: 
  - 类型: String
  - 说明: 卡片的显示名称，支持Minecraft颜色代码（&）
  
- `lore`: 
  - 类型: String Array
  - 说明: 卡片的描述信息，每行一个元素，支持颜色代码
  
- `item`: 
  - 类型: String
  - 说明: 卡片使用的物品标识符，可以是原版物品（如"PAPER"）或ItemsAdder自定义物品（如"myitems:fire_card"）
  
- `permission`: 
  - 类型: String
  - 说明: 玩家需要拥有的权限节点才能装备该卡片，留空表示无权限限制
  
- `required_class`: 
  - 类型: String
  - 说明: 玩家需要拥有的职业才能装备该卡片（需要MythicLib支持），留空表示无职业限制

#### 属性字段

- `base_attributes`: 
  - 类型: Key-Value Map
  - 说明: 卡片的基础属性加成
  - 支持的属性:
    - `MAX_HEALTH`: 最大生命值
    - `ATTACK_DAMAGE`: 攻击伤害
    - `MOVEMENT_SPEED`: 移动速度

#### 升级字段

- `upgrade.max_level`: 
  - 类型: Integer
  - 说明: 卡片的最大等级
  
- `upgrade.levels.[level].attributes`: 
  - 类型: Key-Value Map
  - 说明: 指定等级的属性加成，结构与base_attributes相同
  
- `upgrade.levels.[level].cost_to_next`: 
  - 类型: Object Array
  - 说明: 升级到下一级所需的消耗列表
  - 消耗对象字段:
    - `type`: 消耗类型，"item"（物品）或"currency"（货币）
    - `id`: 物品ID或货币类型（"playerpoints"或"coins"）
    - `amount`: 消耗数量

## accessories.yml - 饰品配置文件

饰品提供技能伤害加成，主要与MythicMobs技能系统配合使用。

```yaml
accessories:
  # 饰品ID（唯一标识符）
  thunder_amulet:
    # 显示名称（支持颜色代码）
    name: "&e雷电护符"
    
    # 描述Lore（支持颜色代码，每行一个元素）
    lore:
      - "&7闪烁着雷电的护符"
      - "&7增强你的技能威力"
      
    # 物品标识符（支持原版物品或ItemsAdder自定义物品）
    item: "PAPER"
    # 或使用ItemsAdder物品: item: "myitems:thunder_amulet"
    
    # 基础技能伤害倍数
    base_skill_damage_multiplier: 1.15
    
    # 权限节点（可选，空字符串表示无需权限）
    permission: "cas.accessory.thunder_amulet"
    
    # 职业限制（可选，需要MythicLib支持）
    required_class: "mage"
    
    # 饰品效果（新功能）
    effects:
      # 效果ID（唯一标识符）
      skill_damage_1:
        # 效果类型
        type: "skill_damage"
        # 效果选项
        options:
          multiplier: 1.15
          
      # 暴击效果示例
      critical_1:
        type: "critical_chance"
        options:
          chance: 0.1
          multiplier: 1.5
    
    # 升级系统配置
    upgrade:
      # 最大等级
      max_level: 5
      
      # 各等级的配置
      levels:
        # 等级2的配置
        2:
          # 该等级的技能伤害倍数
          skill_damage_multiplier: 1.30
          
          # 升级到下一级所需的消耗
          cost_to_next:
            # 物品消耗示例
            - type: "item"
              id: "EMERALD"
              amount: 2
              
            # PlayerPoints消耗示例
            - type: "currency"
              id: "playerpoints"
              amount: 50
```

### 字段详解

#### 基本字段

- `name`: 
  - 类型: String
  - 说明: 饰品的显示名称，支持Minecraft颜色代码（&）
  
- `lore`: 
  - 类型: String Array
  - 说明: 饰品的描述信息，每行一个元素，支持颜色代码
  
- `item`: 
  - 类型: String
  - 说明: 饰品使用的物品标识符，可以是原版物品（如"PAPER"）或ItemsAdder自定义物品（如"myitems:thunder_amulet"）
  
- `base_skill_damage_multiplier`: 
  - 类型: Decimal
  - 说明: 基础技能伤害倍数（1.0表示无加成，1.15表示+15%伤害）
  
- `permission`: 
  - 类型: String
  - 说明: 玩家需要拥有的权限节点才能装备该饰品，留空表示无权限限制
  
- `required_class`: 
  - 类型: String
  - 说明: 玩家需要拥有的职业才能装备该饰品（需要MythicLib支持），留空表示无职业限制

#### 效果字段（新功能）

- `effects`: 
  - 类型: Object Map
  - 说明: 饰品提供的各种效果
  - 效果对象字段:
    - `type`: 效果类型（见下文效果类型列表）
    - `target`: 效果目标（可选）
    - `options`: 效果选项（根据效果类型不同而不同）

##### 支持的效果类型

1. `skill_damage` - 技能伤害加成
   - 选项:
     - `multiplier`: 伤害倍数（如1.15表示+15%伤害）
     - `skill`: 特定技能名称（可选，只对该技能生效）

2. `potion_effect` - 药水效果
   - 选项:
     - `potion`: 药水效果类型（如"INCREASE_DAMAGE"、"REGENERATION"等）
     - `amplifier`: 效果等级（0表示I级，1表示II级，以此类推）
     - `duration`: 持续时间（单位为ticks，20 ticks = 1秒）
     - `ambient`: 是否为环境效果（true/false，默认false）
     - `particles`: 是否显示粒子效果（true/false，默认true）
     - `icon`: 是否显示图标（true/false，默认true）

3. `critical_chance` - 暴击几率
   - 选项:
     - `chance`: 暴击几率（0.0-1.0之间，如0.1表示10%）
     - `multiplier`: 暴击伤害倍数（如1.5表示1.5倍伤害）

4. `life_steal` - 生命偷取
   - 选项:
     - `percentage`: 生命偷取百分比（如0.1表示造成伤害的10%转化为生命值）

#### 升级字段

- `upgrade.max_level`: 
  - 类型: Integer
  - 说明: 饰品的最大等级
  
- `upgrade.levels.[level].skill_damage_multiplier`: 
  - 类型: Decimal
  - 说明: 指定等级的技能伤害倍数
  
- `upgrade.levels.[level].cost_to_next`: 
  - 类型: Object Array
  - 说明: 升级到下一级所需的消耗列表，结构与卡片相同

## 玩家数据文件

玩家数据存储在 `player_data` 目录中，每个玩家一个文件，文件名为其UUID。

```yaml
player-equipment:
  # 卡牌槽位
  cards:
    0:
      id: "fire_heart"
      level: 3
    1:
      id: ""
      level: 1
    2:
      id: ""
      level: 1
    3:
      id: ""
      level: 1
      
  # 饰品槽位
  accessories:
    0:
      id: "thunder_amulet"
      level: 2
    1:
      id: ""
      level: 1
```

## 配置示例

### 完整的cards.yml示例

```yaml
cards:
  fire_heart:
    name: "&c火焰之心"
    lore:
      - "&7古老的火焰之心"
      - "&7蕴含着强大的力量"
    item: "PAPER"
    permission: "cas.card.fire_heart"
    required_class: "mage"
    base_attributes:
      MAX_HEALTH: 10.0
    upgrade:
      max_level: 5
      levels:
        2:
          attributes:
            MAX_HEALTH: 18.0
          cost_to_next:
            - type: "item"
              id: "EMERALD"
              amount: 2
            - type: "currency"
              id: "playerpoints"
              amount: 50
        3:
          attributes:
            MAX_HEALTH: 28.0
            ATTACK_DAMAGE: 3.0
          cost_to_next:
            - type: "item"
              id: "myitems:fire_gem"
              amount: 1
            - type: "currency"
              id: "coins"
              amount: 1000

  ice_brain:
    name: "&b寒冰之脑"
    lore:
      - "&7蕴含极地智慧的宝石"
      - "&7提升你的思维能力"
    item: "PRISMARINE_SHARD"
    permission: ""
    required_class: ""
    base_attributes:
      ATTACK_DAMAGE: 2.0
      MOVEMENT_SPEED: 0.05
    upgrade:
      max_level: 3
      levels:
        2:
          attributes:
            ATTACK_DAMAGE: 4.0
            MOVEMENT_SPEED: 0.1
          cost_to_next:
            - type: "item"
              id: "ICE"
              amount: 10
```

### 完整的accessories.yml示例

```yaml
accessories:
  thunder_amulet:
    name: "&e雷电护符"
    lore:
      - "&7闪烁着雷电的护符"
      - "&7增强你的技能威力"
    item: "PAPER"
    base_skill_damage_multiplier: 1.15
    permission: "cas.accessory.thunder_amulet"
    required_class: "mage"
    effects:
      skill_damage_1:
        type: "skill_damage"
        options:
          multiplier: 1.15
      critical_1:
        type: "critical_chance"
        options:
          chance: 0.1
          multiplier: 1.5
    upgrade:
      max_level: 5
      levels:
        2:
          skill_damage_multiplier: 1.30
          cost_to_next:
            - type: "item"
              id: "EMERALD"
              amount: 2
            - type: "currency"
              id: "playerpoints"
              amount: 50
        3:
          skill_damage_multiplier: 1.50
          cost_to_next:
            - type: "item"
              id: "myitems:thunder_gem"
              amount: 1

  dragon_ring:
    name: "&d龙之戒"
    lore:
      - "&7远古巨龙的力量结晶"
      - "&7大幅提升技能伤害"
    item: "NETHERITE_INGOT"
    base_skill_damage_multiplier: 1.25
    permission: ""
    required_class: ""
    effects:
      skill_damage_1:
        type: "skill_damage"
        options:
          multiplier: 1.25
      life_steal_1:
        type: "life_steal"
        options:
          percentage: 0.1
      potion_1:
        type: "potion_effect"
        options:
          potion: "INCREASE_DAMAGE"
          amplifier: 0
          duration: 100
    upgrade:
      max_level: 3
      levels:
        2:
          skill_damage_multiplier: 1.40
          cost_to_next:
            - type: "item"
              id: "END_CRYSTAL"
              amount: 1
```

## 高级配置技巧

### 1. 使用ItemsAdder自定义物品

要使用ItemsAdder自定义物品，只需将[item](file:///c:/Users/Administrator/Desktop/repos/CardAccessorySystem/src/main/java/cn/popcraft/cardaccessory/manager/ItemManager.java#L257-L259)字段设置为ItemsAdder的命名空间ID：

```yaml
item: "myitems:legendary_sword"
```

### 2. 配置复杂的升级系统

可以为不同等级配置不同的属性和消耗：

```yaml
upgrade:
  max_level: 10
  levels:
    2:
      attributes:
        MAX_HEALTH: 20.0
      cost_to_next:
        - type: "item"
          id: "EMERALD"
          amount: 5
    5:
      attributes:
        MAX_HEALTH: 50.0
        ATTACK_DAMAGE: 5.0
      cost_to_next:
        - type: "item"
          id: "DIAMOND"
          amount: 3
        - type: "currency"
          id: "playerpoints"
          amount: 200
    10:
      attributes:
        MAX_HEALTH: 100.0
        ATTACK_DAMAGE: 10.0
        MOVEMENT_SPEED: 0.2
      cost_to_next:
        - type: "item"
          id: "NETHER_STAR"
          amount: 1
```

### 3. 混合消耗类型

可以同时配置多种类型的消耗：

```yaml
cost_to_next:
  - type: "item"
    id: "EMERALD"
    amount: 10
  - type: "currency"
    id: "playerpoints"
    amount: 100
  - type: "currency"
    id: "coins"
    amount: 1000
  - type: "item"
    id: "myitems:rare_gem"
    amount: 1
```

### 4. 使用效果系统创建复杂饰品

通过组合不同效果，可以创建功能丰富的饰品：

```yaml
effects:
  # 多重技能伤害加成
  skill_damage_1:
    type: "skill_damage"
    options:
      multiplier: 1.15
  # 暴击效果
  critical_1:
    type: "critical_chance"
    options:
      chance: 0.1
      multiplier: 1.5
  # 药水效果
  potion_1:
    type: "potion_effect"
    options:
      potion: "SPEED"
      amplifier: 1
      duration: 200
  # 生命偷取
  life_steal_1:
    type: "life_steal"
    options:
      percentage: 0.05
```

## 常见问题

### 1. 配置文件修改后不生效

修改配置文件后需要使用 `/cas reload` 命令重载配置，或重启服务器。

### 2. 物品显示为红色屏障方块

这通常是因为物品ID配置错误或ItemsAdder物品未正确加载。检查[item](file:///c:/Users/Administrator/Desktop/repos/CardAccessorySystem/src/main/java/cn/popcraft/cardaccessory/manager/ItemManager.java#L257-L259)字段是否正确，并确保ItemsAdder插件已正确加载相关资源包。

### 3. 权限节点不起作用

确保在服务器的权限管理插件中正确分配了权限节点。

### 4. 职业限制不工作

职业限制需要MythicLib支持，确保MythicMobs和MythicLib插件已正确安装并启用。

### 5. 效果不生效

确保MythicMobs插件已正确安装并启用，因为大部分效果依赖于MythicMobs的技能系统。