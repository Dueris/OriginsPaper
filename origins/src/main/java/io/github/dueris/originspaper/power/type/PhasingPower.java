package io.github.dueris.originspaper.power.type;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.util.Util;
import io.papermc.paper.event.player.PlayerFailMoveEvent;
import net.minecraft.Optionull;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

public class PhasingPower extends PowerType {
	private static final NamespacedKey INSIDE_BLOCK_KEY = new NamespacedKey("originspaper", "insideblock");
	public static LinkedList<Player> PHASING_BLOCKS = new LinkedList<>();
	public static HashMap<Player, Boolean> RESYNCED = new HashMap<>();
	public static HashMap<Player, HashMap<BlockPos, Integer>> TRACKED_BLOCKPOS = new HashMap<>();
	public static AttributeModifier PHASING_SPEED_FIX = new AttributeModifier(NamespacedKey.fromString("origins:phasing_patch"), 1, AttributeModifier.Operation.ADD_NUMBER);
	static Vector[] offsets = new Vector[]{
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

	private final Predicate<BlockInWorld> blockCondition;
	private final boolean blacklist;
	private final RenderType renderType;
	private final Predicate<Entity> phaseDownCondition;

	public PhasingPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
						ConditionTypeFactory<BlockInWorld> blockCondition, boolean blacklist, RenderType renderType, ConditionTypeFactory<Entity> phaseDownCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.blockCondition = blockCondition;
		this.blacklist = blacklist;
		this.renderType = renderType;
		this.phaseDownCondition = phaseDownCondition;
	}

