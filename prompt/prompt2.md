太好了！你的补充非常关键，尤其是 **兼容 ItemsAdder** —— 这将极大提升插件的灵活性和美术表现力（自定义模型、3D物品等）。

我们来更新设计，确保完全支持 **ItemsAdder 自定义物品作为卡牌/饰品**。

---

## ✅ 更新后的需求亮点

| 特性 | 说明 |
|------|------|
| **包名** | `cn.popcraft.cardaccessory` |
| **ItemsAdder 兼容** | ✅ 卡牌/饰品可使用 ItemsAdder 自定义物品（通过命名空间ID） |
| **原版物品兼容** | ✅ 也支持原版 Material（如 `DIAMOND`, `NETHERITE_SCRAP`） |
| **自动识别** | 管理员在配置文件中写 `item: myitems:fire_card` → 自动加载 ItemsAdder 物品 |
| **无缝集成** | 装备、GUI、属性加成都统一处理，无需区分来源 |

---

## 📁 配置文件升级：支持 ItemsAdder

### `cards.yml` 示例（混合原版 & ItemsAdder）

```yaml
cards:
  fire_heart:
    name: "&c火焰之心"
    lore:
      - "&7+20 最大生命值"
    # 使用 ItemsAdder 自定义物品（命名空间:ID）
    item: "mymagic:fire_card"  # ← ItemsAdder 物品ID
    attributes:
      MAX_HEALTH: 20.0

  ice_brain:
    name: "&b寒冰之脑"
    lore:
      - "&7+5 攻击伤害"
    # 使用原版物品
    item: "BLUE_ICE"
    attributes:
      ATTACK_DAMAGE: 5.0
```

### `accessories.yml` 同理：

```yaml
accessories:
  thunder_amulet:
    name: "&6雷霆吊坠"
    lore:
      - "&7技能伤害 +15%"
    item: "mymagic:thunder_amulet"  # ItemsAdder 物品
    skill_damage_multiplier: 1.15
```

> 💡 **规则**：  
> - 如果 `item` 包含 `:`（如 `namespace:id`）→ 尝试从 ItemsAdder 加载  
> - 否则 → 视为原版 Material（如 `DIAMOND_SWORD`）

---

## ⚙️ 技术实现：如何加载 ItemsAdder 物品？

ItemsAdder 提供了非常友好的 API：

```java
// 检查是否已安装 ItemsAdder
if (Bukkit.getPluginManager().isPluginEnabled("ItemsAdder")) {
    ItemStack item = ItemsAdder.getCustomItem("mymagic:fire_card");
    if (item != null) {
        // 成功加载自定义物品
    }
}
```

我们需要一个 **统一的物品加载工具类**：

```java
public class CustomItemLoader {
    public static ItemStack loadItem(String identifier) {
        if (identifier.contains(":")) {
            // 可能是 ItemsAdder 物品
            if (isItemsAdderEnabled()) {
                ItemStack iaItem = ItemsAdder.getCustomItem(identifier);
                if (iaItem != null) return iaItem;
            }
            // 如果 ItemsAdder 未启用或物品不存在，回退到原版（或报错）
        }

        // 尝试解析为原版 Material
        try {
            Material mat = Material.matchMaterial(identifier.toUpperCase());
            if (mat != null && mat.isItem()) {
                return new ItemStack(mat);
            }
        } catch (Exception ignored) {}

        // 无效物品，返回空气或占位符
        return new ItemStack(Material.BARRIER);
    }
}
```

---

## 🧱 更新后的类结构（新增）

```java
cn.popcraft.cardaccessory
 ├── CardAccessorySystem.java
 ├── manager/
 │    ├── ItemManager.java          // ← 使用 CustomItemLoader
 │    ├── EquipManager.java
 │    ├── GUIManager.java
 │    └── EffectManager.java
 ├── model/
 │    ├── Card.java
 │    ├── Accessory.java
 │    └── PlayerEquipment.java
 ├── listener/
 │    ├── EquipListener.java
 │    └── SkillDamageListener.java
 ├── util/
 │    ├── ConfigUtil.java
 │    └── CustomItemLoader.java     // ← 新增：统一加载 ItemsAdder/原版物品
 └── hook/
      └── ItemsAdderHook.java       // ← 可选：封装 ItemsAdder 检测逻辑
```

