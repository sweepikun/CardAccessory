package cn.popcraft.cardaccessory.manager;

import cn.popcraft.cardaccessory.model.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Method;
import java.util.Collection;

public class EffectProcessor {

    public static void processMythicDamageEffects(Event event, Collection<Effect> effects) {
        try {
            // 使用反射获取MythicDamageEvent的方法，避免硬依赖
            Method getCasterMethod = event.getClass().getMethod("getCaster");
            Object caster = getCasterMethod.invoke(event);

            Method getEntityMethod = caster.getClass().getMethod("getEntity");
            Object abstractEntity = getEntityMethod.invoke(caster);

            Method getBukkitEntityMethod = abstractEntity.getClass().getMethod("getBukkitEntity");
            Object bukkitEntity = getBukkitEntityMethod.invoke(abstractEntity);

            if (!(bukkitEntity instanceof Player)) {
                return;
            }

            Player player = (Player) bukkitEntity;
            Method getDamageMethod = event.getClass().getMethod("getDamage");
            Method setDamageMethod = event.getClass().getMethod("setDamage", double.class);

            double originalDamage = (double) getDamageMethod.invoke(event);

            for (Effect effect : effects) {
                switch (effect.getType().toLowerCase()) {
                    case "skill_damage":
                        originalDamage = processSkillDamageEffect(originalDamage, effect);
                        break;
                    case "potion_effect":
                        processPotionEffect(player, effect);
                        break;
                    case "critical_chance":
                        originalDamage = processCriticalChanceEffect(originalDamage, effect);
                        break;
                    case "life_steal":
                        processLifeStealEffect(player, originalDamage, effect);
                        break;
                }
            }

            setDamageMethod.invoke(event, originalDamage);
        } catch (Exception e) {
            // MythicMobs API不可用，静默忽略
        }
    }

    private static double processSkillDamageEffect(double damage, Effect effect) {
        Double multiplier = effect.getOption("multiplier", 1.0);
        return damage * multiplier;
    }

    private static void processPotionEffect(Player player, Effect effect) {
        String potionType = effect.getOption("potion", "");
        Integer amplifier = effect.getOption("amplifier", 0);
        Integer duration = effect.getOption("duration", 200);
        Boolean ambient = effect.getOption("ambient", false);
        Boolean particles = effect.getOption("particles", true);
        Boolean icon = effect.getOption("icon", true);

        PotionEffectType type = PotionEffectType.getByName(potionType.toUpperCase());
        if (type != null) {
            PotionEffect potionEffect = new PotionEffect(type, duration, amplifier, ambient, particles, icon);
            player.addPotionEffect(potionEffect, true);
        }
    }

    private static double processCriticalChanceEffect(double damage, Effect effect) {
        Double chance = effect.getOption("chance", 0.0);
        Double multiplier = effect.getOption("multiplier", 1.5);

        if (Math.random() < chance) {
            return damage * multiplier;
        }
        return damage;
    }

    private static void processLifeStealEffect(Player player, double damage, Effect effect) {
        Double percentage = effect.getOption("percentage", 0.0);
        if (percentage > 0) {
            double healAmount = damage * percentage;
            player.setHealth(Math.min(player.getHealth() + healAmount, player.getMaxHealth()));
        }
    }
}
