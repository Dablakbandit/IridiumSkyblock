package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Utils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.block.Biome;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.*;

public class BiomeGUI extends GUI implements Listener {

    public int page;

    public BiomeGUI root;

    public HashMap<Integer, BiomeGUI> pages = new HashMap<>();

    public HashMap<Integer, Biome> biomes = new HashMap<>();

    public BiomeGUI(Island island) {
        IridiumSkyblock.getInstance().registerListeners(this);
        int size = (int) (Math.floor(Biome.values().length / ((double) IridiumSkyblock.getInventories().biomeGUISize - 9)) + 1);
        for (int i = 1; i <= size; i++) {
            pages.put(i, new BiomeGUI(island, i, this));
        }
    }

    public BiomeGUI(Island island, int page, BiomeGUI root) {
        super(island, IridiumSkyblock.getInventories().biomeGUISize, IridiumSkyblock.getInventories().biomeGUITitle);
        this.page = page;
        this.root = root;
    }

    @Override
    public void addContent() {
        super.addContent();
        int i = 0;
        int slot = 0;
        List<Biome> biomeList = Arrays.asList(Biome.values());
        biomeList.sort(Comparator.comparing(Biome::toString));
        for (Biome biome : biomeList) {
            if (i >= 45 * (page - 1) && slot < 45) {
                setItem(slot, Utils.makeItem(IridiumSkyblock.getInventories().biome, Collections.singletonList(new Utils.Placeholder("biome", WordUtils.capitalize(biome.name().toLowerCase().replace("_", " "))))));
                biomes.put(slot, biome);
                slot++;
            }
            i++;
        }
        setItem(getInventory().getSize() - 3, Utils.makeItem(IridiumSkyblock.getInventories().nextPage));
        setItem(getInventory().getSize() - 7, Utils.makeItem(IridiumSkyblock.getInventories().previousPage));
    }

    @EventHandler
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (getInventory() == null) {
            for (BiomeGUI gui : pages.values()) {
                gui.onInventoryClick(e);
            }
        } else {
            if (e.getInventory().equals(getInventory())) {
                e.setCancelled(true);
                if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
                if (e.getSlot() == getInventory().getSize() - 3) {
                    if (root.pages.containsKey(page + 1)) {
                        e.getWhoClicked().openInventory(root.pages.get(page + 1).getInventory());
                    }
                }
                if (e.getSlot() == getInventory().getSize() - 7) {
                    if (root.pages.containsKey(page - 1)) {
                        e.getWhoClicked().openInventory(root.pages.get(page - 1).getInventory());
                    }
                }
                if (biomes.containsKey(e.getSlot())) {
                    getIsland().setBiome(biomes.get(e.getSlot()));
                }
            }
        }
    }
}