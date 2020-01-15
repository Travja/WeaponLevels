package com.coffeecup.novus.weaponlevels.type;

import java.io.IOException;
import java.io.InputStreamReader;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.coffeecup.novus.weaponlevels.WLPlugin;
import com.coffeecup.novus.weaponlevels.util.Util;

public class TypeChecker {
    private static YamlConfiguration names = null;

    public static void loadItems(WLPlugin plugin) {
        names = new YamlConfiguration();

        try {
            InputStreamReader reader = new InputStreamReader(plugin.getResource("names.yml"));
            names.load(reader);
            reader.close();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if an item is a weapon.
     *
     * @param material - The material of the item to check
     * @return True if the item is a weapon.
     */
    public static boolean isWeapon(Material material) {
        return names.getConfigurationSection("weapons").getValues(false).containsKey(material.name());
    }

    /**
     * Check if an item is armor.
     *
     * @param material - The material of the item to check
     * @return True if the item is armor.
     */
    public static boolean isArmor(Material material) {
        return names.getConfigurationSection("armor").getValues(false).containsKey(material.name());
    }

    /**
     * Check if an item is a tool.
     *
     * @param material - The material of the item to check
     * @return True if the item is a tool.
     */
    public static boolean isTool(Material material) {
        return names.getConfigurationSection("tools").getValues(false).containsKey(material.name());
    }

    /**
     * Checks if a tool is the correct tool to use for the specified block.
     *
     * @param block - The block to check
     * @param tool  - The tool to check
     * @return True if the tool is the proper tool for the block.
     */
    public static boolean isCorrectTool(ToolType tool, Block block) {
        String values = names.getConfigurationSection("correct tools.").getString(tool.name());

        return Util.getCommaSeperatedValues(values).contains(block.getType().name());
    }

    /**
     * Get the name of the item that is shown in-game.
     *
     * @param material - The material to get the name of
     * @return The in-game name of the item.
     */
    public static String getInGameName(Material material) {
        if (isWeapon(material)) {
            return names.getConfigurationSection("weapons").getString(material.name());
        } else if (isArmor(material)) {
            return names.getConfigurationSection("armor").getString(material.name());
        } else if (isTool(material)) {
            return names.getConfigurationSection("tools").getString(material.name());
        } else {
            return Util.capitalizeFirst(material.name(), '_');
        }
    }

    public static ItemType getItemType(Material material) {
        if (isWeapon(material)) {
            return ItemType.WEAPON;
        } else if (isArmor(material)) {
            return ItemType.ARMOR;
        } else if (isTool(material)) {
            return ItemType.TOOL;
        } else {
            return ItemType.ITEM;
        }
    }
}
