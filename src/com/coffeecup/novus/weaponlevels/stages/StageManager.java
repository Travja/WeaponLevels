package com.coffeecup.novus.weaponlevels.stages;

import com.coffeecup.novus.weaponlevels.Config;
import com.coffeecup.novus.weaponlevels.data.LevelEnchantment;
import com.coffeecup.novus.weaponlevels.type.ItemType;
import com.coffeecup.novus.weaponlevels.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class StageManager {
    private static HashMap<Integer, Stage> stages = new HashMap<Integer, Stage>();

    public static void loadStages() {
        Map<String, Object> yamlStages = Config.STAGES.getValues(false);

        for (Entry<String, Object> entry : yamlStages.entrySet()) {
            String stageName = entry.getKey();
            int level = Config.STAGES.getInt(stageName + ".level");
            Stage stage = createStage(stageName);

            stages.put(level, stage);
        }
    }

    public static Stage getStage(ItemType type, int level) {
        return stages.get(level);
    }

    private static Stage createStage(String name) {
        ConfigurationSection config = Config.STAGES.getConfigurationSection(name);
        int level = config.getInt("level");
        ChatColor color = Util.getSafeChatColor(config.getString("color"), ChatColor.WHITE);
        List<LevelEnchantment> enchantments = getEnchantments(config.getString("enchantments"));
        Map<String, Integer> bonuses = getBonuses(config.getConfigurationSection("bonuses"));

        return new Stage(name, level, color, enchantments, bonuses);
    }

    private static List<LevelEnchantment> getEnchantments(String data) {
        List<LevelEnchantment> list = new ArrayList<LevelEnchantment>();

        for (String enchantment : Util.getCommaSeperatedValues(data)) {
            try {
                String[] split = enchantment.split("\\.");

                String id = split[0];
                int level = Integer.valueOf(split[1]);

                list.add(new LevelEnchantment(Enchantment.getByKey(NamespacedKey.minecraft(id)), level));
            } catch (NumberFormatException e) {
                Bukkit.getLogger().warning("WeaponLevels: Invalid enchantment format '" + enchantment + "'.");
            }
        }

        return list;
    }

    private static Map<String, Integer> getBonuses(ConfigurationSection config) {
        Map<String, Integer> map = new HashMap<String, Integer>();

        for (Entry<String, Object> entry : config.getValues(false).entrySet()) {
            String name = entry.getKey();
            int value = config.getInt(name);

            map.put(name, value);
        }

        return map;
    }
}
