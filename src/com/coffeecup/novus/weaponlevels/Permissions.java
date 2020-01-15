package com.coffeecup.novus.weaponlevels;

import org.bukkit.entity.Player;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.CalculableType;

public class Permissions {
    public static boolean BPERMISSIONS = false;

    public static boolean hasPermission(Player player, String node) {

        node = "weaponlevels." + node.toLowerCase();

        if (BPERMISSIONS) {
            return hasBPermission(player, node);
        } else {
            return player.hasPermission(node);
        }
    }

    public static boolean hasPermissionConfig(Player player, String node) {
        if (Config.USE_PERMS) {
            return hasPermission(player, node);
        } else {
            return true;
        }
    }

    private static boolean hasBPermission(Player player, String node) {
        return ApiLayer.hasPermission(player.getWorld().getName(), CalculableType.USER, player.getName(), node);
    }
}
