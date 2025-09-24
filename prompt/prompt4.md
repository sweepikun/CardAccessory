

---

### 🧠 **Prompt：CardAccessorySystem 插件 —— 下一步开发指令（Phase: 完善核心逻辑）**

```prompt
你是一位精通 Spigot 插件开发与 Minecraft 生态集成的高级工程师。  
我正在开发 `CardAccessorySystem` 插件（包名：`cn.popcraft.cardaccessory`），目前已完成基础框架、配置加载、ItemsAdder 兼容、GUI 界面和命令系统。

但以下关键功能仍需完善，请**优先实现以下三项核心任务**，输出**可直接集成到现有项目**的代码：

---

## 🎯 任务 1：实现 NBT 标签系统（使用 PersistentDataContainer）

### 背景
当前通过物品类型判断卡牌/饰品，极易误判。需改用 **PersistentDataContainer (PDC)** 进行精确标识。

### 要求
- 定义两个命名空间键（NamespacedKey）：
  - 卡牌：`cas:card_id` → 值为卡牌 ID（如 `"fire_heart"`）
  - 饰品：`cas:accessory_id` → 值为饰品 ID（如 `"thunder_amulet"`）
- 在 `ItemManager` 生成物品时，自动写入对应 PDC 标签
- 在 `EquipListener` 和 GUI 中，**仅通过 PDC 判断**物品是否为有效卡牌/饰品
- 卸下/替换时，确保旧物品的 PDC 不影响新逻辑

### 交付物
- 工具类：`NBTKeys.java`（集中管理 NamespacedKey）
- 修改 `ItemManager.java`：生成物品时写入 PDC
- 修改所有识别逻辑（监听器、GUI）：使用 `item.getItemMeta().getPersistentDataContainer().has(KEY)` 判断

---

## 🎯 任务 2：完善 AttributePlus 属性集成（真实 API 调用）

### 背景
当前属性应用仅为占位符。需根据 **AttributePlus 实际 API** 实现属性动态增减。

### 要求
- 创建 `AttributePlusHook.java`（位于 `hook` 包）
- 安全检测 AttributePlus 是否启用（`Bukkit.getPluginManager().isPluginEnabled("AttributePlus")`）
- 实现方法：
  ```java
  public static void applyAttribute(Player player, String attributeName, double value)
  public static void removeAttribute(Player player, String attributeName, double value)
  ```
- 属性名需与 `cards.yml` 中的 `attributes` 键一致（如 `"MAX_HEALTH"`）
- 支持**叠加**：同一属性多张卡牌可累加（如两张 +10 HP 卡 = +20 HP）
- 玩家退出时**自动移除所有临时属性**

> 💡 若 AttributePlus 无公开 API，请使用反射或监听其事件；若完全不可用，则回退到 Bukkit Attribute（如 `GENERIC_MAX_HEALTH`）

### 交付物
- `hook/AttributePlusHook.java`
- 修改 `EffectManager.java`：调用上述方法应用/移除属性
- 在 `EquipManager` 装备/卸下时触发属性变更

---

## 🎯 任务 3：实现 GUI 完整交互（装备/卸下/槽位管理）

### 背景
当前 GUI 仅为静态展示，需实现完整交互逻辑。

### 要求
- **卡牌 GUI（6×9）**：
  - 槽位 0-3：当前装备的 4 张卡牌（可点击卸下）
  - 其余格子：背包中可装备的卡牌（点击装备到第一个空槽）
- **饰品 GUI（6×9）**：
  - 槽位 0-1：当前装备的 2 个饰品
  - 其余格子：可装备饰品
- **交互规则**：
  - 点击已装备卡牌 → 卸下并返还物品到背包
  - 点击未装备卡牌 → 装备到第一个空槽（若满则提示）
  - 自动刷新 GUI（装备后立即更新界面）
  - 防止重复点击（取消 InventoryClickEvent）

### 交付物
- 修改 `gui/CardGUI.java` 和 `gui/AccessoryGUI.java`
- 实现 `onItemClick` 逻辑（区分“装备区”和“背包区”）
- 调用 `EquipManager.equipCard()` / `unequipCard()` 等方法
- 添加提示消息（如“&c卡牌槽已满！”）

---

## 📌 通用要求

- **包结构**：严格遵循 `cn.popcraft.cardaccessory.{manager,model,util,...}`
- **错误处理**：
  - 所有外部 API 调用需 try-catch
  - 插件未安装时静默跳过，不报错
- **性能**：
  - GUI 点击事件中避免阻塞操作
  - 属性变更使用异步保存（但属性应用本身需同步）
- **代码风格**：
  - 关键逻辑添加中文注释
  - 使用 `final`、`private` 等修饰符
  - 避免魔法值（如槽位索引定义为常量）

---

## ▶️ 输出指令

请按顺序输出以下内容：
1. `util/NBTKeys.java`
2. 修改后的 `ItemManager.java`（仅展示 PDC 相关部分，或完整类）
3. `hook/AttributePlusHook.java`
4. 修改后的 `EffectManager.java`（属性应用逻辑）
5. 完整的 `gui/CardGUI.java` 和 `gui/AccessoryGUI.java`（含交互逻辑）

> 注意：假设 AttributePlus 的属性设置 API 为 `AttributePlusAPI.setAttribute(Player, String, double)`，若实际不同，请使用通用反射方案并添加 TODO 注释。
```

