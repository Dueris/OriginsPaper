package me.dueris.genesismc.factory.powers.player;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R2.potion.CraftPotionEffectType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.awt.*;
import java.util.ArrayList;

public class SelfGlow extends CraftPower {

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    Player p;

    public SelfGlow() {
        this.p = p;
    }

    @Override
    public void run(Player p) {
        if (!getPowerArray().contains(p)) return;
        for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
            for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (Entity entity : Bukkit.getServer().getWorld(p.getWorld().getKey()).getEntities()) {
                    if (entity instanceof Player player) {
                        if (conditionExecutor.check("entity_condition", "entity_conditions", p, power, getPowerFile(), p, entity, null, null, p.getItemInHand(), null)) {
                            if (conditionExecutor.check("bientity_condition", "bientity_conditions", p, power, getPowerFile(), p, entity, null, null, p.getItemInHand(), null)) {
                                setActive(power.getTag(), true);
                                CraftPlayer craftPlayers = (CraftPlayer) player;
                                    craftPlayers.getHandle().connection.send(new ClientboundUpdateMobEffectPacket(p.getEntityId(),
                                            new MobEffectInstance(CraftPotionEffectType.bukkitToMinecraft(PotionEffectType.GLOWING), 5, 1, false, false, false)));
                            } else {
                                setActive(power.getTag(), false);
                            }
                        } else {
                            setActive(power.getTag(), false);
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
        return "origins:self_glow";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return self_glow;
    }
}
