package me.dueris.genesismc.factory.powers.OriginsMod.player;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeWrapper;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

public class SelfGlow extends CraftPower {

    @Override
    public void setActive(String tag, Boolean bool){
        if(powers_active.containsKey(tag)){
            powers_active.replace(tag, bool);
        }else{
            powers_active.put(tag, bool);
        }
    }

    

    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()){
            for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                for(Entity entity : Bukkit.getServer().getWorld(p.getWorld().getKey()).getEntities()){
                    if(entity instanceof Player player){
                        if(conditionExecutor.check("entity_condition", "entity_conditions", p, origin, getPowerFile(), null, entity)){
                            if(conditionExecutor.check("bientity_condition", "bientity_conditions", p, origin, getPowerFile(), null, entity)){
                                setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                                CraftPlayer craftPlayers = (CraftPlayer) player;
                                craftPlayers.getHandle().connection.send(new ClientboundUpdateMobEffectPacket(p.getEntityId(),
                                        new MobEffectInstance(MobEffect.byId(24), 5, 1, false, false, false)));
                            }else{
                                setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                            }
                        }else{
                            setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                        }
                    }
                }
                CraftPlayer craftPlayer = (CraftPlayer) p;
                craftPlayer.getHandle().connection.send(new ClientboundUpdateMobEffectPacket(craftPlayer.getEntityId(),
                        new MobEffectInstance(MobEffect.byId(24), 5, 1, false, false, false)));
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
