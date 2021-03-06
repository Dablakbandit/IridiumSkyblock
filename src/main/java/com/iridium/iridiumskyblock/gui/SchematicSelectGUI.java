package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.*;
import com.iridium.iridiumskyblock.configs.Schematics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SchematicSelectGUI extends GUI implements Listener {

    public SchematicSelectGUI(Island island) {
        super(island, IridiumSkyblock.getInventories().schematicselectGUISize, IridiumSkyblock.getInventories().schematicselectGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (IridiumSkyblock.getIslandManager().islands.containsKey(islandID)) {
            int i = 0;
            for (Schematics.FakeSchematic fakeSchematic : IridiumSkyblock.getSchematics().schematics) {
                if (fakeSchematic.slot == null) fakeSchematic.slot = i;
                try {
                    setItem(fakeSchematic.slot, Utils.makeItem(fakeSchematic.item, 1, fakeSchematic.displayname, fakeSchematic.lore));
                } catch (Exception e) {
                    setItem(fakeSchematic.slot, Utils.makeItem(MultiversionMaterials.STONE, 1, fakeSchematic.displayname, fakeSchematic.lore));
                }
                i++;
            }
        }
    }

    @EventHandler
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            for (Schematics.FakeSchematic fakeSchematic : IridiumSkyblock.getSchematics().schematics) {
                if (e.getSlot() == fakeSchematic.slot && (fakeSchematic.permission.isEmpty() || e.getWhoClicked().hasPermission(fakeSchematic.permission))) {
                    e.getWhoClicked().closeInventory();
                    if (getIsland().getSchematic() != null) {
                        for (String player : getIsland().getMembers()) {
                            User user = User.getUser(player);
                            Player p = Bukkit.getPlayer(user.name);
                            if (p != null) {
                                p.sendMessage(Utils.color(IridiumSkyblock.getMessages().regenIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                            }
                        }
                    }
                    if (getIsland().getSchematic() == null) {
                        getIsland().setSchematic(fakeSchematic.name);
                        getIsland().pasteSchematic((Player) e.getWhoClicked(), false);
                    } else {
                        getIsland().setSchematic(fakeSchematic.name);
                        getIsland().pasteSchematic(true);
                    }
                    getIsland().setHome(getIsland().getHome().add(fakeSchematic.x, fakeSchematic.y, fakeSchematic.z));
                    getIsland().setNetherhome(getIsland().getNetherhome().add(fakeSchematic.x, fakeSchematic.y, fakeSchematic.z));
                    if (IridiumSkyblock.getConfiguration().restartUpgradesOnRegen) {
                        getIsland().setSizeLevel(1);
                        getIsland().setMemberLevel(1);
                        getIsland().setWarpLevel(1);
                    }
                    return;
                }
            }
        }
    }
}
