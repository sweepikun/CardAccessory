根据检索到的 AttributePlus Pro 官方开发文档信息，我已将其整理为标准 Markdown 格式，供你提供给 Qwen3-Coder 使用：

---

# AttributePlus Pro 开发文档（精简版）

## 前提
- 需掌握 Java 或 Kotlin
- 需具备 Bukkit 插件开发能力
- 交流群：901796907

## 核心事件（可用于监听与修改）

| 事件类 | 说明 |
|--------|------|
| `AttrAttributeTriggerEvent.Before` | 属性触发前 |
| `AttrAttributeTriggerEvent.After` | 属性触发后 |
| `AttrUpdateAttributeEvent.Before` | 实体属性刷新前 |
| `AttrUpdateAttributeEvent.After` | 实体属性刷新后 |
| `AttrAttributeReadEvent` | 属性读取事件 |
| `AttrEntityAttackEvent` | **实体攻击事件**（用于技能/普攻伤害监听） |
| `AttrShieldBlockEvent` | 盾牌格挡事件 |
| `AttrShootBowEvent` | 弓射击事件 |

> 来源：, 

## 核心 API（Java/Kotlin）

### 获取实体属性数据
```kotlin
// Kotlin 示例
fun getAttrData(entity: LivingEntity): AttributeData =
    AttributePlus.attributeManager.getAttributeData(entity.uniqueId, entity)
```
> 来源：, 

### 设置/修改属性值
```java
// Java 中可通过 AttributeData 操作
// 文档提到存在 setAttribute 方法用于设置属性值
setAttribute(defaultAttributeName: String, value: Double)
```
> 来源：

### 属性存储值操作（Java）
```java
// 使用 Data<String, Double>.storageValue() 等方法读取/写入
// 注意：避免在 getRandomValue() 后错误修改存储值
```
> 来源：

## 组件与注解

| 注解 | 说明 |
|------|------|
| `@AutoRegister` | 自动注册组件 |
| `@AutoIncreaseKey` | 自动新增标签（配合 ReadComponent 使用） |

> 来源：

## 属性类型（AttributeName）

- `attackOrDefense`：攻击与防御类属性  
- `update`：更新类属性（属性刷新时触发）  
- `runtime`：运行类属性（定时触发）  
- `other`：其他类型  

> 来源：

## 使用建议（针对你的插件）

- **技能伤害监听**：优先监听 `AttrEntityAttackEvent`，该事件专为“实体攻击”设计，可区分技能与普攻。
- **属性修改**：通过 `AttributeData` 获取后修改 `storageValue`，或使用 `setAttribute` 方法。
- **安全调用**：务必检查 `AttributePlus` 插件是否启用，再调用其 API。

---

你可以将以上内容直接作为上下文提供给 Qwen3-Coder，它将能正确实现与 AttributePlus 的深度集成（如属性应用、技能伤害加成等）。需要我帮你生成对应的 `AttributePlusHook.java` 吗？