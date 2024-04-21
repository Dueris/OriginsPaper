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

public class SelfGlow extends CraftPower {

    @Override
    public void run(Player p, Power power) {
        for (Entity entity : Bukkit.getServer().getWorld(p.getWorld().getKey()).getEntities()) {
            if (entity instanceof Player player) {
                if (ConditionExecutor.testEntity(power.getJsonObject("entity_condition"), (CraftEntity) p) && ConditionExecutor.testEntity(power.getJsonObject("entity_condition"), (CraftEntity) p) && ConditionExecutor.testBiEntity(power.getJsonObject("bientity_condition"), (CraftEntity) p, (CraftEntity) entity)) {
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

    @Override
    public String getType() {
        return "apoli:self_glow";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return self_glow;
    }
}
