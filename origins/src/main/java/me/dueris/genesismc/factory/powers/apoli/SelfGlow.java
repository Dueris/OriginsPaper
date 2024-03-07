package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.world.effect.MobEffectInstance;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.potion.CraftPotionEffectType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;

public class SelfGlow extends CraftPower {

	@Override
	public void setActive(Player p, String tag, Boolean bool) {
		if (powers_active.containsKey(p)) {
			if (powers_active.get(p).containsKey(tag)) {
				powers_active.get(p).replace(tag, bool);
			} else {
				powers_active.get(p).put(tag, bool);
			}
		} else {
			powers_active.put(p, new HashMap());
			setActive(p, tag, bool);
		}
	}

	@Override
	public void run(Player p) {
		if (!getPowerArray().contains(p)) return;
		for (Layer layer : CraftApoli.getLayersFromRegistry()) {
			for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
				ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
				for (Entity entity : Bukkit.getServer().getWorld(p.getWorld().getKey()).getEntities()) {
					if (entity instanceof Player player) {
						if (ConditionExecutor.testEntity(power.get("entity_condition"), (CraftEntity) p) && ConditionExecutor.testEntity(power.get("entity_condition"), (CraftEntity) p) && ConditionExecutor.testBiEntity(power.get("bientity_condition"), (CraftEntity) p, (CraftEntity) entity)) {
							setActive(p, power.getTag(), true);
							CraftPlayer craftPlayers = (CraftPlayer) player;
							craftPlayers.getHandle().connection.send(new ClientboundUpdateMobEffectPacket(p.getEntityId(),
								new MobEffectInstance(CraftPotionEffectType.bukkitToMinecraft(PotionEffectType.GLOWING), 5, 1, false, false, false)));
						} else {
							setActive(p, power.getTag(), false);
						}
					}
				}
				CraftPlayer craftPlayer = (CraftPlayer) p;
				craftPlayer.getHandle().connection.send(new ClientboundUpdateMobEffectPacket(craftPlayer.getEntityId(),
					new MobEffectInstance(CraftPotionEffectType.bukkitToMinecraft(PotionEffectType.GLOWING), 5, 1, false, false, false)));
			}
		}
	}

	@Override
	public String getPowerFile() {
		return "apoli:self_glow";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return self_glow;
	}
}
