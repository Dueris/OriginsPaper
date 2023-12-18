package me.dueris.genesismc.factory.powers.player;

import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorldBorder;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.geysermc.geyser.api.GeyserApi;

import java.util.ArrayList;
import java.util.HashMap;

public class Phasing extends CraftPower implements Listener {

    public static ArrayList<Player> IN_PHANTOM_FORM_BLOCKS = new ArrayList<>();
    private final Long interval;
    private final int ticksE;
    public static HashMap<Player, Boolean> test = new HashMap<>();
    
    public Phasing() {
        this.interval = 100L;
        this.ticksE = 0;
    }

    @EventHandler
    public void je(PlayerJoinEvent e){
        test.put(e.getPlayer(), false);
    }

    @Override
    public void run(Player p) {
        if (getPowerArray().contains(p)) {
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    if (conditionExecutor.check("condition", "conditions", p, power, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                        setActive(power.getTag(), true);
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
                            if (power.getOverlay()) {
                                initializePhantomOverlay(p);
                            }

                            p.setFlySpeed(0.04F);
                            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "insideBlock"), PersistentDataType.BOOLEAN, true);

//                            if (power.getRenderType().equalsIgnoreCase("blindness")) {
//                                Float viewD = power.getViewDistance().floatValue();
//                                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, viewD.intValue() * 2, 255, false, false, false));
//                            }
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
                        setActive(power.getTag(), false);
                        if(test.get(p) == null){
                            test.put(p, false);
                        }else if (test.get(p)) {
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
                            test.put(p, false);
                        }
                    }
                }
            }
        }
    }

    public static void setInPhasingBlockForm(Player p) {
        test.put(p, true);
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("Geyser-Spigot")) {
            if (GeyserApi.api().isBedrockPlayer(p.getUniqueId())) {
                if (p.getGameMode().equals(GameMode.CREATIVE)) {
                    p.setGameMode(GameMode.SPECTATOR);
                } else if (p.getGameMode() != GameMode.SPECTATOR) {
                    p.setGameMode(GameMode.SPECTATOR);
                }
                p.setCollidable(false);
                IN_PHANTOM_FORM_BLOCKS.add(p);
                p.setAllowFlight(true);
                p.setFlying(true);
            } else {
                if (p.getGameMode().equals(GameMode.CREATIVE)) {
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
        } else {
            if (p.getGameMode().equals(GameMode.CREATIVE)) {
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

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    @EventHandler
    public void shiftGoDown(PlayerToggleSneakEvent e) {
        if (e.isSneaking()) {
            Player p = e.getPlayer();
            PersistentDataContainer data = p.getPersistentDataContainer();
            if (OriginPlayer.isInPhantomForm(p)) {
                if (phasing.contains(p)) {
                    if (!p.getLocation().getBlock().isCollidable()) {
                        for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                            for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                                if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).isCollidable()) {
                                    Location currentLocation = p.getLocation();
                                    Location targetLocation = currentLocation.getBlock().getRelative(BlockFace.DOWN).getLocation();
                                    Location loc = new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY(), targetLocation.getZ(), p.getEyeLocation().getYaw(), p.getEyeLocation().getPitch());
                                    ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                                    if (conditionExecutor.check("phase_down_condition", "phase_down_conditions", p, power, getPowerFile(), p, null, loc.getBlock(), null, p.getItemInHand(), null)) {
                                        p.teleportAsync(loc);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:phasing";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return phasing;
    }

    @EventHandler
    public void CancelSpectate(PlayerStartSpectatingEntityEvent e) {
        Player p = e.getPlayer();
        if (phasing.contains(p)) {
            if (OriginPlayer.isInPhantomForm(p)) {
                e.setCancelled(true);
            }

        }
    }
}