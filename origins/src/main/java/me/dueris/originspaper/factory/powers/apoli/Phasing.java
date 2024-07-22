package me.dueris.originspaper.factory.powers.apoli;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.util.Util;
import me.dueris.originspaper.util.console.OriginConsoleSender;
import net.minecraft.Optionull;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
	public static HashMap<Player, Boolean> resynched = new HashMap<>();
	public static HashMap<Player, HashMap<BlockPos, Integer>> sentBlockLocations = new HashMap<>();
	public static AttributeModifier speedFixAttribute = new AttributeModifier("PhantomSpeedFix", 1, AttributeModifier.Operation.ADD_NUMBER);
	private final FactoryJsonObject phaseDownCondition;
	private final String renderType;
	private final boolean blacklist;
	private final FactoryJsonObject blockCondition;

	public Phasing(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject phaseDownCondition, String renderType, boolean blacklist, FactoryJsonObject blockCondition) {
		super(name, description, hidden, condition, loading_priority);
		this.phaseDownCondition = phaseDownCondition;
		this.renderType = renderType;
		this.blacklist = blacklist;
		this.blockCondition = blockCondition;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("phasing"))
			.add("phase_down_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("render_type", String.class, "blindness")
			.add("blacklist", boolean.class, false)
			.add("block_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	protected static void resyncJavaPlayer(@NotNull ServerPlayer player) {
		if (player.gameMode.getGameModeForPlayer().equals(GameType.SPECTATOR)) {
			player.gameMode.changeGameModeForPlayer(GameType.SURVIVAL);
		}
		GameType gamemode = player.gameMode.getGameModeForPlayer();
		ClientboundPlayerInfoUpdatePacket.Entry entry = new ClientboundPlayerInfoUpdatePacket.Entry(player.getUUID(), player.getGameProfile(), true, 1, gamemode, player.getTabListDisplayName(), Optionull.map(player.getChatSession(), RemoteChatSession::asData));
		ClientboundPlayerInfoUpdatePacket packet = new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE), entry);
		player.connection.send(packet);
	}

	public static boolean isBedrock(Player p) {
		if (Bukkit.getPluginManager().isPluginEnabled("floodgate")) {
			return FloodgateApi.getInstance().isFloodgateId(p.getUniqueId());
		} else {
			return false;
		}
	}

	public void sendPhasingPackets(Player p) {
		resynched.put(p, true);
		sentBlockLocations.putIfAbsent(p, new HashMap<>());
		ServerPlayer player = ((CraftPlayer) p).getHandle();
		inPhantomFormBlocks.add(p);
		GameType gamemode = GameType.SPECTATOR;
		ClientboundPlayerInfoUpdatePacket.Entry entry = new ClientboundPlayerInfoUpdatePacket.Entry(
			player.getUUID(),
			player.getGameProfile(),
			true,
			1,
			gamemode,
			player.getTabListDisplayName(),
			Optionull.map(player.getChatSession(), RemoteChatSession::asData)
		);
		ClientboundPlayerInfoUpdatePacket packet = new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE), entry);
		/* Shape.executeAtPositions(CraftLocation.toBlockPosition(p.getLocation()), Shape.SPHERE, 7, (blockPos) -> {
			try {
				Location location = CraftLocation.toBukkit(blockPos);
				location.setWorld(p.getWorld());
				BlockState state = p.getWorld().getBlockState(location);
				if (sentBlockLocations.get(p).containsKey(blockPos) || !state.isCollidable()
					|| ((blacklist && !ConditionExecutor.testBlock(blockCondition, state.getBlock())) || (!blacklist && ConditionExecutor.testBlock(blockCondition, state.getBlock())))
				) return;

				ServerLevel level = player.serverLevel();
				Display.BlockDisplay blockDisplay = (Display.BlockDisplay) Util.getEntityWithPassengers(
					level,
					EntityType.BLOCK_DISPLAY,
					Util.ParserUtils.parseJson(new StringReader(
						"{id:\"minecraft:block_display\",NoGravity:1b,Silent:1b,HasVisualFire:0b,Glowing:0b,CustomNameVisible:0b,block_state:{Name:\"$A$\"}}"
						.replace("$A$", level.getBlockState(blockPos).getBukkitMaterial().getKey().asString())), CompoundTag.CODEC),
					CraftLocation.toVec3D(CraftLocation.toBukkit(blockPos)),
					Optional.empty(),
					Optional.empty()
				).orElseThrow();
				level.addFreshEntity(blockDisplay);
				ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(blockDisplay);
				sentBlockLocations.get(p).put(blockPos, blockDisplay.getId());
				player.connection.send(addEntityPacket);
			} catch (Throwable throwable) {
				GenesisMC.getPlugin().getLogger().severe("An unhandled exception occurred when sending visual updates!");
				GenesisMC.getPlugin().throwable(throwable, false);
			}
		}); */
		player.connection.send(packet);
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
		}.runTaskLater(OriginsPaper.getPlugin(), 20);
	}

	@EventHandler
	public void chatEvent(@NotNull PlayerChatEvent e) {
		if (e.getMessage().equalsIgnoreCase("./origins-fixme")) {
			OriginConsoleSender.NMSSender.executeNMSCommand(((CraftEntity) e.getPlayer()).getHandle(), CraftLocation.toVec3D(e.getPlayer().getLocation()), "gamemode survival @s");
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
						if (isActive(p) && !p.getLocation().getBlock().isCollidable()) {
							if (ConditionExecutor.testBlock(phaseDownCondition, (CraftBlock) p.getLocation().add(0, -1, 0).getBlock())) {
								p.teleportAsync(p.getLocation().add(0, -0.1, 0));
							}
						}
					}
				}
			}
		}.runTaskLater(OriginsPaper.getPlugin(), 1);
	}

	@EventHandler
	public void je(@NotNull PlayerJoinEvent e) {
		resynched.put(e.getPlayer(), false);
	}

	private @NotNull Set<Block> getBlocksPlayerIsTouching(Player player) {
		Set<Block> touchingBlocks = new HashSet<>();

		Vector[] offsets = new Vector[]{
			new Vector(0.55, 0, 0.55),
			new Vector(0.55, 0, 0),
			new Vector(0, 0, 0.55),
			new Vector(-0.55, 0, -0.55),
			new Vector(0, 0, -0.55),
			new Vector(-0.55, 0, 0),
			new Vector(0.55, 0, -0.55),
			new Vector(-0.55, 0, 0.55),
			new Vector(0, 0.5, 0),

			new Vector(0.55, 0, 0.55),
			new Vector(0.55, 0, 0),
			new Vector(0, 0, 0.55),
			new Vector(-0.55, 0, -0.55),
			new Vector(0, 0, -0.55),
			new Vector(-0.55, 0, 0),
			new Vector(0.55, 0, -0.55),
			new Vector(-0.55, 0, 0.55)
		};

		for (Vector offset : offsets) {
			Block blockAtFeet = player.getLocation().add(offset).getBlock();
			Block blockAtHead = player.getEyeLocation().add(offset).getBlock();

			if (blockAtFeet.isCollidable() && ((blacklist && !ConditionExecutor.testBlock(blockCondition, blockAtFeet)) || (!blacklist && ConditionExecutor.testBlock(blockCondition, blockAtFeet)))) {
				touchingBlocks.add(blockAtFeet);
			}

			if (blockAtHead.isCollidable() && ((blacklist && !ConditionExecutor.testBlock(blockCondition, blockAtHead)) || (!blacklist && ConditionExecutor.testBlock(blockCondition, blockAtHead)))) {
				touchingBlocks.add(blockAtHead);
			}
		}

		return touchingBlocks;
	}

	@Override
	public void tick(Player p) {
		if (isActive(p)) {
			Set<Block> blocks = getBlocksPlayerIsTouching(p);
			if (!blocks.isEmpty()) {
				if (!isBedrock(p)) {
					sendPhasingPackets(p);
				} else {
					if (p.getGameMode() != GameMode.SPECTATOR) {
						p.setGameMode(GameMode.SPECTATOR);
					}
				}

				if (renderType.equalsIgnoreCase("blindness")) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100000, 112, false, false, false));
				}

				p.setFlySpeed(0.03F);
				p.getPersistentDataContainer().set(new NamespacedKey(OriginsPaper.getPlugin(), "insideblock"), PersistentDataType.BOOLEAN, true);

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
				if (renderType.equalsIgnoreCase("blindness")) {
					p.removePotionEffect(PotionEffectType.BLINDNESS);
				}
				if (sentBlockLocations.get(p) != null) {
					ClientboundRemoveEntitiesPacket removeEntitiesPacket = new ClientboundRemoveEntitiesPacket(Util.convertToIntArray(sentBlockLocations.get(p).values()));
					((CraftPlayer) p).getHandle().connection.send(removeEntitiesPacket);
					sentBlockLocations.clear();
				}
				p.setFlySpeed(0.1F);
				inPhantomFormBlocks.remove(p);
				p.getPersistentDataContainer().set(new NamespacedKey(OriginsPaper.getPlugin(), "insideblock"), PersistentDataType.BOOLEAN, false);
			}
		} else {
			inPhantomFormBlocks.remove(p);
			if (resynched.get(p) == null) {
				resynched.put(p, false);
			} else if (resynched.get(p)) {
				if (!isBedrock(p)) {
					resyncJavaPlayer(((CraftPlayer) p).getHandle());
				} else {
					GameMode gameMode = p.getPreviousGameMode();
					if (gameMode.equals(GameMode.SPECTATOR)) {
						gameMode = GameMode.SURVIVAL;
					}
					p.setGameMode(gameMode);
				}
				if (renderType.equalsIgnoreCase("blindness")) {
					p.removePotionEffect(PotionEffectType.BLINDNESS);
				}
				if (sentBlockLocations.get(p) != null) {
					ClientboundRemoveEntitiesPacket removeEntitiesPacket = new ClientboundRemoveEntitiesPacket(Util.convertToIntArray(sentBlockLocations.get(p).values()));
					((CraftPlayer) p).getHandle().connection.send(removeEntitiesPacket);
					sentBlockLocations.clear();
				}
				p.setFlySpeed(0.1F);
				p.getPersistentDataContainer().set(new NamespacedKey(OriginsPaper.getPlugin(), "insideblock"), PersistentDataType.BOOLEAN, false);
				resynched.put(p, false);
			}
		}
	}

	@EventHandler
	public void dmgEventRemoveSuff(@NotNull EntityDamageEvent e) {
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
				if (!p.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).getModifiers().contains(speedFixAttribute)) {
					p.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).addModifier(speedFixAttribute);
				}
			} else {
				if (p.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).getModifiers().contains(speedFixAttribute)) {
					p.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).removeModifier(speedFixAttribute);
				}
			}
		}
	}

	@EventHandler
	public void moveEvent(@NotNull PlayerMoveEvent e) {
		if (getPlayers().contains(e.getPlayer()) && inPhantomFormBlocks.contains(e.getPlayer())) {
			if (e.getTo().getBlock().isCollidable() &&
				!((blacklist && !ConditionExecutor.testBlock(blockCondition, e.getTo().getBlock())) || (!blacklist && ConditionExecutor.testBlock(blockCondition, e.getTo().getBlock())))) {
				e.setCancelled(true);
				Location toSet = e.getFrom();
				toSet.setDirection(e.getTo().getDirection());
				e.setTo(toSet);
			}
		}
	}

	public FactoryJsonObject getPhaseDownCondition() {
		return phaseDownCondition;
	}
}
