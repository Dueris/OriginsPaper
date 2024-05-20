package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.util.entity.PowerHolderComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.GameEvent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftSound;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.world.GenericGameEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;

public class ElytraFlightPower extends PowerType {
	public static ArrayList<UUID> glidingPlayers = new ArrayList<>();

	public ElytraFlightPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority) {
		super(name, description, hidden, condition, loading_priority);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("elytra_flight"));
	}

	public static ArrayList<UUID> getGlidingPlayers() {
		return glidingPlayers;
	}

	@EventHandler
	public void fixChangeConstantFlight(PowerUpdateEvent e) {
		if (!(e.getPower().getType().equalsIgnoreCase(getType()) && e.isRemoved())) return;
		if (glidingPlayers.contains(e.getPlayer())) {
			glidingPlayers.remove(e.getPlayer());
			e.getPlayer().setGliding(false);
		}
	}

	@EventHandler
	@SuppressWarnings({"unchecked", "Not scheduled yet"})
	public void executeFlight(PlayerToggleFlightEvent e) {
		Player p = e.getPlayer();
		if (p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR)) return;
		if (getPlayers().contains(e.getPlayer())) {
			e.setCancelled(true);
			p.setFlying(false);
			if (isActive(p) && !PowerHolderComponent.hasPowerType(p, PreventElytraFlight.class)) {
				if (!p.isGliding() && !p.getLocation().add(0, 1, 0).getBlock().isCollidable()) {
					if (p.getGameMode() == GameMode.SPECTATOR) return;
					glidingPlayers.add(p.getUniqueId());
					new BukkitRunnable() {
						@Override
						public void run() {
							if (p.isOnGround() || p.isFlying() || p.isInsideVehicle()) {
								this.cancel();
								glidingPlayers.remove(p.getUniqueId());
							}
							float angle = Math.round(p.getPitch() * 10.0F) / 10.0F;
							if (angle <= 38.7) {
								p.setFallDistance(0.0F);
							}
							glidingPlayers.add(p.getUniqueId());
							p.setGliding(true);
						}
					}.runTaskTimer(GenesisMC.getPlugin(), 0L, 1L);
				}
			}
		}
	}

	@EventHandler
	public void onBoost(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR) {
			if (event.getItem().getType().equals(Material.FIREWORK_ROCKET)) {
				if (getPlayers().contains(event.getPlayer()) && glidingPlayers.contains(event.getPlayer().getUniqueId())) {
					event.getPlayer().fireworkBoost(event.getItem());
					if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
						event.getItem().setAmount(event.getItem().getAmount() - 1);
				}
			}
		}
	}

	@EventHandler
	public void recreateFallDamage(GenericGameEvent e) {
		if (e.getEvent().equals(GameEvent.HIT_GROUND)) {
			if (e.getEntity() instanceof Player p) {
				if (p.getGameMode().equals(GameMode.CREATIVE)) return;
				if (getPlayers().contains(p)) {
					float fallDistance = p.getFallDistance();
					ServerPlayer pl = ((CraftPlayer) p).getHandle();
					if (pl.getAbilities().invulnerable) return;
					if (fallDistance >= 2.0F) {
						pl.awardStat(Stats.FALL_ONE_CM, (int) Math.round((double) fallDistance * 100.0D));
					}

					int distance = this.calculateFallDamage(p, fallDistance, 1);

					if (distance > 0) {
						pl.hurt(pl.level().damageSources().fall(), (float) distance);

						p.playSound(p.getLocation(), CraftSound.minecraftToBukkit(distance > 4 ? pl.getFallSounds().big() : pl.getFallSounds().small()), 1f, 1f);
						if (!pl.isSilent()) {
							int a = Mth.floor(pl.getX());
							int b = Mth.floor(pl.getY() - 0.20000000298023274d);
							int c = Mth.floor(pl.getZ());
							BlockState blockData = pl.level().getBlockState(new BlockPos(a, b, c));

							if (!blockData.isAir()) {
								SoundType soundEffectType = blockData.getSoundType();

								pl.playSound(soundEffectType.getFallSound(), soundEffectType.getVolume() * 0.5F, soundEffectType.getPitch() * 0.75F);
							}
						}
					}
				}
			}
		}
	}

	private int calculateFallDamage(Player p, float fallDistance, float damageMultiplier) {
		ServerPlayer pl = ((CraftPlayer) p).getHandle();
		if (!pl.getType().is(EntityTypeTags.FALL_DAMAGE_IMMUNE)) {
			MobEffectInstance mobeffect = pl.getEffect(MobEffects.JUMP);
			float dm = mobeffect == null ? 0.0F : (mobeffect.getAmplifier() + 1);

			return Mth.ceil((fallDistance - 3.0F - dm) * damageMultiplier);
		}
		return 0;
	}
}
