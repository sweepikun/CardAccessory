当然可以！以下是为你量身定制的、**完整且可直接交付给 Qwen3-Coder 的 Prompt**，涵盖你所有已确认的需求：

- 移除 AttributePlus，全面转向 **MythicMobs + 原生属性**
- 实现 **升级/强化系统**（支持多物品、PlayerPoints 点券、非线性等级）
- 支持 **权限/职业限制**（兼容 MythicLib 职业）
- 添加 **粒子/音效**（可配置、可开关）
- 所有功能通过 **GUI 交互**（升级界面、装备管理）
- 完整兼容 **ItemsAdder 自定义物品**

---

### 🧠 **Prompt：CardAccessorySystem 插件 —— 完整扩展功能开发指令**

```prompt
你是一位高级 Spigot 插件开发者，正在为 `CardAccessorySystem`（包名：`cn.popcraft.cardaccessory`）实现以下扩展功能。插件已移除 AttributePlus，现基于 **Bukkit 原生 Attribute + MythicMobs 技能系统**。

请根据以下需求，生成**完整、可运行、配置驱动**的代码。

---

## 🎯 一、核心目标

1. **升级/强化系统**  
   - 卡牌/饰品可升级（1 → max_level）  
   - 每级可配置不同属性/技能加成  
   - 升级消耗：支持 **原版物品、ItemsAdder 物品、PlayerPoints 点券**  
   - 提供 **升级 GUI**（展示消耗、确认/取消）

2. **权限与职业限制**  
   - 支持 `permission` 节点  
   - 支持 `required_class`（兼容 MythicLib 职业）

3. **特效系统**  
   - 装备、卸下、升级时播放 **粒子 + 音效**  
   - 可在 `config.yml` 中全局开关

4. **合成系统（可选但预留）**  
   - 配置中支持 `craft` 字段（本次可仅解析，GUI 后续实现）

---

## 📁 二、配置文件结构（cards.yml / accessories.yml）

```yaml
fire_heart:
  name: "&c火焰之心"
  item: "myitems:fire_card"
  permission: "cas.card.fire_heart"
  required_class: "mage"

  # 属性（卡牌）或技能加成（饰品）
  base_attributes:
    MAX_HEALTH: 10.0

  # 升级系统
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
            id: "playerpoints"  # ← 使用 PlayerPoints
            amount: 50
      3:
        attributes:
          MAX_HEALTH: 28.0
          ATTACK_DAMAGE: 3.0
        cost_to_next:
          - type: "item"
            id: "myitems:fire_gem"
            amount: 1

  # 特效
  effects:
    on_equip:
      particles:
        - type: "FLAME"
          count: 20
      sound:
        name: "ENTITY_PLAYER_LEVELUP"
        volume: 1.0
        pitch: 1.5
    on_upgrade:
      particles:
        - type: "FIREWORKS_SPARK"
          count: 30
```

> 💡 `accessories.yml` 中 `base_attributes` 改为 `base_skill_damage_multiplier: 1.15`

---

## ⚙️ 三、技术实现要求

### 1. PlayerPoints 集成
- 检测插件是否启用：`Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")`
- 使用官方 API：
  ```java
  PlayerPointsAPI api = PlayerPointsAPI.getInstance();
  int current = api.look(uuid);
  boolean success = api.take(uuid, amount);
  ```
- 若未安装 PlayerPoints，`currency: playerpoints` 消耗视为**不可用**（升级按钮置灰）

### 2. 职业限制（MythicLib）
- 若 MythicMobs 启用，通过 `MythicPlayer.getProfession().getName()` 获取职业（转小写）
- 若未启用，忽略 `required_class`

### 3. 升级 GUI（6×9）
- 顶部：当前卡牌（带等级 Lore）
- 中部：消耗格子
  - 物品：显示实际物品图标
  - PlayerPoints：显示命名物品 `&a点券: &f{amount}`，Lore: `"来自 PlayerPoints"`
- 底部：
  - 绿色玻璃：`[ &a确认升级 ]`（仅当资源足够时可点击）
  - 红色玻璃：`[ &c取消 ]`

### 4. 特效播放
- 仅在玩家在线、未隐身、距离 < 32 格时播放
- 使用 `player.spawnParticle()` 和 `player.playSound()`
- 全局开关：`config.yml → settings.effects-enabled: true`

### 5. 数据存储
- 玩家装备的卡牌/饰品需保存 `level` 字段：
  ```yaml
  cards:
    - id: "fire_heart"
      level: 3
  ```

---

## 🗂️ 四、代码结构调整

- 新增包：`gui.upgrade`（`UpgradeCardGUI.java`, `UpgradeAccessoryGUI.java`）
- 修改：`model/Card.java`, `model/Accessory.java`（新增 upgrade、effects 字段）
- 新增：`hook/PlayerPointsHook.java`, `hook/MythicClassHook.java`
- 修改：`EquipManager.java`（检查权限/职业）、`EffectManager.java`（播放特效）
- 新增指令：`/cas upgrade <type> <id>`（打开升级 GUI）

---

## 🛡️ 五、安全与兼容性

- 所有外部插件调用必须 try-catch
- 配置字段缺失时使用默认值（如无 upgrade → level=1）
- GUI 点击事件必须取消 `InventoryClickEvent`
- 玩家退出时清除所有 AttributeModifier

---

## ▶️ 输出要求

请输出以下内容（按包结构）：
1. `model/Card.java` 和 `Accessory.java`（含 upgrade/effect 字段）
2. `hook/PlayerPointsHook.java`
3. `hook/MythicClassHook.java`
4. `gui/upgrade/UpgradeCardGUI.java`
5. `listener/EquipListener.java`（更新装备检查逻辑）
6. `config.yml` 示例（含 effects-enabled 开关）

> 注意：假设 PlayerPoints 和 MythicMobs 可通过 Maven 引入，直接 import 即可。
```

---

这个 Prompt 已覆盖你所有需求，逻辑闭环、技术明确、边界清晰。你可以直接将其交给 Qwen3-Coder，它将生成一套**生产级、可配置、高性能**的扩展系统。

需要我帮你先生成其中某个组件（比如 `PlayerPointsHook.java`）作为示例吗？