---

## 🔌 插件依赖声明（plugin.yml）

```yaml
name: CardAccessorySystem
version: 1.0
main: cn.popcraft.cardaccessory.CardAccessorySystem
api-version: 1.16
depend: []  # 不强制依赖
softdepend: [ItemsAdder, AttributePlus]  # 软依赖：有则用，无则不用
```

> ✅ 这样即使服务器没装 ItemsAdder 或 AttributePlus，插件也能启动（但相关功能禁用）

---

## 🛠️ Phase 1 开发：基础框架（含 ItemsAdder 支持）

我现在为你生成 **可直接运行的 Phase 1 代码**，包含：

1. 主类（加载配置、注册监听器）
2. 配置文件生成（cards.yml, accessories.yml）
3. `CustomItemLoader` 工具类
4. `ItemManager`：根据ID生成卡牌/饰品 ItemStack
5. 基础监听器（右键识别物品）

---

### ✅ 文件 1：`plugin.yml`

```yaml
name: CardAccessorySystem
version: 1.0
main: cn.popcraft.cardaccessory.CardAccessorySystem
api-version: 1.16
softdepend: [ItemsAdder, AttributePlus]
```

---

### ✅ 文件 2：`CardAccessorySystem.java`

```java
package cn.popcraft.cardaccessory;

import cn.popcraft.cardaccessory.manager.ItemManager;
import cn.popcraft.cardaccessory.listener.EquipListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class CardAccessorySystem extends JavaPlugin {

    private static CardAccessorySystem instance;
    private ItemManager itemManager;

    @Override
    public void onEnable() {
        instance = this;

        // 保存默认配置
        saveDefaultConfig();
        saveResource("cards.yml", false);
        saveResource("accessories.yml", false);

        // 初始化管理器
        this.itemManager = new ItemManager();

        // 注册监听器
        getServer().getPluginManager().registerEvents(new EquipListener(), this);

        getLogger().info("CardAccessorySystem 已启用！支持 ItemsAdder 和 AttributePlus。");
    }

    public static CardAccessorySystem getInstance() {
        return instance;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }
}
```

---

### ✅ 文件 3：`util/CustomItemLoader.java`

```java
package cn.popcraft.cardaccessory.util;

import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class CustomItemLoader {

    public static ItemStack loadItem(String identifier) {
        if (identifier == null || identifier.isEmpty()) {
            return new ItemStack(Material.AIR);
        }

        // 尝试 ItemsAdder（如果启用）
        if (identifier.contains(":")) {
            Plugin iaPlugin = org.bukkit.Bukkit.getPluginManager().getPlugin("ItemsAdder");
            if (iaPlugin != null && iaPlugin.isEnabled()) {
                try {
                    ItemStack iaItem = ItemsAdder.getCustomItem(identifier);
                    if (iaItem != null && !iaItem.getType().isAir()) {
                        return iaItem;
                    }
                } catch (Exception e) {
                    // ItemsAdder API 调用失败
                }
            }
        }

        // 回退到原版 Material
        try {
            Material mat = Material.matchMaterial(identifier.toUpperCase());
            if (mat != null && mat.isItem()) {
                return new ItemStack(mat);
            }
        } catch (Exception ignored) {}

        // 无效物品
        return new ItemStack(Material.BARRIER);
    }
}
```

---

### ✅ 文件 4：`model/Card.java`