	public static SerializableData getFactory() {
		return PowerType.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("phasing"))
			.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
			.add("blacklist", SerializableDataTypes.BOOLEAN, false)
			.add("render_type", SerializableDataTypes.enumValue(RenderType.class), RenderType.BLINDNESS)
			.add("phase_down_condition", ApoliDataTypes.ENTITY_CONDITION, null);
	}

	protected static void resyncJavaPlayer(@NotNull ServerPlayer player) {
		GameType gamemode = player.gameMode.getGameModeForPlayer();
		ClientboundPlayerInfoUpdatePacket.Entry entry = new ClientboundPlayerInfoUpdatePacket.Entry(player.getUUID(), player.getGameProfile(), true, 1, gamemode, player.getTabListDisplayName(), Optionull.map(player.getChatSession(), RemoteChatSession::asData));
		ClientboundPlayerInfoUpdatePacket packet = new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE), entry);
		player.connection.send(packet);
	}

	public void sendPhasingPackets(ServerPlayer player) {
		RESYNCED.put(player, true);
		TRACKED_BLOCKPOS.putIfAbsent(player, new HashMap<>());
		PHASING_BLOCKS.add(player);
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
		player.connection.send(packet);
		CraftPlayer p = player.getBukkitEntity();
		p.setCollidable(false);
		p.setAllowFlight(true);
		p.setFlying(true);
	}

	@EventHandler
	public void onShift(PlayerToggleSneakEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (e.isSneaking()) {
					org.bukkit.entity.Player p = e.getPlayer();
					Player player = ((CraftPlayer) p).getHandle();
					if (getPlayers().contains(player) && isActive(player) && (p.getLocation().add(0, -0.5, 0).getBlock().isCollidable() || p.getLocation().add(0, -0.5, 0).getBlock().isSolid())) {
						if (phaseDownCondition.test(player)) {
							p.teleportAsync(p.getLocation().add(0, -0.1, 0));
						}
					}
				}
			}
		}.runTaskLater(OriginsPaper.getPlugin(), 2);
	}

	@EventHandler
	public void onJoin(@NotNull PlayerJoinEvent e) {
		RESYNCED.put(((CraftPlayer) e.getPlayer()).getHandle(), false);
	}

	private @NotNull Set<Block> getBlocksInCollision(org.bukkit.entity.Player player) {
		Set<Block> touchingBlocks = new HashSet<>();

		for (Vector offset : offsets) {
			Block blockAtFeet = player.getLocation().add(offset).getBlock();
			Block blockAtHead = player.getEyeLocation().add(offset).getBlock();
			BlockInWorld feet = new BlockInWorld(((CraftPlayer) player).getHandle().level(), ((CraftBlock) blockAtFeet).getPosition(), false);
			BlockInWorld head = new BlockInWorld(((CraftPlayer) player).getHandle().level(), ((CraftBlock) blockAtHead).getPosition(), false);
			boolean testFeet = blockCondition.test(feet);
			boolean testHead = blockCondition.test(head);

			if ((blockAtFeet.isCollidable() && blockAtFeet.isSolid()) && ((blacklist && !testFeet) || (!blacklist && testFeet))) {
				touchingBlocks.add(blockAtFeet);
			}

			if ((blockAtHead.isCollidable() && blockAtHead.isSolid()) && ((blacklist && !testHead) || (!blacklist && testHead))) {
				touchingBlocks.add(blockAtHead);
			}
		}

		return touchingBlocks;
	}

	@Override
	public void tick(@NotNull Player player) {
		CraftPlayer p = (CraftPlayer) player.getBukkitEntity();
		if (isActive(player)) {
			Set<Block> blocks = getBlocksInCollision(p);
			if (!blocks.isEmpty()) {
				sendPhasingPackets((ServerPlayer) player);

				if (renderType.equals(RenderType.BLINDNESS)) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100000, 112, false, false, false));
				}

				p.setFlySpeed(0.03F);
				p.getPersistentDataContainer().set(INSIDE_BLOCK_KEY, PersistentDataType.BOOLEAN, true);
			} else {
				resyncJavaPlayer((ServerPlayer) player);
				if (renderType.equals(RenderType.BLINDNESS)) {
					p.removePotionEffect(PotionEffectType.BLINDNESS);
				}
				if (TRACKED_BLOCKPOS.get(player) != null) {
					ClientboundRemoveEntitiesPacket removeEntitiesPacket = new ClientboundRemoveEntitiesPacket(Util.convertToIntArray(TRACKED_BLOCKPOS.get(player).values()));
					((ServerPlayer) player).connection.send(removeEntitiesPacket);
					TRACKED_BLOCKPOS.clear();
				}
				p.setFlySpeed(0.1F);
				PHASING_BLOCKS.remove(player);
				p.getPersistentDataContainer().set(INSIDE_BLOCK_KEY, PersistentDataType.BOOLEAN, false);
			}
		} else if (getPlayers().contains(player)) {
			callUndo(player);
		}
	}

	@Override
	public void onRemoved(Player player) {
		callUndo(player);
	}

	public void callUndo(@NotNull Player player) {
		CraftPlayer p = (CraftPlayer) player.getBukkitEntity();

		PHASING_BLOCKS.remove(player);
		if (RESYNCED.get(player) == null) {
			RESYNCED.put(player, false);
		} else if (RESYNCED.get(player)) {
			resyncJavaPlayer((ServerPlayer) player);
			if (renderType.equals(RenderType.BLINDNESS)) {
				p.removePotionEffect(PotionEffectType.BLINDNESS);
			}
			if (TRACKED_BLOCKPOS.get(player) != null) {
				ClientboundRemoveEntitiesPacket removeEntitiesPacket = new ClientboundRemoveEntitiesPacket(Util.convertToIntArray(TRACKED_BLOCKPOS.get(player).values()));
				((ServerPlayer) player).connection.send(removeEntitiesPacket);
				TRACKED_BLOCKPOS.clear();
			}
			p.setFlySpeed(0.1F);
			p.getPersistentDataContainer().set(INSIDE_BLOCK_KEY, PersistentDataType.BOOLEAN, false);
			RESYNCED.put(player, false);
		}
	}

	@EventHandler
	public void noSuffocation(@NotNull EntityDamageEvent e) {
		if (e.getEntity() instanceof org.bukkit.entity.Player player) {
			if (!PHASING_BLOCKS.contains(((CraftPlayer) player).getHandle())) return;
			if (e.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION)) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void fixSpeedInPhasing(ServerTickEndEvent e) {
		for (CraftHumanEntity p : getPlayers().stream().map(Player::getBukkitEntity).toList()) {
			if (p.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED) == null) continue;
			if (PHASING_BLOCKS.contains(p.getHandle())) {
				if (!p.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).getModifiers().contains(PHASING_SPEED_FIX)) {
					p.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).addTransientModifier(PHASING_SPEED_FIX);
				}
			} else {
				if (p.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).getModifiers().contains(PHASING_SPEED_FIX)) {
					p.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).removeModifier(PHASING_SPEED_FIX);
				}
			}
		}
	}

	@EventHandler
	public void onMove(@NotNull PlayerMoveEvent e) {
		Player player = ((CraftPlayer) e.getPlayer()).getHandle();
		if (getPlayers().contains(player) && PHASING_BLOCKS.contains(player)) {
			boolean conditionPassed = blockCondition.test(new BlockInWorld(player.level(), ((CraftBlock) e.getTo().getBlock()).getPosition(), false));
			if (e.getTo().getBlock().isCollidable() &&
				!((blacklist && !conditionPassed)) || (!blacklist && conditionPassed)) {
				e.setCancelled(true);
				Location toSet = e.getFrom();
				toSet.setDirection(e.getTo().getDirection());
				e.setTo(toSet);
			}
		}
	}

	public enum RenderType {
		BLINDNESS, NONE
	}
}
