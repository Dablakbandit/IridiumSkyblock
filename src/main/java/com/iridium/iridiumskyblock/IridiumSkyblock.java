package com.iridium.iridiumskyblock;

import com.iridium.iridiumskyblock.commands.CommandManager;
import com.iridium.iridiumskyblock.configs.*;
import com.iridium.iridiumskyblock.gui.*;
import com.iridium.iridiumskyblock.listeners.*;
import com.iridium.iridiumskyblock.placeholders.ClipPlaceholderAPIManager;
import com.iridium.iridiumskyblock.placeholders.MVDWPlaceholderAPIManager;
import com.iridium.iridiumskyblock.serializer.Persist;
import com.iridium.iridiumskyblock.support.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;

public class IridiumSkyblock extends JavaPlugin {

    private static IridiumSkyblock instance;

    public HashMap<Schematics.FakeSchematic, Schematic> schems = new HashMap<>();
    public HashMap<Schematics.FakeSchematic, Schematic> netherschems = new HashMap<>();

    public static Config configuration;
    public static Messages messages;
    public static Inventories inventories;
    public static Schematics schematics;
    public static Commands commands;
    public static BlockValues blockValues;
    public static Upgrades upgrades;

    private static Persist persist;

    private static IslandManager islandManager;

    private static CommandManager commandManager;

    public static TopGUI topGUI;

    public static HashMap<Integer, VisitGUI> visitGUI;

    public boolean updatingBlocks = false;

    private String latest;


    public static SkyblockGenerator generator;

    public static WorldEdit worldEdit;

    public List<String> languages = new ArrayList<>();

    @Override
    public void onEnable() {
        try {
            generator = new SkyblockGenerator();
            instance = this;

            super.onEnable();
            Bukkit.getUpdateFolderFile().mkdir();
            getDataFolder().mkdir();

            persist = new Persist();

            new Metrics(IridiumSkyblock.getInstance());

            if (!loadConfigs()) return;
            saveConfigs();

            commandManager = new CommandManager("island");
            commandManager.registerCommands();

            if (Bukkit.getPluginManager().getPlugin("Vault") != null) new Vault();
            if (Bukkit.getPluginManager().isPluginEnabled("WildStacker")) new Wildstacker();
            if (Bukkit.getPluginManager().isPluginEnabled("MergedSpawner")) new MergedSpawners();
            if (Bukkit.getPluginManager().isPluginEnabled("UltimateStacker")) new UltimateStacker();
            if (Bukkit.getPluginManager().isPluginEnabled("EpicSpawners")) new EpicSpawners();
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
                registerListeners(new onExpansionUnregister());
            getLanguages();
            Bukkit.getScheduler().runTask(this, () -> { // Call this a tick later to ensure all worlds are loaded
                loadIslandManager();
                if (getIslandManager() == null) return;

                if (Bukkit.getPluginManager().getPlugin("Multiverse-Core") != null) registerMultiverse();

                // Call it as a delayed task to wait for the server to properly load first
                Bukkit.getScheduler().scheduleSyncDelayedTask(IridiumSkyblock.getInstance(), IridiumSkyblock.getInstance()::islandValueManager);

                topGUI = new TopGUI();
                visitGUI = new HashMap<>();

                registerListeners(new onBlockPiston(), new onEntityPickupItem(), new onPlayerTalk(), new onPlayerTeleport(), new onPlayerPortal(), new onBlockBreak(), new onBlockPlace(), new onClick(), new onBlockFromTo(), new onPlayerJoinLeave(), new onPlayerTalk(), new onPlayerMove(), new onEntityDamageByEntity(),new onEntityExplode());

                Bukkit.getScheduler().scheduleAsyncRepeatingTask(IridiumSkyblock.getInstance(), this::saveIslandManager, 0, 20 * 60);

                if (configuration.doIslandBackup)
                    Bukkit.getScheduler().scheduleAsyncRepeatingTask(IridiumSkyblock.getInstance(), this::backupIslandManager, 0, 20 * 60 * getConfiguration().backupIntervalMinutes);

                Bukkit.getScheduler().scheduleAsyncRepeatingTask(IridiumSkyblock.getInstance(), this::addPages, 0, 20 * 60);

                setupPlaceholderAPI();

                Plugin worldedit = Bukkit.getPluginManager().getPlugin("WorldEdit");
                if (worldedit != null) {
                    if (worldedit.getDescription().getVersion().startsWith("6")) {
                        worldEdit = new WorldEdit6();
                    } else if (worldedit.getDescription().getVersion().startsWith("7")){
                        worldEdit = new WorldEdit7();
                    }
                }

                getLogger().info("-------------------------------");
                getLogger().info("");
                getLogger().info(getDescription().getName() + " Enabled!");
                getLogger().info("");
                getLogger().info("-------------------------------");

                Bukkit.getScheduler().scheduleAsyncDelayedTask(this, () -> {
                    try {
                        latest = new BufferedReader(new InputStreamReader(new URL("https://api.spigotmc.org/legacy/update.php?resource=62480").openConnection().getInputStream())).readLine();
                    } catch (IOException e) {
                        getLogger().warning("Failed to connect to api.spigotmc.org");
                    }
                    if (latest != null && !latest.equals(getDescription().getVersion())) {
                        getLogger().info("Newer version available: " + latest);
                        if (getConfiguration().automaticUpdate) {
                            getLogger().info("Attempting to download version: " + latest);
                            try {
                                getFile().renameTo(new File(getFile().getParentFile(), "/IridiumSkyblock-" + latest + ".jar"));
                                File file = new File(Bukkit.getUpdateFolderFile() + "/IridiumSkyblock-" + latest + ".jar");
                                file.createNewFile();
                                URL url = new URL("http://www.peachessupport.xyz/IridiumSkyblock-" + latest + ".jar");
                                OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
                                URLConnection conn = url.openConnection();
                                conn.setConnectTimeout(15000);
                                conn.setReadTimeout(15000);
                                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                                conn.setAllowUserInteraction(false);
                                conn.setDoOutput(true);
                                InputStream in = conn.getInputStream();
                                byte[] buffer = new byte[1024];

                                int numRead;
                                while ((numRead = in.read(buffer)) != -1) {
                                    out.write(buffer, 0, numRead);
                                }
                                in.close();
                                out.close();
                            } catch (Exception e) {
                                sendErrorMessage(e);
                            }
                        }
                    }
                });
            });
        } catch (Exception e) {
            sendErrorMessage(e);
        }
    }

