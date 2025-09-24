非常好的思考！你已经触及了**装备成长系统**的核心设计问题。下面我将为你系统梳理 **升级/合成系统的完整逻辑模型**，涵盖你提到的所有关键点：

---

## 🧠 一、核心理念：**“配置驱动 + 灵活扩展”**

所有逻辑由配置文件定义，代码只负责**解析 + 执行**，不硬编码任何物品、货币或等级规则。

---

## 📦 二、升级系统（Upgrade System）详细设计

### 1. **升级消耗支持多种类型**
在 `cost_per_level` 中，每项消耗可声明类型：

```yaml
upgrade:
  max_level: 5
  cost_per_level:
    - type: "item"
      id: "EMERALD"          # 原版物品
      amount: 3
    - type: "item"
      id: "myitems:fire_gem" # ItemsAdder 物品
      amount: 1
    - type: "currency"
      id: "coins"            # 自定义货币ID（如 coins, tokens）
      amount: 100
    - type: "currency"
      id: "points"
      amount: 50
```

> 💡 `type: "item"` → 检查玩家背包  
> `type: "currency"` → 调用经济/点券插件 API（如 Vault、自定义点券系统）

---

### 2. **不同等级不同效果（非线性成长）**
支持为**每个等级单独配置属性**，而非简单线性叠加：

```yaml
upgrade:
  levels:
    1:
      attributes:
        MAX_HEALTH: 10.0
      cost_to_next: []  # 初始无消耗
    2:
      attributes:
        MAX_HEALTH: 18.0  # +8（非+10）
      cost_to_next:
        - type: "item"
          id: "EMERALD"
          amount: 2
    3:
      attributes:
        MAX_HEALTH: 28.0
        ATTACK_DAMAGE: 3.0  # 新增属性！
      cost_to_next:
        - type: "currency"
          id: "coins"
          amount: 200
```

> ✅ 优势：可设计“质变”节点（如 3 级解锁新属性）

---

### 3. **升级 GUI 设计**

#### 布局（6×9）：
- **顶部 1 行**：当前卡牌（带当前等级 Lore）
- **中间 3 行**：消耗物品展示格（物品图标 + 数量）
  - 若为货币：用 **命名物品** 表示（如 “&a金币: &f100”），Lore 显示来源
- **底部 2 行**：
  - `[ 确认 ]` 按钮（绿色染色玻璃）
  - `[ 取消 ]` 按钮（红色染色玻璃）

#### 交互逻辑：
- 点击“确认”：
  1. 检查玩家是否拥有所有消耗
  2. 扣除物品/货币
  3. 卡牌等级 +1
  4. 关闭 GUI，播放升级特效
- 点击“取消”：关闭 GUI

> 🔒 安全：每次打开 GUI 时**实时计算消耗**（防止配置热重载后不一致）

---

## 🔗 三、合成系统（Crafting System）增强设计

### 1. **支持等级限制**
合成配方可要求输入物品的**最低/精确等级**：

```yaml
craft:
  requirements:
    input_level_min: 2        # 输入卡牌至少 2 级
    input_level_exact: false  # false=≥2, true=必须=2
  recipe:
    - "AAA"
    - "ABA"
    - "AAA"
  ingredients:
    A: "fire_heart"   # 必须是 fire_heart 且满足等级
    B: "DIAMOND"
  result:
    id: "inferno_heart"
    level: 1          # 合成后固定为 1 级（或 inherit）
```

### 2. **合成 GUI**
- 类似工作台，但支持：
  - 拖入卡牌/饰品（自动校验等级）
  - 显示“合成结果预览”
  - 点击“合成”按钮执行

---

## 💰 四、货币/点券系统集成策略

### 方案：**抽象“支付处理器”**
```java
public interface PaymentHandler {
    boolean has(Player player, String currencyId, int amount);
    void withdraw(Player player, String currencyId, int amount);
    String getDisplayName(String currencyId); // 用于 GUI 显示
}
```

#### 实现类：
- `VaultEconomyHandler` → 处理金币（通过 Vault）
- `CustomPointHandler` → 处理点券（如从你的插件或 PointsAPI 读取）
- `ItemPaymentHandler` → 处理物品

> ✅ 管理员在 `config.yml` 中声明货币映射：
> ```yaml
> currencies:
>   coins: "vault"      # 使用 Vault 经济
>   points: "custom"    # 使用自定义点券
> ```

---

## 🗂️ 五、配置文件最终结构示例（cards.yml）

```yaml
fire_heart:
  name: "&c火焰之心"
  item: "myitems:fire_card"
  permission: "cas.card.fire_heart"
  required_class: "mage"

  # 基础属性（level 1）
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
            id: "coins"
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
            id: "points"
            amount: 30

  # 合成（可选）
  craft:
    input_level_min: 3
    recipe:
      - "FFF"
      - "FEF"
      - "FFF"
    ingredients:
      F: "fire_heart"
      E: "ENDER_PEARL"
    result:
      id: "inferno_heart"
      level: 1

  # 特效
  effects:
    on_upgrade:
      particles:
        - type: "FIREWORKS_SPARK"
          count: 30
      sound:
        name: "ENTITY_PLAYER_LEVELUP"
```

---

## ✅ 六、下一步行动

这个设计已覆盖：
- 多类型消耗（物品/金币/点券）
- 非线性等级成长
- 合成等级限制
- 升级/合成 GUI
- 经济系统解耦

**现在可以生成给 Qwen3-Coder 的 Prompt 了**。需要我为你写出这个完整 Prompt 吗？
## 注：使用playerpoints插件。