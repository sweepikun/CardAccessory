# CardAccessorySystem

一个为Minecraft Spigot服务器设计的插件，用于扩展玩家装备系统，支持卡片和饰品的自定义功能。

## 📋 插件介绍

CardAccessorySystem允许玩家装备卡片和饰品来增强他们的能力。该插件支持：
- 卡片系统：提供属性加成（如生命值、攻击力等）
- 饰品系统：提供技能伤害加成
- GUI管理界面：方便玩家装备和管理物品
- 物品升级系统：通过消耗资源提升物品等级
- 与主流插件集成：MythicMobs、Vault、PlayerPoints等

## 🚀 功能特性

### 卡片系统
- 玩家可以装备最多4张卡片
- 卡片提供各种属性加成（生命值、攻击力、移动速度等）
- 支持Bukkit原生属性系统

### 饰品系统
- 玩家可以装备最多2个饰品
- 饰品提供技能伤害加成
- 与MythicMobs技能系统深度集成

### 升级系统
- 卡片和饰品支持多级升级
- 升级消耗可配置（物品、PlayerPoints、金币等）
- 图形化升级界面

### GUI界面
- 卡片管理界面（/cas cards）
- 饰品管理界面（/cas accessories）
- 升级界面（/cas upgrade <type> <id>）

## ⚙️ 安装方法

1. 将编译好的 `CardAccessorySystem-x.x.x.jar` 文件放入服务器的 `plugins` 文件夹
2. 启动服务器，插件会自动生成配置文件
3. 根据需要修改配置文件
4. 重启服务器使配置生效

## 📖 使用方法

### 基本命令
```
/cas - 主命令
/cas cards - 打开卡片管理界面
/cas accessories - 打开饰品管理界面
/cas upgrade <type> <id> - 打开升级界面
/cas reload - 重载配置文件
```

### 装备物品
1. 获得卡片或饰品（通过指令或其他方式）
2. 手持物品右键即可装备
3. 或者使用GUI界面进行装备管理

### 升级物品
1. 使用 `/cas upgrade card <卡片ID>` 或 `/cas upgrade accessory <饰品ID>` 打开升级界面
2. 查看升级所需的消耗
3. 确保拥有足够的资源
4. 点击确认按钮完成升级

## ⚙️ 配置文件

插件包含以下配置文件：

### config.yml
主配置文件，包含插件的基本设置：
```yaml
# 插件设置
settings:
  # 是否启用特效
  effects-enabled: true
```

### cards.yml
卡片配置文件，定义所有可用的卡片：
```yaml
cards:
  fire_heart:
    name: "&c火焰之心"
    lore:
      - "&7提供生命值加成"
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
```

### accessories.yml
饰品配置文件，定义所有可用的饰品：
```yaml
accessories:
  thunder_amulet:
    name: "&e雷电护符"
    lore:
      - "&7提供技能伤害加成"
    item: "PAPER"
    base_skill_damage_multiplier: 1.15
    permission: "cas.accessory.thunder_amulet"
    required_class: "mage"
    upgrade:
      max_level: 5
      levels:
        2:
          skill_damage_multiplier: 1.30
          cost_to_next:
            - type: "item"
              id: "myitems:thunder_gem"
              amount: 1
```

### 详细配置说明
有关配置文件的详细说明，请参阅 [CONFIGURATION.md](CONFIGURATION.md) 文件。

## 🔌 插件依赖

### 必需依赖
- Spigot/Paper 1.16.5 服务器

### 可选依赖
- **MythicMobs**: 提供技能伤害支持
- **Vault**: 提供经济系统支持
- **PlayerPoints**: 提供点券系统支持
- **ItemsAdder**: 提供自定义物品支持

## 🔧 权限节点

```
cas.use - 使用基本命令
cas.admin - 管理员权限（重载配置等）
```

## 🎯 API集成

### MythicMobs集成
插件与MythicMobs技能系统集成，饰品提供的技能伤害加成会自动应用于MythicMobs技能。

### Vault集成
支持使用Vault经济系统进行升级消耗。

### PlayerPoints集成
支持使用PlayerPoints点券系统进行升级消耗。

## 🛠 开发者信息

### 构建项目
```bash
gradle build
```

### 项目结构
```
src/main/java/cn/popcraft/cardaccessory/
├── command/          # 命令处理
├── gui/              # 图形界面
│   └── upgrade/      # 升级界面
├── hook/             # 插件钩子
├── listener/         # 事件监听器
├── manager/          # 管理器
├── model/            # 数据模型
└── util/             # 工具类
```

## 📄 许可证

本项目采用MIT许可证，详情请见 [LICENSE](LICENSE) 文件。