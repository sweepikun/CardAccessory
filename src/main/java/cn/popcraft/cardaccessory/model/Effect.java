package cn.popcraft.cardaccessory.model;

import java.util.Map;

/**
 * 饰品效果类
 * 用于定义饰品可以提供的各种效果
 */
public class Effect {
    private final String type;           // 效果类型
    private final String target;         // 效果目标（可选）
    private final Map<String, Object> options; // 效果选项

    public Effect(String type, String target, Map<String, Object> options) {
        this.type = type;
        this.target = target;
        this.options = options;
    }

    public String getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }

    public Map<String, Object> getOptions() {
        return options;
    }
    
    /**
     * 获取选项值
     * @param key 选项键
     * @param defaultValue 默认值
     * @return 选项值或默认值
     */
    @SuppressWarnings("unchecked")
    public <T> T getOption(String key, T defaultValue) {
        Object value = options.get(key);
        if (value != null) {
            try {
                return (T) value;
            } catch (ClassCastException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
}