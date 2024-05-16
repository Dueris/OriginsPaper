package me.dueris.genesismc.factory.powers.apoli;

import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.util.RaycastUtils;
import me.dueris.genesismc.util.entity.PowerHolderComponent;
import net.minecraft.Optionull;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.geysermc.geyser.api.GeyserApi;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

/**
 * Java Packet Manipulation(JPM) code was made in contribution with Origins-Reborn author, cometcake575
 * <p>
 * Theory:
 * The client side has 2 gamemodes, one for all players and one for specifically themselves.
 * Values required in both the server and client, or required for both the client and other players, are stored
 * within the other players one (the bit affected by the ClientboundPlayerInfoUpdatePacket to allow a survival player to go into blocks),
 * and the one that is required only in the client for knowing stuff like which hotbar to render
 */
public class Phasing extends PowerType {
	public static ArrayList<Player> inPhantomFormBlocks = new ArrayList<>();
	public static HashMap<Player, Boolean> test = new HashMap<>();
	public static AttributeModifier speedFix = new AttributeModifier("PhantomSpeedFix", 1, AttributeModifier.Operation.ADD_NUMBER);
	private final FactoryJsonObject phaseDownCondition;

	public Phasing(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject phaseDownCondition) {
		super(name, description, hidden, condition, loading_priority);
		this.phaseDownCondition = phaseDownCondition;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("phasing"))
			.add("phase_down_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

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

	public static float getGameModeFloat(GameMode gameMode) {
		return switch (gameMode) {
			case CREATIVE -> 1.0f;
			case SURVIVAL -> 0.0f;
			case ADVENTURE -> 2.0f;
			case SPECTATOR -> 3.0f;
		};
	}

	public static boolean isBedrock(Player p) {
		if (Bukkit.getPluginManager().isPluginEnabled("floodgate")) {
			return GeyserApi.api().connectionByUuid(p.getUniqueId()) != null;
		} else {
			return false;
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
				if (isBedrock(e.getPlayer()) && getPlayers().contains(e.getPlayer())) {
					e.getPlayer().sendMessage(ChatColor.YELLOW + "Warning! You are using a power(Phasing) that is highly experimental/breakable on bedrock! If you get stuck in a \"spectator like\" mode, type \"./origins-fixMe\" in chat to be fixed.");
				}
			}
		}.runTaskLater(GenesisMC.getPlugin(), 20);
	}

	@EventHandler
	public void chatEvent(PlayerChatEvent e) {
		if (e.getMessage().equalsIgnoreCase("./origins-fixme")) {
			RaycastUtils.executeNMSCommand(((CraftEntity) e.getPlayer()).getHandle(), CraftLocation.toVec3D(e.getPlayer().getLocation()), "gamemode survival @s");
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void shiftGoDown(PlayerToggleSneakEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (e.isSneaking()) {
					if (getPlayers().contains(e.getPlayer())) {
						Player p = e.getPlayer();
						if (isActive(p)) {
							if (ConditionExecutor.testBlock(phaseDownCondition, (CraftBlock) p.getLocation().add(0, -1, 0).getBlock())) {
								p.teleportAsync(p.getLocation().add(0, -0.1, 0));
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
	public void tick(Player p) {
		if (isActive(p)) {
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
		for (Player p : getPlayers()) {
			if (inPhantomFormBlocks.contains(p)) {
				if (!p.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).getModifiers().contains(speedFix)) {
					p.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).addModifier(speedFix);
				}
			} else {
				if (p.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).getModifiers().contains(speedFix)) {
					p.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).removeModifier(speedFix);
				}
			}
		}
	}

	@EventHandler
	public void CancelSpectate(PlayerStartSpectatingEntityEvent e) {
		Player p = e.getPlayer();
		if (getPlayers().contains(p)) {
			if (PowerHolderComponent.isInPhantomForm(p)) {
				e.setCancelled(true);
			}

		}
	}

	public FactoryJsonObject getPhaseDownCondition() {
		return phaseDownCondition;
	}
}