package com.iridium.iridiumskyblock.configs;

import com.iridium.iridiumskyblock.MultiversionMaterials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Inventories {


    public String confirmationGUITitle = "&7Are you sure?";
    public String membersGUITitle = "&7Members";
    public String coopGUITitle = "&7Co-op Islands";
    public String islandMenuGUITitle = "&7Menu";
    public String warpGUITitle = "&7Warps";
    public String topGUITitle = "&7Top Islands";
    public String borderColorGUITitle = "&7Border Color";
    public String permissionsGUITitle = "&7Permissions";
    public String schematicselectGUITitle = "&7Select an Island";
    public String visitGUITitle = "&7Visit an Island";
    public String biomeGUITitle = "&7Island Biome";

    public int membersGUISize = 27;
    public int coopGUISize = 27;
    public int islandMenuGUISize = 45;
    public int warpGUISize = 27;
    public int topGUISize = 27;
    public int borderColorGUISize = 27;
    public int permissionsGUISize = 27;
    public int schematicselectGUISize = 27;
    public int visitGUISize = 54;
    public int biomeGUISize = 54;

    //Menu
    public Item home = new Item(MultiversionMaterials.WHITE_BED, 13, 1, "&b&lIsland Home", Collections.singletonList("&7Teleport to your island home"));
    public Item members = new Item(MultiversionMaterials.PLAYER_HEAD, 14, 1, "&b&lIsland Members", "Peaches_MLG", Collections.singletonList("&7View your island Members."));
    public Item regen = new Item(MultiversionMaterials.GRASS, 36, 1, "&b&lIsland Regen", Collections.singletonList("&7Regenerate your island."));
    public Item permissions = new Item(MultiversionMaterials.BOOK, 31, 1, "&b&lIsland Permissions", Collections.singletonList("&7Change island permissions."));
    public Item top = new Item(MultiversionMaterials.DIAMOND, 0, 1, "&b&lIsland Top", Collections.singletonList("&7View top islands."));
    public Item warps = new Item(MultiversionMaterials.END_PORTAL_FRAME, 20, 1, "&b&lIsland Warps", Collections.singletonList("&7View your island warps."));
    public Item border = new Item(MultiversionMaterials.BEACON, 24, 1, "&b&lIsland Border", Collections.singletonList("&7Change your island border."));
    public Item coop = new Item(MultiversionMaterials.NAME_TAG, 32, 1, "&b&lIsland Coop", Collections.singletonList("&7View your Co-op Islands."));
    public Item biomes = new Item(MultiversionMaterials.PLAYER_HEAD, 12, 1, "&b&lIsland Biome", "BlockminersTV", Collections.singletonList("&7Change your island biome."));
    public Item delete = new Item(MultiversionMaterials.BARRIER, 44, 1, "&b&lIsland Delete", Collections.singletonList("&7Delete your island."));

    public Item background = new Item(MultiversionMaterials.BLACK_STAINED_GLASS_PANE, 1, " ", new ArrayList<>());

    public Item nextPage = new Item(MultiversionMaterials.LIME_STAINED_GLASS_PANE, 1, "&a&lNext Page", new ArrayList<>());

    public Item previousPage = new Item(MultiversionMaterials.RED_STAINED_GLASS_PANE, 1, "&c&lPrevious Page", new ArrayList<>());

    public Item biome = new Item(MultiversionMaterials.GRASS, 1, "&b&l{biome} Biome", new ArrayList<>());

    public Item back = new Item(MultiversionMaterials.NETHER_STAR, 1, "&c&lBack", new ArrayList<>());

    public Item islandmember = new Item(MultiversionMaterials.PLAYER_HEAD, 1, "&b&l{player}", "{player}", Arrays.asList("&bRole: {role}", "", "&b&l[!] &bLeft Click to {demote}" + " this Player.", "&b&l[!] &bRight Click to Promote this Player."));
    public Item islandcoop = new Item(MultiversionMaterials.PLAYER_HEAD, 1, "&b&l{player}", "{player}", Arrays.asList("&b&l * &7Island: &b{name}", "&b&l * &7Rank: &b{rank}", "&b&l * &7Value: &b{value}", "", "&b&l[!] &bLeft Click to Teleport to this island.", "&b&l[!] &bRight Click to un co-op this island."));
    public Item islandRoles = new Item(MultiversionMaterials.RED_STAINED_GLASS_PANE, 1, "&b&l{role}", Collections.emptyList());
    public Item islandPermissionAllow = new Item(MultiversionMaterials.LIME_STAINED_GLASS_PANE, 1, "&b&l{permission}", Collections.emptyList());
    public Item islandPermissionDeny = new Item(MultiversionMaterials.RED_STAINED_GLASS_PANE, 1, "&b&l{permission}", Collections.emptyList());
    public Item islandWarp = new Item(MultiversionMaterials.YELLOW_STAINED_GLASS_PANE, 1, "&b&l{warp}", Arrays.asList("", "&b&l[!] &bLeft Click to Teleport to this warp.", "&b&l[!] &bRight Click to Delete to warp."));
    public Item topisland = new Item(MultiversionMaterials.PLAYER_HEAD, 1, "&b&l{player}", "{player}", Arrays.asList("&b&l * &7Island: &b{name}", "&b&l * &7Rank: &b{rank}", "&b&l * &7Value: &b{value}", "", "&b&l[!] &bLeft Click to Teleport to this island."));

    public static class Item {

        public MultiversionMaterials material;
        public int amount;
        public String title;
        public String headOwner;
        public List<String> lore;
        public Integer slot;

        public Item(MultiversionMaterials material, int amount, String title, List<String> lore) {
            this.material = material;
            this.amount = amount;
            this.lore = lore;
            this.title = title;
        }

        public Item(MultiversionMaterials material, int slot, int amount, String title, List<String> lore) {
            this.material = material;
            this.amount = amount;
            this.lore = lore;
            this.title = title;
            this.slot = slot;
        }

        public Item(MultiversionMaterials material, int slot, int amount, String title, String headOwner, List<String> lore) {
            this.material = material;
            this.amount = amount;
            this.lore = lore;
            this.title = title;
            this.headOwner = headOwner;
            this.slot = slot;
        }

        public Item(MultiversionMaterials material, int amount, String title, String headOwner, List<String> lore) {
            this.material = material;
            this.amount = amount;
            this.lore = lore;
            this.title = title;
            this.headOwner = headOwner;
        }
    }
}
