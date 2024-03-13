package me.dueris.genesismc.factory.powers.apoli;

import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.apoli.RaycastUtils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.Optionull;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorldBorder;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.geysermc.geyser.api.GeyserApi;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

/**
 * Java Packet Manipulation(JPM) code was made in contribution with Origins-Reborn author, cometcake575
 * aka i helped fix bugs and he made the packet code/discovery with the info packets XD
 * <p>
 * cometcakes theory:
 * The client side has 2 gamemodes, one for all players and one for specifically themselves.
 * Values required in both the server and client, or required for both the client and other players, are stored
 * within the other players one (the bit affected by the ClientboundPlayerInfoUpdatePacket to allow a survival player to go into blocks),
 * and the one that is required only in the client for knowing stuff like which hotbar to render
 */
public class Phasing extends CraftPower implements Listener {

	public static ArrayList<Player> inPhantomFormBlocks = new ArrayList<>();
	public static HashMap<Player, Boolean> test = new HashMap<>();

	protected static void sendJavaPacket(ServerPlayer player) {
		GameType gamemode = GameType.SPECTATOR;
		ClientboundPlayerInfoUpdatePacket.Entry entry = new ClientboundPlayerInfoUpdatePacket.Entry(player.getUUID(), player.getGameProfile(), true, 1, gamemode, player.getTabListDisplayName(), Optionull.map(player.getChatSession(), RemoteChatSession::asData));
		ClientboundPlayerInfoUpdatePacket packet = new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE), entry);
		player.connection.send(packet);
	}

	protected static void resyncJavaPlayer(ServerPlayer player) {
		if (player.gameMode.getGameModeForPlayer().equals(GameType.SPECTATOR)) {
			player.gameMode.changeGameModeForPlayer(GameType.SURVIVAL);
		}
		GameType gamemode = player.gameMode.getGameModeForPlayer();
		ClientboundPlayerInfoUpdatePacket.Entry entry = new ClientboundPlayerInfoUpdatePacket.Entry(player.getUUID(), player.getGameProfile(), true, 1, gamemode, player.getTabListDisplayName(), Optionull.map(player.getChatSession(), RemoteChatSession::asData));
		ClientboundPlayerInfoUpdatePacket packet = new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE), entry);
		player.connection.send(packet);
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

	public void setInPhasingBlockForm(Player p) {
		test.put(p, true);
		ServerPlayer player = ((CraftPlayer) p).getHandle();
		inPhantomFormBlocks.add(p);
		sendJavaPacket(player);
		p.setCollidable(false);
		p.setAllowFlight(true);
		p.setFlying(true);
	}

	@EventHandler
	public void stEnd(ServerTickEndEvent event) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			((CraftPlayer) player).getHandle().noPhysics = true;
		}
	}

	@EventHandler
	public void warn(PlayerJoinEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (isBedrock(e.getPlayer()) && getPowerArray().contains(e.getPlayer())) {
					e.getPlayer().sendMessage(ChatColor.YELLOW + "Warning! You are using a power(Phasing) that is highly experimental/breakable on bedrock! If you get stuck in a \"spectator like\" mode, type \"./origins-fixMe\" in chat to be fixed.");
				}
			}
		}.runTaskLater(GenesisMC.getPlugin(), 20);
	}

	@EventHandler
	public void chatEvent(PlayerChatEvent e) {
		if (e.getMessage().equalsIgnoreCase("./origins-fixme")) {
			RaycastUtils.executeCommandAtHit(((CraftEntity) e.getPlayer()).getHandle(), CraftLocation.toVec3D(e.getPlayer().getLocation()), "gamemode survival @s");
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void shiftGoDown(PlayerToggleSneakEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (e.isSneaking()) {
					if (getPowerArray().contains(e.getPlayer())) {
						Player p = e.getPlayer();
						for (Layer layer : CraftApoli.getLayersFromRegistry()) {
							ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
							for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
								if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p)) {
									if (ConditionExecutor.testBlock(power.get("phase_down_condition"), (CraftBlock) p.getLocation().add(0, -1, 0).getBlock())) {
										p.teleportAsync(p.getLocation().add(0, -0.1, 0));
									}
								}
							}
						}
					}
				}
			}
		}.runTaskLater(GenesisMC.getPlugin(), 1);
	}

	@EventHandler
	public void je(PlayerJoinEvent e) {
		test.put(e.getPlayer(), false);
	}

	@Override
	public void run(Player p) {
		if (getPowerArray().contains(p)) {
			for (Layer layer : CraftApoli.getLayersFromRegistry()) {
				ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
				for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
					if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p)) {
						setActive(p, power.getTag(), true);
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
							if (!isBedrock(p)) {
								setInPhasingBlockForm(p);
							} else {
								if (p.getGameMode() != GameMode.SPECTATOR) {
									p.setGameMode(GameMode.SPECTATOR);
								}
							}

							p.setFlySpeed(0.03F);
							p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "insideBlock"), PersistentDataType.BOOLEAN, true);

						} else {
							if (!isBedrock(p)) {
								resyncJavaPlayer(((CraftPlayer) p).getHandle());
							} else {
								if (p.getGameMode() == GameMode.SPECTATOR) {
									GameMode gameMode = p.getPreviousGameMode();
									if (gameMode.equals(GameMode.SPECTATOR)) {
										gameMode = GameMode.SURVIVAL;
									}
									p.setGameMode(gameMode);
								}
							}
							p.setFlySpeed(0.1F);
							inPhantomFormBlocks.remove(p);
							p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "insideBlock"), PersistentDataType.BOOLEAN, false);
						}
					} else {
						setActive(p, power.getTag(), false);
						inPhantomFormBlocks.remove(p);
						if (test.get(p) == null) {
							test.put(p, false);
						} else if (test.get(p)) {
							if (!isBedrock(p)) {
								resyncJavaPlayer(((CraftPlayer) p).getHandle());
							} else {
								GameMode gameMode = p.getPreviousGameMode();
								if (gameMode.equals(GameMode.SPECTATOR)) {
									gameMode = GameMode.SURVIVAL;
								}
								p.setGameMode(gameMode);
							}
							p.setFlySpeed(0.1F);
							p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "insideBlock"), PersistentDataType.BOOLEAN, false);
							test.put(p, false);
						}
					}
				}
			}
		}
	}

	public boolean isBedrock(Player p) {
		if (Bukkit.getPluginManager().isPluginEnabled("floodgate")) {
			return GeyserApi.api().connectionByUuid(p.getUniqueId()) != null;
		} else {
			return false;
		}
	}

	@EventHandler
	public void dmgEventRemoveSuff(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player player) {
			if (!inPhantomFormBlocks.contains(player)) return;
			if (e.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION)) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void fixMineSpeed(ServerTickEndEvent e) {
		for (Player p : inPhantomFormBlocks) {
			if (!p.getActivePotionEffects().contains(new PotionEffect(PotionEffectType.FAST_DIGGING, 5, 3, false, false, false))) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 5, 3, false, false, false));
			}
		}
	}


	@Override
	public String getPowerFile() {
		return "apoli:phasing";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return phasing;
	}

	@EventHandler
	public void CancelSpectate(PlayerStartSpectatingEntityEvent e) {
		Player p = e.getPlayer();
		if (phasing.contains(p)) {
			if (OriginPlayerAccessor.isInPhantomForm(p)) {
				e.setCancelled(true);
			}

		}
	}
}