    public void getLanguages() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            languages.clear();
            languages.add("English");
            languages.add("French");
        });
    }

    public void setLanguage(String language) {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {

            loadConfigs();
        });
    }

    private void registerMultiverse() {
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv import " + getIslandManager().getWorld().getName() + " normal -g " + getName());
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv modify set generator " + getName() + " " + getIslandManager().getWorld().getName());

        if (IridiumSkyblock.getConfiguration().netherIslands) {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv import " + getIslandManager().getNetherWorld().getName() + " nether -g " + getName());
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv modify set generator " + getName() + " " + getIslandManager().getNetherWorld().getName());
        }
    }

    @Override
    public void onDisable() {
        try {
            super.onDisable();

            saveData();

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.closeInventory();
            }

            getLogger().info("-------------------------------");
            getLogger().info("");
            getLogger().info(getDescription().getName() + " Disabled!");
            getLogger().info("");
            getLogger().info("-------------------------------");
        } catch (Exception e) {
            sendErrorMessage(e);
        }
    }

    public void saveIslandManager() {
        if (getIslandManager() != null) {
            getDataFolder().mkdir();
            if (islandManager != null) {
                getPersist().save(islandManager, getPersist().getFile("IslandManager_temp"));
                try {
                    if (persist.load(IslandManager.class, getPersist().getFile("IslandManager_temp")) == null) {
                        getPersist().getFile("IslandManager_temp").delete();
                        return;
                    }
                } catch (Exception e) {
                    getPersist().getFile("IslandManager_temp").delete();
                    return;
                }
                getPersist().getFile(islandManager).delete();
                getPersist().getFile("IslandManager_temp").renameTo(getPersist().getFile(islandManager));
            }
        }
    }

    public void backupIslandManager() {
        if (getIslandManager() != null) {
            File backupsFolder = new File(getDataFolder(), "backups");
            if (!backupsFolder.exists()) backupsFolder.mkdir();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -getConfiguration().deleteBackupsAfterDays);
            for (File file : backupsFolder.listFiles()) {
                Date date = getLocalDateTime(file.getName().replace(".json", "").replace("IslandManager_", ""));
                if (date == null) {
                    file.delete();
                } else {
                    if (date.before(cal.getTime())) {
                        file.delete();
                    }
                }
            }
            getPersist().save(islandManager, new File(backupsFolder, "IslandManager_" + getCurrentTimeStamp() + ".json"));
        }
    }

    public String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    public Date getLocalDateTime(String time) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");//dd/MM/yyyy
        try {
            return sdfDate.parse(time);
        } catch (ParseException e) {
            return null;
        }
    }


    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        if (worldName.equals(getConfiguration().worldName) || worldName.equals(getConfiguration().worldName + "_nether"))
            return generator;
        return super.getDefaultWorldGenerator(worldName, id);
    }

    private void addPages() {
        int size = (int) (Math.floor(Utils.getIslands().size() / 45.00) + 1);
        for (int i = 1; i <= size; i++) {
            if (!visitGUI.containsKey(i)) {
                visitGUI.put(i, new VisitGUI(i));
            }
        }
    }

    public void islandValueManager() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            ListIterator<Integer> islands = new ArrayList<>(islandManager.islands.keySet()).listIterator();

            @Override
            public void run() {
                if (!updatingBlocks) {
                    if (!islands.hasNext()) {
                        islands = new ArrayList<>(islandManager.islands.keySet()).listIterator();
                    }
                    if (islands.hasNext()) {
                        int id = islands.next();
                        Island island = IridiumSkyblock.getIslandManager().getIslandViaId(id);
                        if (island != null) {
                            updatingBlocks = true;
                            island.initBlocks();
                        }
                    }
                }
            }
        }, 0, 0);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            ListIterator<Integer> islands = new ArrayList<>(islandManager.islands.keySet()).listIterator();

            @Override
            public void run() {
                if (!islands.hasNext()) {
                    islands = new ArrayList<>(islandManager.islands.keySet()).listIterator();
                }
                if (islands.hasNext()) {
                    int id = islands.next();
                    Island island = IridiumSkyblock.getIslandManager().getIslandViaId(id);
                    if (island != null) {
                        island.calculateIslandValue();
                    }
                }
            }
        }, 0, IridiumSkyblock.getConfiguration().islandsUpdateInterval);
    }

    public void sendErrorMessage(Exception e) {
        e.printStackTrace();
    }

    public void registerListeners(Listener... listener) {
        for (Listener l : listener) {
            Bukkit.getPluginManager().registerEvents(l, this);
        }
    }

    private void setupPlaceholderAPI() {
        Plugin mvdw = getServer().getPluginManager().getPlugin("MVdWPlaceholderAPI");
        if (mvdw != null && mvdw.isEnabled()) {
            new MVDWPlaceholderAPIManager().register();
            getLogger().info("Successfully registered placeholders with MVDWPlaceholderAPI.");
        }
        setupClipsPlaceholderAPI();
    }

    public void setupClipsPlaceholderAPI() {
        Plugin clip = getServer().getPluginManager().getPlugin("PlaceholderAPI");
        if (clip != null && clip.isEnabled()) {
            if (new ClipPlaceholderAPIManager().register()) {
                getLogger().info("Successfully registered placeholders with PlaceholderAPI.");
            }
        }
    }

    public void loadSchematics() throws IOException {
        File schematicFolder = new File(getDataFolder(), "schematics");
        if (!schematicFolder.exists()) {
            schematicFolder.mkdir();
            if (!new File(schematicFolder, "island.schematic").exists()) {
                if (getResource("schematics/island.schematic") != null) {
                    saveResource("schematics/island.schematic", false);
                }
            }
            if (!new File(schematicFolder, "nether.schematic").exists()) {
                if (getResource("schematics/nether.schematic") != null) {
                    saveResource("schematics/nether.schematic", false);
                }
            }
        }

        schems.clear();

        for (Schematics.FakeSchematic fakeSchematic : schematics.schematics) {
            schems.put(fakeSchematic, Schematic.loadSchematic(new File(schematicFolder, fakeSchematic.name)));
            if (fakeSchematic.netherisland == null) {
                fakeSchematic.netherisland = fakeSchematic.name;
            }
            netherschems.put(fakeSchematic, Schematic.loadSchematic(new File(schematicFolder, fakeSchematic.netherisland)));
        }
    }

    public void loadIslandManager() {
        islandManager = persist.getFile(IslandManager.class).exists() ? persist.load(IslandManager.class) : new IslandManager();

        if (islandManager == null) return;

        for (Island island : islandManager.islands.values()) {
            island.init();
        }
        getIslandManager().getWorld().getWorldBorder().setSize(Double.MAX_VALUE);
        if (getConfiguration().netherIslands)
            getIslandManager().getNetherWorld().getWorldBorder().setSize(Double.MAX_VALUE);
    }

    public boolean loadConfigs() {
        configuration = persist.getFile(Config.class).exists() ? persist.load(Config.class) : new Config();
        messages = persist.getFile(Messages.class).exists() ? persist.load(Messages.class) : new Messages();
        upgrades = persist.getFile(Upgrades.class).exists() ? persist.load(Upgrades.class) : new Upgrades();
        inventories = persist.getFile(Inventories.class).exists() ? persist.load(Inventories.class) : new Inventories();
        schematics = persist.getFile(Schematics.class).exists() ? persist.load(Schematics.class) : new Schematics();
        commands = persist.getFile(Commands.class).exists() ? persist.load(Commands.class) : new Commands();
        blockValues = persist.getFile(BlockValues.class).exists() ? persist.load(BlockValues.class) : new BlockValues();

        if (configuration == null || messages == null || inventories == null || schematics == null || commands == null || blockValues == null ) {
            return false;
        }

        getBlockValues().blockvalue.remove(MultiversionMaterials.AIR);

        if (getConfiguration().blockvalue != null) {
            getBlockValues().blockvalue = (HashMap<MultiversionMaterials, Integer>) getConfiguration().blockvalue.clone();
            getConfiguration().blockvalue = null;
        }
        if (getConfiguration().spawnervalue != null) {
            getBlockValues().spawnervalue = (HashMap<String, Integer>) getConfiguration().spawnervalue.clone();
            getConfiguration().spawnervalue = null;
        }
        int max = 0;
        for (Upgrades.IslandUpgrade size : getUpgrades().sizeUpgrade.upgrades.values()) {
            if (max < size.size) {
                max = size.size;
            }
        }
        if (getConfiguration().distance <= max) {
            getConfiguration().distance = max + 1;
        }
        try {
            loadSchematics();
        } catch (Exception e) {

        }
        return true;
    }

    public void saveData() {
        if (islandManager != null) persist.save(islandManager);
    }

    public void saveConfigs() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            if (configuration != null) persist.save(configuration);
            if (messages != null) persist.save(messages);
            if (inventories != null) persist.save(inventories);
            if (schematics != null) persist.save(schematics);
            if (commands != null) persist.save(commands);
            if (blockValues != null) persist.save(blockValues);
        });
    }

    public String getLatest() {
        return latest;
    }

    public static IridiumSkyblock getInstance() {
        return instance;
    }

    public static IslandManager getIslandManager() {
        return islandManager;
    }

    public static Config getConfiguration() {
        return configuration;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

    public static Messages getMessages() {
        return messages;
    }

    public static BlockValues getBlockValues() {
        return blockValues;
    }

    public static Upgrades getUpgrades() {
        if (upgrades == null) {
            upgrades = new Upgrades();
            IridiumSkyblock.getPersist().getFile(upgrades).delete();
            IridiumSkyblock.getInstance().saveConfigs();
        }
        return upgrades;
    }

    public static Commands getCommands() {
        return commands;
    }

    public static Schematics getSchematics() {
        return schematics;
    }

    public static Inventories getInventories() {
        return inventories;
    }

    public static Persist getPersist() {
        return persist;
    }
}