package com.iridium.iridiumskyblock;

import com.iridium.iridiumskyblock.configs.Inventories;
import com.iridium.iridiumskyblock.support.Vault;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {

    public static Object getObject(List<String> fields) {
        Object object = IridiumSkyblock.getInstance();
        for (String field : fields) {
            try {
                if (object instanceof List) {
                    List<Object> objects = (List<Object>) object;
                    if (objects.size() > Integer.parseInt(field)) {
                        object = objects.get(Integer.parseInt(field));
                    } else {
                        return object;
                    }
                } else if (object instanceof HashSet) {
                    HashSet<Object> objects = (HashSet<Object>) object;
                    int i = 0;
                    for (Object o : objects) {
                        if ((i == Integer.parseInt(field))) {
                            object = o;
                        }
                        i++;
                    }
                } else {
                    object = object.getClass().getField(field).get(object);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    public static Field getField(List<String> fields) {
        Object object = IridiumSkyblock.getInstance();
        Field f = null;
        for (String field : fields) {
            try {
                if (object instanceof List) {
                    List<Object> objects = (List<Object>) object;
                    if (objects.size() > Integer.parseInt(field)) {
                        f = object.getClass().getField(field);
                        f.setAccessible(true);
                        object = objects.get(Integer.parseInt(field));
                    } else {
                        return f;
                    }
                } else if (object instanceof HashSet) {
                    HashSet<Object> objects = (HashSet<Object>) object;
                    int i = 0;
                    for (Object o : objects) {
                        if ((i == Integer.parseInt(field))) {
                            f = object.getClass().getField(field);
                            f.setAccessible(true);
                            object = o;
                        }
                        i++;
                    }
                } else {
                    f = object.getClass().getField(field);
                    f.setAccessible(true);
                    object = object.getClass().getField(field).get(object);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return f;
    }

    public static ItemStack makeItem(Material material, int amount, int type, String name, List<String> lore, Object object) {
        ItemStack item = new ItemStack(material, amount, (short) type);
        ItemMeta m = item.getItemMeta();
        m.setLore(lore);
        m.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(m);
        return item;
    }

    public static ItemStack makeItem(MultiversionMaterials material, int amount, String name) {
        ItemStack item = new ItemStack(material.parseMaterial(), amount, (short) material.data);
        ItemMeta m = item.getItemMeta();
        m.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(m);
        return item;
    }

    public static ItemStack makeItem(MultiversionMaterials material, int amount, String name, List<String> lore) {
        ItemStack item = new ItemStack(material.parseMaterial(), amount, (short) material.data);
        ItemMeta m = item.getItemMeta();
        m.setLore(Utils.color(lore));
        m.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(m);
        return item;
    }

    public static ItemStack makeItem(Inventories.Item item, List<Placeholder> placeholders) {
        try {
            ItemStack itemstack = makeItem(item.material, item.amount, processMultiplePlaceholders(item.title, placeholders), processMultiplePlaceholders(item.lore, placeholders));
            if (item.material == MultiversionMaterials.PLAYER_HEAD && item.headOwner != null) {
                SkullMeta m = (SkullMeta) itemstack.getItemMeta();
                m.setOwner(processMultiplePlaceholders(item.headOwner, placeholders));
                itemstack.setItemMeta(m);
            }
            return itemstack;
        } catch (Exception e) {
            return makeItem(MultiversionMaterials.STONE, item.amount, processMultiplePlaceholders(item.title, placeholders), processMultiplePlaceholders(item.lore, placeholders));
        }
    }

    public static ItemStack makeItem(Inventories.Item item) {
        try {
            ItemStack itemstack = makeItem(item.material, item.amount, item.title, item.lore);
            if (item.material == MultiversionMaterials.PLAYER_HEAD && item.headOwner != null) {
                SkullMeta m = (SkullMeta) itemstack.getItemMeta();
                m.setOwner(item.headOwner);
                itemstack.setItemMeta(m);
            }
            return itemstack;
        } catch (Exception e) {
            return makeItem(MultiversionMaterials.STONE, item.amount, item.title, item.lore);
        }
    }

    public static ItemStack makeItem(Inventories.Item item, Island island) {
        try {
            ItemStack itemstack = makeItem(item.material, item.amount, processIslandPlaceholders(item.title, island), color(processIslandPlaceholders(item.lore, island)));
            if (item.material == MultiversionMaterials.PLAYER_HEAD && item.headOwner != null) {
                SkullMeta m = (SkullMeta) itemstack.getItemMeta();
                m.setOwner(item.headOwner);
                itemstack.setItemMeta(m);
            }
            return itemstack;
        } catch (Exception e) {
            return makeItem(MultiversionMaterials.STONE, item.amount, processIslandPlaceholders(item.title, island), color(processIslandPlaceholders(item.lore, island)));
        }
    }

    public static ItemStack makeItemHidden(Inventories.Item item) {
        try {
            ItemStack itemstack = makeItemHidden(item.material.parseMaterial(), item.amount, item.material.data, item.title, item.lore);
            if (item.material == MultiversionMaterials.PLAYER_HEAD && item.headOwner != null) {
                SkullMeta m = (SkullMeta) itemstack.getItemMeta();
                m.setOwner(item.headOwner);
                itemstack.setItemMeta(m);
            }
            return itemstack;
        } catch (Exception e) {
            return makeItemHidden(Material.STONE, item.amount, item.material.data, item.title, item.lore);
        }
    }

    public static ItemStack makeItemHidden(Inventories.Item item, Island island) {
        try {
            ItemStack itemstack = makeItemHidden(item.material.parseMaterial(), item.amount, item.material.data, processIslandPlaceholders(item.title, island), color(processIslandPlaceholders(item.lore, island)));
            if (item.material == MultiversionMaterials.PLAYER_HEAD && item.headOwner != null) {
                SkullMeta m = (SkullMeta) itemstack.getItemMeta();
                m.setOwner(item.headOwner);
                itemstack.setItemMeta(m);
            }
            return itemstack;
        } catch (Exception e) {
            return makeItemHidden(Material.STONE, item.amount, item.material.data, processIslandPlaceholders(item.title, island), color(processIslandPlaceholders(item.lore, island)));
        }
    }

    public static ItemStack makeItemHidden(Material material, int amount, int type, String name, List<String> lore) {
        ItemStack item = new ItemStack(material, amount, (short) type);
        ItemMeta m = item.getItemMeta();
        m.setLore(Utils.color(lore));
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
        m.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(m);
        return item;
    }

    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static List<String> color(List<String> strings) {
        return strings.stream().map(Utils::color).collect(Collectors.toList());
    }

    public static boolean isBlockValuable(Block b) {
        return IridiumSkyblock.getBlockValues().blockvalue.containsKey(MultiversionMaterials.fromMaterial(b.getType())) || b.getState() instanceof CreatureSpawner;
    }

    public static List<Island> getTopIslands() {
        List<Island> islands = new ArrayList<>(IridiumSkyblock.getIslandManager().islands.values());
        islands.sort(Comparator.comparingInt(Island::getValue));
        Collections.reverse(islands);
        return islands;
    }

    public static List<Island> getIslands() {
        List<Island> islands = new ArrayList<>(IridiumSkyblock.getIslandManager().islands.values());
        islands.sort(Comparator.comparingInt(Island::getVotes));
        Collections.reverse(islands);
        return islands;
    }

    public static boolean isSafe(Location loc, Island island) {
        return (island.isInIsland(loc) && loc.getBlock().getType().equals(Material.AIR)
                && (!loc.clone().add(0, -1, 0).getBlock().getType().equals(Material.AIR) && !loc.clone().add(0, -1, 0).getBlock().isLiquid()));
    }

    public static int getIslandRank(Island island) {
        int i = 1;
        for (Island is : getTopIslands()) {
            if (is.equals(island)) {
                return i;
            }
            i++;
        }
        return 0;
    }

    public static Location getNewHome(Island island, Location loc) {
        Block b;
        b = loc.getWorld().getHighestBlockAt(loc);
        if (isSafe(b.getLocation(), island)) {
            return b.getLocation().add(0.5, 1, 0.5);
        }

        for (double X = island.getPos1().getX(); X <= island.getPos2().getX(); X++) {
            for (double Z = island.getPos1().getZ(); Z <= island.getPos2().getZ(); Z++) {
                b = loc.getWorld().getHighestBlockAt((int) X, (int) Z);
                if (isSafe(b.getLocation(), island)) {
                    return b.getLocation().add(0.5, 1, 0.5);
                }
            }
        }
        return null;
    }

    public static String processIslandPlaceholders(String line, Island island) {
        List<Placeholder> placeholders = new ArrayList<>(Arrays.asList(
                // Upgrades
                new Placeholder("sizevaultcost", IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.containsKey(island.getSizeLevel() + 1) ? IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(island.getSizeLevel() + 1).vaultCost + "" : IridiumSkyblock.getMessages().maxlevelreached),
                new Placeholder("membervaultcost", IridiumSkyblock.getUpgrades().memberUpgrade.upgrades.containsKey(island.getMemberLevel() + 1) ? IridiumSkyblock.getUpgrades().memberUpgrade.upgrades.get(island.getMemberLevel() + 1).vaultCost + "" : IridiumSkyblock.getMessages().maxlevelreached),
                new Placeholder("warpvaultcost", IridiumSkyblock.getUpgrades().warpUpgrade.upgrades.containsKey(island.getWarpLevel() + 1) ? IridiumSkyblock.getUpgrades().warpUpgrade.upgrades.get(island.getWarpLevel() + 1).vaultCost + "" : IridiumSkyblock.getMessages().maxlevelreached),

                new Placeholder("sizecrystalscost", IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.containsKey(island.getSizeLevel() + 1) ? IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(island.getSizeLevel() + 1).crystalsCost + "" : IridiumSkyblock.getMessages().maxlevelreached),
                new Placeholder("membercrystalscost", IridiumSkyblock.getUpgrades().memberUpgrade.upgrades.containsKey(island.getMemberLevel() + 1) ? IridiumSkyblock.getUpgrades().memberUpgrade.upgrades.get(island.getMemberLevel() + 1).crystalsCost + "" : IridiumSkyblock.getMessages().maxlevelreached),
                new Placeholder("warpcrystalscost", IridiumSkyblock.getUpgrades().warpUpgrade.upgrades.containsKey(island.getWarpLevel() + 1) ? IridiumSkyblock.getUpgrades().warpUpgrade.upgrades.get(island.getWarpLevel() + 1).crystalsCost + "" : IridiumSkyblock.getMessages().maxlevelreached),

                new Placeholder("sizecost", IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.containsKey(island.getSizeLevel() + 1) ? IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(island.getSizeLevel() + 1).crystalsCost + "" : IridiumSkyblock.getMessages().maxlevelreached),
                new Placeholder("membercost", IridiumSkyblock.getUpgrades().memberUpgrade.upgrades.containsKey(island.getMemberLevel() + 1) ? IridiumSkyblock.getUpgrades().memberUpgrade.upgrades.get(island.getMemberLevel() + 1).crystalsCost + "" : IridiumSkyblock.getMessages().maxlevelreached),
                new Placeholder("warpcost", IridiumSkyblock.getUpgrades().warpUpgrade.upgrades.containsKey(island.getWarpLevel() + 1) ? IridiumSkyblock.getUpgrades().warpUpgrade.upgrades.get(island.getWarpLevel() + 1).crystalsCost + "" : IridiumSkyblock.getMessages().maxlevelreached),

                new Placeholder("sizeblocks", IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(island.getSizeLevel()).size + ""),
                new Placeholder("membercount", IridiumSkyblock.getUpgrades().memberUpgrade.upgrades.get(island.getMemberLevel()).size + ""),
                new Placeholder("warpcount", IridiumSkyblock.getUpgrades().warpUpgrade.upgrades.get(island.getMemberLevel()).size + ""),

                new Placeholder("sizelevel", island.getSizeLevel() + ""),
                new Placeholder("memberlevel", island.getMemberLevel() + ""),
                new Placeholder("warplevel", island.getWarpLevel() + "")));
        return processMultiplePlaceholders(line, placeholders);
    }

    public static List<String> processIslandPlaceholders(List<String> lines, Island island) {
        List<String> newlist = new ArrayList<>();
        for (String string : lines) {
            newlist.add(processIslandPlaceholders(string, island));
        }
        return newlist;
    }

    public static List<String> processMultiplePlaceholders(List<String> lines, List<Placeholder> placeholders) {
        List<String> newlist = new ArrayList<>();
        for (String string : lines) {
            newlist.add(processMultiplePlaceholders(string, placeholders));
        }
        return newlist;
    }

    public static String processMultiplePlaceholders(String line, List<Placeholder> placeholders) {
        for (Placeholder placeholder : placeholders) {
            line = placeholder.process(line);
        }
        return color(line);
    }

    public static class Placeholder {

        private String key;
        private String value;

        public Placeholder(String key, String value) {
            this.key = "{" + key + "}";
            this.value = value;
        }

        public String process(String line) {
            return line.replace(key, value);
        }
    }
}
