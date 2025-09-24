package cn.popcraft.cardaccessory.manager;

import cn.popcraft.cardaccessory.model.Effect;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.events.MythicDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;

/**
 * 效果处理器
 * 用于处理饰品提供的各种效果
 */
public class EffectProcessor {
    
    /**
     * 处理MythicDamage事件中的饰品效果
     * @param event MythicDamage事件
     * @param effects 饰品效果列表
     */
    public static void processMythicDamageEffects(MythicDamageEvent event, Collection<Effect> effects) {
        AbstractEntity attacker = event.getCaster().getEntity();
        if (!(attacker.getBukkitEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) attacker.getBukkitEntity();
        
        for (Effect effect : effects) {
            switch (effect.getType().toLowerCase()) {
                case "skill_damage":
                    processSkillDamageEffect(event, effect);
                    break;
                case "potion_effect":
                    processPotionEffect(player, effect);
                    break;
                case "critical_chance":
                    processCriticalChanceEffect(event, effect);
                    break;
                case "life_steal":
                    processLifeStealEffect(event, effect);
                    break;
            }
        }
    }
    
    /**
     * 处理技能伤害效果
     * @param event MythicDamage事件
     * @param effect 效果配置
     */
    private static void processSkillDamageEffect(MythicDamageEvent event, Effect effect) {
        Double multiplier = effect.getOption("multiplier", 1.0);
        String skill = effect.getOption("skill", "");
        
        // 如果配置了特定技能，检查是否匹配
        if (!skill.isEmpty()) {
            // 由于API限制，我们暂时无法精确匹配技能名称
            // 可以通过其他方式实现，比如检查伤害类型等
        }
        
        // 应用伤害倍数
        event.setDamage(event.getDamage() * multiplier);
    }
    
    /**
     * 处理药水效果
     * @param player 玩家
     * @param effect 效果配置
     */
    private static void processPotionEffect(Player player, Effect effect) {
        String potionType = effect.getOption("potion", "");
        Integer amplifier = effect.getOption("amplifier", 0);
        Integer duration = effect.getOption("duration", 100); // 默认200 ticks (10秒)
        Boolean ambient = effect.getOption("ambient", false);
        Boolean particles = effect.getOption("particles", true);
        Boolean icon = effect.getOption("icon", true);
        
        PotionEffectType type = PotionEffectType.getByName(potionType.toUpperCase());
        if (type != null) {
            PotionEffect potionEffect = new PotionEffect(type, duration, amplifier, ambient, particles, icon);
            player.addPotionEffect(potionEffect, true);
        }
    }
    
    /**
     * 处理暴击几率效果
     * @param event MythicDamage事件
     * @param effect 效果配置
     */
    private static void processCriticalChanceEffect(MythicDamageEvent event, Effect effect) {
        Double chance = effect.getOption("chance", 0.0);
        Double multiplier = effect.getOption("multiplier", 1.5);
        
        // 简单的随机暴击实现
        if (Math.random() < chance) {
            event.setDamage(event.getDamage() * multiplier);
            // 可以在这里添加暴击特效
        }
    }
    
    /**
     * 处理生命偷取效果
     * @param event MythicDamage事件
     * @param effect 效果配置
     */
    private static void processLifeStealEffect(MythicDamageEvent event, Effect effect) {
        Double percentage = effect.getOption("percentage", 0.0);
        AbstractEntity attacker = event.getCaster().getEntity();
        
        if (attacker.getBukkitEntity() instanceof Player && percentage > 0) {
            Player player = (Player) attacker.getBukkitEntity();
            double healAmount = event.getDamage() * percentage;
            player.setHealth(Math.min(player.getHealth() + healAmount, player.getMaxHealth()));
        }
    }
}