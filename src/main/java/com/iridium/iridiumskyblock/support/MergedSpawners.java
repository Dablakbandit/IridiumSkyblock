package com.iridium.iridiumskyblock.support;

import com.vk2gpz.mergedspawner.api.MergedSpawnerAPI;
import org.bukkit.block.CreatureSpawner;

public class MergedSpawners {

    public static boolean enabled = false;

    public MergedSpawners() {
        enabled = true;
    }

    public static int getSpawnerAmount(CreatureSpawner spawner) {
        return MergedSpawnerAPI.getInstance().getCountFor(spawner.getBlock());
    }
}
