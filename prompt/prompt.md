你是一位资深 Minecraft Spigot 插件架构师，熟悉现代插件开发最佳实践、主流生态（ItemsAdder、AttributePlus、MythicMobs 等）、性能优化与模块化设计。

我现在要从零开始开发一个名为 **CardAccessorySystem** 的 Spigot 插件，实现「卡牌」与「饰品」系统。  
请根据以下完整需求，制定并执行一个**分阶段、可增量交付、代码可直接运行**的开发路线。

---

## 🎯 最终目标

- 玩家可装备 **4 张卡牌**（提升基础属性）和 **2 个饰品**（提升技能伤害）
- 完全兼容 **ItemsAdder 自定义物品** 和 **原版物品**
- 深度集成 **AttributePlus**（属性 + 技能伤害）
- 提供 **GUI 管理界面**、**数据持久化**、**权限/职业扩展钩子**
- 代码结构清晰、性能良好、易于后续扩展（如合成、升级、套装）

---

## 📦 基础设定

- **插件名称**：CardAccessorySystem
- **包名**：`cn.popcraft.cardaccessory`
- **最低 Minecraft 版本**：1.16+
- **依赖策略**：
  - `softdepend: [ItemsAdder, AttributePlus]`
  - 无强制依赖，未安装时相关功能静默禁用
- **数据存储**：YAML（`player_data/{uuid}.yml`）

---

## 🗺️ 完整开发路线（分 5 阶段）

请按顺序为我生成每个阶段的**完整可运行代码**，每阶段需满足：

✅ 功能完整  
✅ 包含必要注释  
✅ 处理边界情况（null、插件未安装等）  
✅ 遵循 Bukkit 最佳实践（异步保存、事件取消判断、资源清理）

---

### 🔹 Phase 1：基础框架 + 配置加载 + ItemsAdder 兼容

**目标**：插件能启动，加载配置，生成带自定义名称/Lore 的卡牌/饰品物品。

**交付物**：
- `plugin.yml`（含 softdepend）
- 主类 `CardAccessorySystem.java`
- 配置文件：`cards.yml`、`accessories.yml`（带示例）
- 工具类：`CustomItemLoader.java`（统一加载 ItemsAdder / 原版物品）
- 模型类：`Card.java`、`Accessory.java`
- 管理器：`ItemManager.java`（根据 ID 生成 ItemStack）

**技术重点**：
- 安全调用 ItemsAdder API（检查插件是否启用）
- 支持 `item: "namespace:id"` 和 `item: "MATERIAL"` 两种格式

---

### 🔹 Phase 2：装备系统 + 属性应用 + 数据持久化

**目标**：玩家可右键装备卡牌/饰品，属性实时生效，下线保留状态。

**交付物**：
- `PersistentDataContainer` 标记系统（key: `cas:card_id`, `cas:accessory_id`）
- 监听器：`EquipListener.java`（右键装备逻辑）
- 管理器：`EquipManager.java`（槽位管理：4卡牌+2饰品）
- 数据模型：`PlayerEquipment.java`
- 数据存储：`PlayerDataManager.java`（异步 YAML 读写）
- 属性应用：通过 AttributePlus API 或 Bukkit Attribute 设置

**技术重点**：
- 使用 PDC 而非 Lore 识别物品（防作弊/修改）
- 登录时自动加载并应用装备效果
- 属性变更需考虑叠加与卸下时的还原

---

### 🔹 Phase 3：技能伤害加成 + MythicMob+Mythiclib 深度集成

**目标**：饰品能正确提升 AttributePlus 技能造成的伤害。

**交付物**：
- 监听器：`SkillDamageListener.java`
- Hook 工具：`AttributePlusHook.java`（安全调用 API）
- 伤害计算：乘算加成（如 1.15 = +15%）

**技术重点**：
- 若 AttributePlus 无公开伤害事件，则通过 `EntityDamageByEntityEvent` + metadata 判断是否为技能
- 确保加成仅作用于“技能伤害”，不影响普攻
- 支持未来扩展：按技能类型加成（预留字段）

---

### 🔹 Phase 4：GUI 管理系统

**目标**：玩家可通过指令打开卡牌/饰品管理界面。

**交付物**：
- 指令：`/cards`、`/accessories`
- GUI 类：`CardGUI.java`、`AccessoryGUI.java`
- 功能：装备预览、点击装备/卸下、Lore 显示加成效果

**技术重点**：
- GUI 动态渲染当前装备状态
- 支持“悬停预览”（Hover Lore 显示装备后属性变化）
- 防重复点击、防跨 GUI 操作

---

### 🔹 Phase 5：优化 + 扩展预留

**目标**：提升体验，为未来功能打基础。

**交付物**：
- 粒子/音效：装备时播放（可配置开关）
- 权限支持：`cas.use`、`cas.admin`
- 职业/种族限制钩子（预留接口）
- 指令：`/cas reload`、`/cas give <player> <type> <id>`
- 完整日志与错误处理

**技术重点**：
- 所有扩展点设计为“可插拔”
- 配置文件支持 `enabled: true/false` 开关
- 性能：避免每 tick 循环，使用事件驱动

---

## 📌 输出要求

- 每次只输出 **一个 Phase** 的完整代码（按我要求的阶段）
- 代码按 Java 包结构组织（`cn.popcraft.cardaccessory.xxx`）
- 包含所有必要 import
- 关键逻辑添加中文注释
- 不要省略 null 检查、try-catch、资源关闭
- 优先使用 Bukkit 异步任务、PDC、Attribute API 等现代特性

---

## ▶️ 当前指令

请从 **Phase 1** 开始，输出完整可运行的基础框架代码。