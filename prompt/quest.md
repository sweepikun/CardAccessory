当然可以！以下是一个专为 **Qwen3-Coder** 设计的清晰、结构化 Prompt，明确要求**移除所有 AttributePlus 相关代码**，并**全面重构为 MythicMobs + MythicLib 集成**，同时保留原有卡牌/饰品核心逻辑。

---

### 🧠 **Prompt：重构 CardAccessorySystem 插件 —— 全面迁移到 MythicMobs + MythicLib**

```prompt
你是一位经验丰富的 Spigot 插件开发者，熟悉 MythicMobs 和 MythicLib 的 API 与事件系统。

我现在要对 `CardAccessorySystem` 插件（包名：`cn.popcraft.cardaccessory`）进行**彻底重构**：

## 🚫 移除所有 AttributePlus 依赖
- 删除所有与 `AttributePlus` 相关的代码：
  - `hook/AttributePlusHook.java`
  - 任何对 `AttributePlus` 插件的检测、API 调用、事件监听
  - 配置文件中关于 AttributePlus 的注释或逻辑
- **不再支持 AttributePlus**，插件将完全基于 **原生 Bukkit 属性 + MythicMobs 技能系统**

## ✅ 新集成目标：MythicMobs + MythicLib

### 1. 卡牌系统（属性加成）
- 使用 **Bukkit 原生 Attribute API**（如 `GENERIC_MAX_HEALTH`, `GENERIC_ATTACK_DAMAGE`）
- 支持动态增减（装备时添加 modifier，卸下时移除）
- 属性类型由 `cards.yml` 配置，映射到标准 Bukkit Attribute：
  ```yaml
  attributes:
    MAX_HEALTH: 20.0     → Attribute.GENERIC_MAX_HEALTH
    ATTACK_DAMAGE: 5.0   → Attribute.GENERIC_ATTACK_DAMAGE
    MOVEMENT_SPEED: 0.1  → Attribute.GENERIC_MOVEMENT_SPEED
  ```

### 2. 饰品系统（技能伤害加成）
- **仅对 MythicMobs 技能生效**
- 监听 `MythicDamageEvent`（来自 MythicMobs）
- 在事件中获取攻击者（Player），查询其装备的饰品总乘数（如 1.15）
- 调用 `event.setDamage(event.getDamage() * multiplier)`

> 要求：确保 `MythicDamageEvent` 来自玩家（非召唤物），且目标为实体

### 3. 插件依赖声明
- `plugin.yml` 中：
  ```yaml
  softdepend: [MythicMobs]
  ```
- 启动时检测 MythicMobs 是否启用：
  ```java
  boolean mythicEnabled = Bukkit.getPluginManager().isPluginEnabled("MythicMobs");
  ```
- 若未安装 MythicMobs：
  - 饰品系统**静默禁用**（不报错）
  - 卡牌系统**仍正常工作**（基于原生属性）

## 🧱 代码结构调整

- 删除 `hook/` 包下所有 AttributePlus 相关类
- 新增 `hook/MythicMobsHook.java`（可选，用于集中管理）
- 修改 `listener/SkillDamageListener.java` → 改为监听 `MythicDamageEvent`
- 修改 `EffectManager.java` → 移除 AttributePlus 逻辑，仅保留 Bukkit Attribute 操作
- 更新 `EquipManager` 和 `DataManager` 中的注释，移除 AttributePlus 提及

## 📦 配置文件保持不变
- `cards.yml` 和 `accessories.yml` 结构不变
- 仅解释逻辑变更：`attributes` 现在映射到 Bukkit Attribute

## 🛡️ 安全与兼容性
- 所有 MythicMobs 调用必须检查插件是否启用
- 所有事件监听器必须通过 `isPluginEnabled("MythicMobs")` 动态注册
- 玩家下线时，**必须清除所有 AttributeModifier**，防止残留

## ▶️ 输出要求
请输出以下内容：
1. 更新后的 `plugin.yml`
2. 完整的 `listener/MythicDamageListener.java`
3. 修改后的 `EffectManager.java`（仅含 Bukkit Attribute 逻辑）
4. 清理后的 `EquipManager.java`（移除 AttributePlus 相关调用）
5. 可选：`hook/MythicMobsHook.java`（若有助于组织代码）

> 注意：假设 MythicMobs 的 `MythicDamageEvent` 可通过 Maven 引入（groupId: `io.lumine`, artifactId: `Mythic-Dist`），代码中直接 import 即可。
```

---

这个 Prompt 明确、果断、技术细节清晰，能确保 Qwen3-Coder **彻底移除黑盒依赖**，转向一个**开源、稳定、事件明确**的生态。你可以直接使用它来生成重构代码。需要我帮你先生成其中某一部分吗？