package me.dueris.genesismc.factory.powers.apoli.provider.origins;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.apoli.provider.PowerProvider;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.PowerHolderComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffects;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.ArrayList;

public class WaterBreathe implements Listener, PowerProvider {
	private static final ArrayList<Player> genesisExecuting = new ArrayList<>();
	public static ArrayList<Player> outofAIR = new ArrayList<>();
	protected static NamespacedKey powerReference = GenesisMC.originIdentifier("water_breathing");
	private static final String cachedPowerRefrenceString = powerReference.asString();

	public static boolean isInBreathableWater(Player player) {
		Block block = player.getEyeLocation().getBlock();
		Material material = block.getType();
		if (block.getType().equals(Material.WATER)) {
			return true;
		} else return player.isInWater() && !material.equals(Material.AIR);
	}

	public static void start() {
		WaterBreathe waterBreathe = new WaterBreathe();
		Bukkit.getScheduler().runTaskTimer(GenesisMC.getPlugin(), () -> waterBreathe.tick(), 0, 20);
	}

	@EventHandler
	public void interuptMinecraft(EntityAirChangeEvent e) {
		if (e.getEntity() instanceof Player player) {
			if (PowerHolderComponent.hasPower(player, cachedPowerRefrenceString)) {
				if (!genesisExecuting.contains(player)) {
					e.setCancelled(true);
					e.setAmount(0);
				}
			}
		}
	}

	@EventHandler
	public void drinkWater(PlayerItemConsumeEvent e) {
		if (PowerHolderComponent.hasPower(e.getPlayer(), cachedPowerRefrenceString) && e.getItem().getType().equals(Material.POTION)) {
			genesisExecuting.add(e.getPlayer());
			e.getPlayer().setRemainingAir(e.getPlayer().getRemainingAir() + 60);
			genesisExecuting.remove(e.getPlayer());
		}
	}

	@Override
	public void tick(Player p) {
		if (p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR)) return;
		if (PowerHolderComponent.hasPower(p, cachedPowerRefrenceString)) {
			genesisExecuting.add(p);
			int addonAir = 4;
			int lowestAir = -10;
			int tickDownAir = 1;
			boolean shouldDamage = true;
			if (((CraftPlayer) (p)).getHandle().hasEffect(MobEffects.WATER_BREATHING)
				|| p.isInRain()
				|| ((CraftPlayer) p).getHandle().hasEffect(MobEffects.CONDUIT_POWER)
				|| p.getGameMode().equals(GameMode.SPECTATOR)
				|| p.getGameMode().equals(GameMode.CREATIVE)
			) {
				addonAir = 0;
				tickDownAir = 0;
				shouldDamage = false;
			}
			if (isInBreathableWater(p)) {
				if (p.getRemainingAir() < 290) {
					p.setRemainingAir(p.getRemainingAir() + addonAir);
				} else {
					p.setRemainingAir(310);
				}
				outofAIR.remove(p);
			} else {
				int remainingAir = p.getRemainingAir();
				if (remainingAir <= 5) {
					p.setRemainingAir(lowestAir);
					outofAIR.add(p);
				} else {
					p.setRemainingAir(remainingAir - tickDownAir);
					outofAIR.remove(p);
				}
			}
			if (!shouldDamage) {
				outofAIR.remove(p);
			} else if (outofAIR.contains(p)) {
				if (p.getRemainingAir() > 20) {
					outofAIR.remove(p);
				}
			}
			genesisExecuting.remove(p);
		}
	}

	public void tick() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			ServerPlayer player = ((CraftPlayer) p).getHandle();
			if (outofAIR.contains(p)) {
				int remainingAir = p.getRemainingAir();
				if (remainingAir <= 5) {
					for (int i = 0; i < 8; ++i) {
						p.spawnParticle(Particle.BUBBLE, player.getRandomX(0.5), player.getEyeY() + player.random.nextGaussian() * 0.08D, player.getRandomZ(0.5), 1);
					}

					float finalDmg = 1;
					if (p.getInventory().getHelmet() != null) {
						if (p.getInventory().getHelmet().getType() == Material.TURTLE_HELMET) {
							finalDmg = 0.5f;
						} else if (p.getInventory().getHelmet().containsEnchantment(Enchantment.RESPIRATION)) {
							finalDmg = 0.5f;
						}
					}
					DamageType dmgType = Utils.DAMAGE_REGISTRY.get(new ResourceLocation("origins", "no_water_for_gills"));
					((CraftPlayer) p).getHandle().hurt(Utils.getDamageSource(dmgType), finalDmg);
				}
			}
		}
	}
}
