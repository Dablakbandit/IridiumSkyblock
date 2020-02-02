package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class onPlayerJoinLeave implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (e.getPlayer().isOp()) {
            if (IridiumSkyblock.getInstance().getLatest() != null && IridiumSkyblock.getConfiguration().notifyAvailableUpdate && !IridiumSkyblock.getInstance().getLatest().equals(IridiumSkyblock.getInstance().getDescription().getVersion())) {
                e.getPlayer().sendMessage(Utils.color(IridiumSkyblock.getConfiguration().prefix + " &7This message is only seen by opped players."));
                e.getPlayer().sendMessage(Utils.color(IridiumSkyblock.getConfiguration().prefix + " &7Newer version available: " + IridiumSkyblock.getInstance().getLatest()));
            }
        }
        User u = User.getUser(e.getPlayer());
        u.name = e.getPlayer().getName();

        u.bypassing = false;
        Island island = IridiumSkyblock.getIslandManager().getIslandViaLocation(e.getPlayer().getLocation());
        if (island != null) {
            island.sendBorder(e.getPlayer());
        }
    }
}
