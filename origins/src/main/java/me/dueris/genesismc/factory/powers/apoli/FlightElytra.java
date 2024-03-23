package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
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
import org.bukkit.craftbukkit.v1_20_R3.CraftSound;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.world.GenericGameEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;

public class FlightElytra extends CraftPower implements Listener {
	public static ArrayList<UUID> glidingPlayers = new ArrayList<>();

	public static ArrayList<UUID> getGlidingPlayers() {
		return glidingPlayers;
	}


	@Override
	public void run(Player p) {

	}

	@EventHandler
	public void fixChangeConstantFlight(OriginChangeEvent e) {
		if (glidingPlayers.contains(e.getPlayer())) {
			glidingPlayers.remove(e.getPlayer());
			e.getPlayer().setGliding(false);
		}
	}

	@EventHandler
	@SuppressWarnings({"unchecked", "Not scheduled yet"})
	public void ExecuteFlight(PlayerToggleFlightEvent e) {
		Player p = e.getPlayer();
		if (p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR)) return;
		if (elytra.contains(e.getPlayer())) {
			e.setCancelled(true);
			p.setFlying(false);
			ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
			for (Layer layer : CraftApoli.getLayersFromRegistry()) {
				for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
					if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p)) {
						setActive(p, power.getTag(), true);
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
									glidingPlayers.add(p.getUniqueId());
									p.setGliding(true);
									p.setFallDistance(0);
								}
							}.runTaskTimer(GenesisMC.getPlugin(), 0L, 1L);
						}
					} else {
						setActive(p, power.getTag(), false);
					}
				}
			}
		}
	}

	@EventHandler
	public void onBoost(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR) {
			if (event.getItem().getType().equals(Material.FIREWORK_ROCKET)) {
				if (elytra.contains(event.getPlayer()) && glidingPlayers.contains(event.getPlayer().getUniqueId())) {
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
				if (elytra.contains(p)) {
					float fallDistance = p.getFallDistance();
					ServerPlayer pl = ((CraftPlayer) p).getHandle();
					if (pl.getAbilities().invulnerable) return;
					if (fallDistance >= 2.0F) {
						pl.awardStat(Stats.FALL_ONE_CM, (int) Math.round((double) fallDistance * 100.0D));
					}

					int i = this.calculateFallDamage(p, fallDistance, 1);

					if (i > 0) {
						pl.hurt(pl.level().damageSources().fall(), (float) i);

						p.playSound(p.getLocation(), CraftSound.minecraftToBukkit(this.getFallDamageSound(pl, i)), 1f, 1f);
						this.playBlockFallSound(pl);
					}
				}
			}
		}
	}

	private SoundEvent getFallDamageSound(ServerPlayer pl, int distance) {
		return distance > 4 ? pl.getFallSounds().big() : pl.getFallSounds().small();
	}

	private void playBlockFallSound(ServerPlayer pl) {
		if (!pl.isSilent()) {
			int a = Mth.floor(pl.getX());
			int b = Mth.floor(pl.getY() - 0.20000000298023274d);
			int c = Mth.floor(pl.getZ());
			BlockState blockdata = pl.level().getBlockState(new BlockPos(a, b, c));

			if (!blockdata.isAir()) {
				SoundType soundeffecttype = blockdata.getSoundType();

				pl.playSound(soundeffecttype.getFallSound(), soundeffecttype.getVolume() * 0.5F, soundeffecttype.getPitch() * 0.75F);
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

	@Override
	public String getPowerFile() {
		return "apoli:elytra_flight";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return elytra;
	}
}