```java
package cn.popcraft.cardaccessory.model;

import java.util.Map;

public class Card {
    private String id;
    private String name;
    private String[] lore;
    private String itemIdentifier; // e.g., "mymagic:fire_card" or "DIAMOND"
    private Map<String, Double> attributes;

    // 构造器、getter、setter 省略（可用 Lombok 或手写）
    public Card(String id, String name, String[] lore, String itemIdentifier, Map<String, Double> attributes) {
        this.id = id;
        this.name = name;
        this.lore = lore;
        this.itemIdentifier = itemIdentifier;
        this.attributes = attributes;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String[] getLore() { return lore; }
    public String getItemIdentifier() { return itemIdentifier; }
    public Map<String, Double> getAttributes() { return attributes; }
}
```

> `Accessory.java` 类似，只是字段为 `skill_damage_multiplier`

---

### ✅ 文件 5：`manager/ItemManager.java`

```java
package cn.popcraft.cardaccessory.manager;

import cn.popcraft.cardaccessory.CardAccessorySystem;
import cn.popcraft.cardaccessory.model.Card;
import cn.popcraft.cardaccessory.model.Accessory;
import cn.popcraft.cardaccessory.util.CustomItemLoader;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

public class ItemManager {
    private final Map<String, Card> cards = new HashMap<>();
    private final Map<String, Accessory> accessories = new HashMap<>();

    public ItemManager() {
        loadCards();
        loadAccessories();
    }

    private void loadCards() {
        File file = new File(CardAccessorySystem.getInstance().getDataFolder(), "cards.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (String id : config.getConfigurationSection("cards").getKeys(false)) {
            String path = "cards." + id + ".";
            String name = config.getString(path + "name", "&c未知卡牌");
            String[] lore = config.getStringList(path + "lore").toArray(new String[0]);
            String item = config.getString(path + "item", "PAPER");
            Map<String, Double> attrs = new HashMap<>();
            if (config.isConfigurationSection(path + "attributes")) {
                for (String attr : config.getConfigurationSection(path + "attributes").getKeys(false)) {
                    attrs.put(attr, config.getDouble(path + "attributes." + attr));
                }
            }

            cards.put(id, new Card(id, name, lore, item, attrs));
        }
    }

    private void loadAccessories() {
        // 类似 loadCards()
    }

    public ItemStack createCardItem(String cardId) {
        Card card = cards.get(cardId);
        if (card == null) return new ItemStack(org.bukkit.Material.BARRIER);

        ItemStack item = CustomItemLoader.loadItem(card.getItemIdentifier());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(org.bukkit.ChatColor.translateAlternateColorCodes('&', card.getName()));
            meta.setLore(Arrays.asList(card.getLore()));
            // 添加NBT标识（用于右键识别）
            // 这里简化：用 PersistentDataContainer 或 Lore 标记
            item.setItemMeta(meta);
        }
        return item;
    }

    // 类似 createAccessoryItem()
}
```

> 🔜 后续我们会用 `PersistentDataContainer` 存储卡牌/饰品ID，比Lore更可靠。

---

### ✅ 文件 6：`listener/EquipListener.java`（简化版）

```java
package cn.popcraft.cardaccessory.listener;

import cn.popcraft.cardaccessory.CardAccessorySystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class EquipListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        // TODO: 检查物品是否为卡牌/饰品（通过NBT或Lore）
        // 目前留空，Phase 2 实现
    }
}
```

---

## 📦 资源文件

在 `src/main/resources/` 中放入：

- `cards.yml`（示例内容见上）
- `accessories.yml`（示例内容见上）

---

## ✅ 下一步

这个 Phase 1 框架已经：
- 支持 ItemsAdder 和原版物品
- 可加载配置
- 生成带自定义名称/Lore 的物品

**Phase 2 我们将：**
1. 用 `PersistentDataContainer` 标记卡牌/饰品（避免Lore被修改）
2. 实现右键装备逻辑
3. 集成 AttributePlus 属性API

---

📌 **请确认：**
- 你是否希望我继续输出 **Phase 2 代码**（含 NBT 标记 + 装备逻辑）？
- 或者你先测试这个基础框架？

随时告诉我！我们一步步打造一个专业级插件 🔥