package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Schematics;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.concurrent.TimeUnit;

public class IslandMenuGUI extends GUI implements Listener {

    public ConfirmationGUI delete;

    public IslandMenuGUI(Island island) {
        super(island, IridiumSkyblock.getInventories().islandMenuGUISize, IridiumSkyblock.getInventories().islandMenuGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);
        this.delete = new ConfirmationGUI(island, () -> getIsland().delete(), IridiumSkyblock.getMessages().deleteAction);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (IridiumSkyblock.getIslandManager().islands.containsKey(islandID)) {
            setItem(IridiumSkyblock.getInventories().home.slot == null ? 0 : IridiumSkyblock.getInventories().home.slot, Utils.makeItemHidden(IridiumSkyblock.getInventories().home, getIsland()));
            setItem(IridiumSkyblock.getInventories().members.slot == null ? 1 : IridiumSkyblock.getInventories().members.slot, Utils.makeItemHidden(IridiumSkyblock.getInventories().members, getIsland()));
            setItem(IridiumSkyblock.getInventories().regen.slot == null ? 2 : IridiumSkyblock.getInventories().regen.slot, Utils.makeItemHidden(IridiumSkyblock.getInventories().regen, getIsland()));
            setItem(IridiumSkyblock.getInventories().permissions.slot == null ? 6 : IridiumSkyblock.getInventories().permissions.slot, Utils.makeItemHidden(IridiumSkyblock.getInventories().permissions, getIsland()));
            setItem(IridiumSkyblock.getInventories().top.slot == null ? 7 : IridiumSkyblock.getInventories().top.slot, Utils.makeItemHidden(IridiumSkyblock.getInventories().top, getIsland()));
            setItem(IridiumSkyblock.getInventories().warps.slot == null ? 8 : IridiumSkyblock.getInventories().warps.slot, Utils.makeItemHidden(IridiumSkyblock.getInventories().warps, getIsland()));
            setItem(IridiumSkyblock.getInventories().border.slot == null ? 9 : IridiumSkyblock.getInventories().border.slot, Utils.makeItemHidden(IridiumSkyblock.getInventories().border, getIsland()));
            setItem(IridiumSkyblock.getInventories().coop.slot == null ? 10 : IridiumSkyblock.getInventories().coop.slot, Utils.makeItemHidden(IridiumSkyblock.getInventories().coop, getIsland()));
            setItem(IridiumSkyblock.getInventories().biomes.slot == null ? 12 : IridiumSkyblock.getInventories().biomes.slot, Utils.makeItemHidden(IridiumSkyblock.getInventories().biomes, getIsland()));
            setItem(IridiumSkyblock.getInventories().delete.slot == null ? 26 : IridiumSkyblock.getInventories().delete.slot, Utils.makeItemHidden(IridiumSkyblock.getInventories().delete, getIsland()));
        }
    }

    @Override
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            Player p = (Player) e.getWhoClicked();
            User u = User.getUser(p);
            if (e.getSlot() == (IridiumSkyblock.getInventories().home.slot == null ? 0 : IridiumSkyblock.getInventories().home.slot)) {
                p.closeInventory();
                getIsland().teleportHome(p);
            } else if (e.getSlot() == (IridiumSkyblock.getInventories().members.slot == null ? 1 : IridiumSkyblock.getInventories().members.slot)) {
                p.closeInventory();
                p.openInventory(getIsland().getMembersGUI().getInventory());
            } else if (e.getSlot() == (IridiumSkyblock.getInventories().regen.slot == null ? 2 : IridiumSkyblock.getInventories().regen.slot)) {
                p.closeInventory();
                if (u.bypassing || getIsland().getPermissions(u.role).regen) {
                    long time = getIsland().canGenerate() / 1000;
                    if (time == 0 || u.bypassing) {
                        if (IridiumSkyblock.getInstance().schems.size() == 1) {
                            p.openInventory(new ConfirmationGUI(getIsland(), () -> {
                                for (Schematics.FakeSchematic schematic : IridiumSkyblock.getInstance().schems.keySet()) {
                                    getIsland().setSchematic(schematic.name);
                                    getIsland().setHome(getIsland().getHome().add(schematic.x, schematic.y, schematic.z));
                                    getIsland().setNetherhome(getIsland().getNetherhome().add(schematic.x, schematic.y, schematic.z));
                                }
                                getIsland().pasteSchematic(true);
                                if (IridiumSkyblock.getConfiguration().restartUpgradesOnRegen) {
                                    getIsland().setSizeLevel(1);
                                    getIsland().setMemberLevel(1);
                                    getIsland().setWarpLevel(1);
                                }
                            }, IridiumSkyblock.getMessages().resetAction).getInventory());
                        } else {
                            p.openInventory(getIsland().getSchematicSelectGUI().getInventory());
                        }
                    } else {
                        int day = (int) TimeUnit.SECONDS.toDays(time);
                        int hours = (int) Math.floor(TimeUnit.SECONDS.toHours(time - day * 86400));
                        int minute = (int) Math.floor((time - day * 86400 - hours * 3600) / 60.00);
                        int second = (int) Math.floor((time - day * 86400 - hours * 3600) % 60.00);
                        p.sendMessage(Utils.color(IridiumSkyblock.getMessages().regenCooldown.replace("%days%", day + "").replace("%hours%", hours + "").replace("%minutes%", minute + "").replace("%seconds%", second + "").replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    }
                } else {
                    p.sendMessage(Utils.color(IridiumSkyblock.getMessages().noPermission.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            } else if (e.getSlot() == (IridiumSkyblock.getInventories().permissions.slot == null ? 6 : IridiumSkyblock.getInventories().permissions.slot)) {
                p.closeInventory();
                p.openInventory(getIsland().getPermissionsGUI().getInventory());
            } else if (e.getSlot() == (IridiumSkyblock.getInventories().top.slot == null ? 7 : IridiumSkyblock.getInventories().top.slot)) {
                p.closeInventory();
                p.openInventory(IridiumSkyblock.topGUI.getInventory());
            } else if (e.getSlot() == (IridiumSkyblock.getInventories().warps.slot == null ? 8 : IridiumSkyblock.getInventories().warps.slot)) {
                p.closeInventory();
                p.openInventory(getIsland().getWarpGUI().getInventory());
            } else if (e.getSlot() == (IridiumSkyblock.getInventories().border.slot == null ? 9 : IridiumSkyblock.getInventories().border.slot)) {
                p.closeInventory();
                p.openInventory(getIsland().getBorderColorGUI().getInventory());
            } else if (e.getSlot() == (IridiumSkyblock.getInventories().coop.slot == null ? 10 : IridiumSkyblock.getInventories().coop.slot)) {
                p.closeInventory();
                p.openInventory(getIsland().getCoopGUI().getInventory());
            } else if (e.getSlot() == (IridiumSkyblock.getInventories().biomes.slot == null ? 12 : IridiumSkyblock.getInventories().biomes.slot)) {
                p.closeInventory();
                p.openInventory(getIsland().getBiomeGUI().pages.get(1).getInventory());
            } else if (e.getSlot() == (IridiumSkyblock.getInventories().delete.slot == null ? 26 : IridiumSkyblock.getInventories().delete.slot)) {
                p.closeInventory();
                if (u.bypassing || getIsland().getOwner().equalsIgnoreCase(u.player)) {
                    p.openInventory(delete.getInventory());
                } else {
                    p.sendMessage(Utils.color(IridiumSkyblock.getMessages().mustBeIslandOwner.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            }
        }
    }
}
