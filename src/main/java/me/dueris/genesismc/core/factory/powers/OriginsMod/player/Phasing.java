package me.dueris.genesismc.core.factory.powers.OriginsMod.player;

import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent;
import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.enums.OriginDataType;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.factory.conditions.entity.EntityCondition;
import me.dueris.genesismc.core.utils.OriginContainer;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorldBorder;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

import static me.dueris.genesismc.core.factory.powers.Powers.phasing;
import static org.bukkit.ChatColor.DARK_AQUA;
import static org.bukkit.ChatColor.GRAY;

public class Phasing extends BukkitRunnable implements Listener {

    private final Long interval;

    public static ArrayList<Player> IN_PHANTOM_FORM_BLOCKS = new ArrayList<>();

    private final int ticksE;
    public Phasing(){
        this.interval = 100L;
        this.ticksE = 0;
    }

//TODO: make bedrock Phasing work by disabling merge and setting into spectator only instead of merging gamemodes
    //TODO: fix blindness flickers bc mc renderer sucks
    
    public static void setInPhasingBlockForm(Player p) {
        //camera client renderer
        if(p.getGameMode().equals(GameMode.CREATIVE)){
            p.setGameMode(GameMode.SPECTATOR);
            ((CraftPlayer) p).getHandle().connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.CHANGE_GAME_MODE, 1));
        } else if (p.getGameMode() != GameMode.SPECTATOR) {
            p.setGameMode(GameMode.SPECTATOR);
            ((CraftPlayer) p).getHandle().connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.CHANGE_GAME_MODE, 0));
        }
        p.setCollidable(false);
        IN_PHANTOM_FORM_BLOCKS.add(p);
        p.setAllowFlight(true);
        p.setFlying(true);
    }

    public static void initializePhantomOverlay(Player player) {
        CraftWorldBorder border = (CraftWorldBorder) Bukkit.createWorldBorder();
        border.setCenter(player.getWorld().getWorldBorder().getCenter());
        border.setSize(player.getWorld().getWorldBorder().getSize());
        border.setWarningDistance(999999999);
        player.setWorldBorder(border);
    }

    public static void deactivatePhantomOverlay(Player player) {
        player.setWorldBorder(player.getWorld().getWorldBorder());
    }

    @EventHandler
    public void shiftGoDown(PlayerToggleSneakEvent e) {
        if (e.isSneaking()) {
            Player p = e.getPlayer();
            PersistentDataContainer data = p.getPersistentDataContainer();
            if (OriginPlayer.isInPhantomForm(p)) {
                if (phasing.contains(p)) {
                    if (!p.getLocation().getBlock().isCollidable()) {
                        for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                            if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).isCollidable()) {
                                Location currentLocation = p.getLocation();
                                Location targetLocation = currentLocation.getBlock().getRelative(BlockFace.DOWN).getLocation();
                                Location loc = new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY(), targetLocation.getZ(), p.getEyeLocation().getYaw(), p.getEyeLocation().getPitch());
                                if(EntityCondition.check(p, origin, "origins:phasing", p) == "true" || EntityCondition.check(p, origin, "origins:phasing", p) == "null"){
                                    p.teleport(loc);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (phasing.contains(p)) {
                for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                    if (ConditionExecutor.check(p, origin, "origins:phasing", null, p)) {
                        if (OriginPlayer.isInPhantomForm(p)) {
                            if ((p.getLocation().add(0.55F, 0, 0.55F).getBlock().isSolid() ||
                            p.getLocation().add(0.55F, 0, 0).getBlock().isSolid() ||
                            p.getLocation().add(0, 0, 0.55F).getBlock().isSolid() ||
                            p.getLocation().add(-0.55F, 0, -0.55F).getBlock().isSolid() ||
                            p.getLocation().add(0, 0, -0.55F).getBlock().isSolid() ||
                            p.getLocation().add(-0.55F, 0, 0).getBlock().isSolid() ||
                            p.getLocation().add(0.55F, 0, -0.55F).getBlock().isSolid() ||
                            p.getLocation().add(-0.55F, 0, 0.55F).getBlock().isSolid() ||
                            p.getLocation().add(0, 0.5, 0).getBlock().isSolid() ||

                            p.getEyeLocation().add(0.55F, 0, 0.55F).getBlock().isSolid() ||
                            p.getEyeLocation().add(0.55F, 0, 0).getBlock().isSolid() ||
                            p.getEyeLocation().add(0, 0, 0.55F).getBlock().isSolid() ||
                            p.getEyeLocation().add(-0.55F, 0, -0.55F).getBlock().isSolid() ||
                            p.getEyeLocation().add(0, 0, -0.55F).getBlock().isSolid() ||
                            p.getEyeLocation().add(-0.55F, 0, 0).getBlock().isSolid() ||
                            p.getEyeLocation().add(0.55F, 0, -0.55F).getBlock().isSolid() ||
                            p.getEyeLocation().add(-0.55F, 0, 0.55F).getBlock().isSolid())
                        ) {
                                setInPhasingBlockForm(p);
                                if(origin.getPowerFileFromType("origins:phasing").getOverlay()){
                                    initializePhantomOverlay(p);
                                }

                                p.setFlySpeed(0.04F);
                                p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "insideBlock"), PersistentDataType.BOOLEAN, true);

                                if(origin.getPowerFileFromType("origins:phasing").getRenderType().equalsIgnoreCase("blindness")){
                                    Float viewD = origin.getPowerFileFromType("origins:phasing").getViewDistance().floatValue();
                                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, viewD.intValue() * 2, 255, false, false, false));
                                }

                            } else {
                                if (p.getGameMode().equals(GameMode.SPECTATOR)) {
                                    if (p.getPreviousGameMode().equals(GameMode.CREATIVE)) {
                                        p.setGameMode(p.getPreviousGameMode());
                                        p.setFlying(false);
                                    } else {
                                        p.setGameMode(p.getPreviousGameMode());
                                        if (p.isOnGround()) ;
                                        p.setFlying(false);
                                    }
                                    p.setFlySpeed(0.1F);
                                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "insideBlock"), PersistentDataType.BOOLEAN, false);

                                }
                            }
                        } else {
                            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "insideBlock"), PersistentDataType.BOOLEAN, false);
                        }
                    } else {
                        p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "insideBlock"), PersistentDataType.BOOLEAN, false);
                    }
                }
            }
        }
    }

    public static float getGamemodeFloat(GameMode gameMode) {
        switch (gameMode) {
            case CREATIVE:
                return 1.0f;
            case SURVIVAL:
                return 0.0f;
            case ADVENTURE:
                return 2.0f;
            case SPECTATOR:
                return 3.0f;
            default:
                return 0.0f;
        }
    }

    @EventHandler
    public void CancelSpectate(PlayerStartSpectatingEntityEvent e) {
        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        if (phasing.contains(p)) {
            if (OriginPlayer.isInPhantomForm(p)) {
                e.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        ItemStack spectatorswitch = new ItemStack(Material.PHANTOM_MEMBRANE);
        ItemMeta switch_meta = spectatorswitch.getItemMeta();
        switch_meta.setDisplayName(GRAY + "Phasing Form");
        ArrayList<String> pearl_lore = new ArrayList();
        switch_meta.setUnbreakable(true);
        switch_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        switch_meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        switch_meta.setLore(pearl_lore);
        switch_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        spectatorswitch.setItemMeta(switch_meta);

        Player p = e.getPlayer();
        if (phasing.contains(e.getPlayer())) {
            e.getPlayer().getInventory().addItem(spectatorswitch);
        }

        if(OriginPlayer.isInPhantomForm(e.getPlayer())){
            e.getPlayer().setGameMode(GameMode.SURVIVAL);
            OriginPlayer.setOriginData(p, OriginDataType.IN_PHASING_FORM, false);
            p.sendActionBar(DARK_AQUA + "Deactivated Phasing Form");
        }
    }

    @EventHandler
    public void onKey(PlayerInteractEvent e) {
        ItemStack spectatorswitch = new ItemStack(Material.PHANTOM_MEMBRANE);
        ItemMeta switch_meta = spectatorswitch.getItemMeta();
        switch_meta.setDisplayName(GRAY + "Phasing Form");
        ArrayList<String> pearl_lore = new ArrayList();
        switch_meta.setUnbreakable(true);
        switch_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        switch_meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        switch_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        switch_meta.setLore(pearl_lore);
        spectatorswitch.setItemMeta(switch_meta);

        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        boolean phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.BOOLEAN);
        for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
            if (phasing.contains(p)) {
                if (e.getItem() != null) {
                    if (e.getItem().isSimilar(spectatorswitch)) {
                        if(phantomid){
                            if (ConditionExecutor.check(p, origin, "origins:phasing", null, p)) {
                                OriginPlayer.setOriginData(p, OriginDataType.IN_PHASING_FORM, false);
                                p.sendActionBar(DARK_AQUA + "Deactivated Phasing Form");
                                if (p.getGameMode().equals(GameMode.SPECTATOR)) {
                                    if (p.getPreviousGameMode().equals(GameMode.CREATIVE)) {
                                        p.setGameMode(p.getPreviousGameMode());
                                        p.setFlying(false);
                                    } else {
                                        p.setGameMode(p.getPreviousGameMode());
                                        if (p.isOnGround()) ;
                                        p.setFlying(false);
                                    }

                                }
                            }
                        }else{
                            if (ConditionExecutor.check(p, origin, "origins:phasing", null, p)) {
                                p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.BOOLEAN, true);
                                OriginPlayer.setOriginData(p, OriginDataType.IN_PHASING_FORM, true);
                                p.sendActionBar(DARK_AQUA + "Activated Phasing Form");
                            }
                        }
                        e.setCancelled(true);
                    }
                }

            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        ItemStack spectatorswitch = new ItemStack(Material.PHANTOM_MEMBRANE);
        ItemMeta switch_meta = spectatorswitch.getItemMeta();
        switch_meta.setDisplayName(GRAY + "Phasing Form");
        ArrayList<String> pearl_lore = new ArrayList();
        switch_meta.setUnbreakable(true);
        switch_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        switch_meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        switch_meta.setLore(pearl_lore);
        switch_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        spectatorswitch.setItemMeta(switch_meta);

        if (phasing.contains(e.getPlayer())) {
            if (e.getItemDrop().getItemStack().isSimilar(spectatorswitch)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onTransfer(InventoryClickEvent e) {
        if (e.getClick().isKeyboardClick()) {
            if (e.getView().getTopInventory().getType() == InventoryType.CRAFTING) return;
            if (e.getView().getBottomInventory().getItem(e.getHotbarButton()) != null) {
                ItemStack transferred = e.getView().getBottomInventory().getItem(e.getHotbarButton());
                if (transferred == null) return;
                if (transferred.getType().equals(Material.PHANTOM_MEMBRANE)) {
                    ItemStack spectatorswitch = new ItemStack(Material.PHANTOM_MEMBRANE);
                    ItemMeta switch_meta = spectatorswitch.getItemMeta();
                    switch_meta.setDisplayName(GRAY + "Phasing Form");
                    ArrayList<String> pearl_lore = new ArrayList();
                    switch_meta.setUnbreakable(true);
                    switch_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                    switch_meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
                    switch_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    switch_meta.setLore(pearl_lore);
                    spectatorswitch.setItemMeta(switch_meta);

                    if (transferred.isSimilar(spectatorswitch)) {
                        e.setCancelled(true);
                    }
                }
            }

            return;
        }
        if (e.getView().getTopInventory().getType() != InventoryType.CRAFTING) {
            if (e.getView().getTopInventory().getHolder() != null && e.getView().getTopInventory().getHolder().equals(e.getWhoClicked()))
                return;
            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().getType().equals(Material.PHANTOM_MEMBRANE)) {
                ItemStack spectatorswitch = new ItemStack(Material.PHANTOM_MEMBRANE);
                ItemMeta switch_meta = spectatorswitch.getItemMeta();
                switch_meta.setDisplayName(GRAY + "Phasing Form");
                ArrayList<String> pearl_lore = new ArrayList();
                switch_meta.setUnbreakable(true);
                switch_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                switch_meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
                switch_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                switch_meta.setLore(pearl_lore);
                spectatorswitch.setItemMeta(switch_meta);

                if (e.getCurrentItem().isSimilar(spectatorswitch)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        ItemStack spectatorswitch = new ItemStack(Material.PHANTOM_MEMBRANE);
        ItemMeta switch_meta = spectatorswitch.getItemMeta();
        switch_meta.setDisplayName(GRAY + "Phasing Form");
        ArrayList<String> pearl_lore = new ArrayList();
        switch_meta.setUnbreakable(true);
        switch_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        switch_meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        switch_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        switch_meta.setLore(pearl_lore);
        spectatorswitch.setItemMeta(switch_meta);

        Player p = e.getPlayer();
        e.getDrops().remove(spectatorswitch);
    }
}

//
//                                List<Block> blocksToRegen = new ArrayList<>();
//
//                                // Clear blocks for renderer
//                                for (int x = minX; x <= maxX; x++) {
//                                    for (int y = minY; y <= maxY; y++) {
//                                        for (int z = minZ; z <= maxZ; z++) {
//                                            Location loc = new Location(world, x + 0.5, y + 0.5, z + 0.5);
//
//                                            double distanceSquared = Math.pow(x - center.getX(), 2) + Math.pow(y - center.getY(), 2) + Math.pow(z - center.getZ(), 2);
//
//                                            if (distanceSquared <= radius * radius) {
//                                                Block block = loc.getBlock();
//                                                if (block.isEmpty()) continue; // Skip empty blocks
//                                                p.sendBlockChange(loc, Material.AIR.createBlockData());
//                                                blocksToRegen.add(block);
//                                            }
//                                        }
//                                    }
//                                }



// regen all the blocks
//                                new BukkitRunnable() {
//                                    @Override
//                                    public void run() {
//                                        if (blocksToRegen.isEmpty() || !OriginPlayer.isInPhantomForm(p)) {
//                                            for (Block block : blocksToRegen) {
//                                                p.sendBlockChange(block.getLocation(), block.getBlockData());
//                                            }
//                                            blocksToRegen.clear();
//                                            // Clear block positions when not in Phantom form
//                                            blockLocations.clear();
//                                            // Cancel the runnable when blocks are regenerated and hashmap is empty
//                                            this.cancel();
//                                        } else {
//                                            if(OriginPlayer.isInPhantomForm(p)) return;
//                                            Block block = blocksToRegen.remove(0);
//                                            p.sendBlockChange(block.getLocation(), block.getBlockData());
//                                        }
//                                    }
//                                }.runTaskTimer(getPlugin(), 0, 1);
