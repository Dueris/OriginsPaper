package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass.modify_break_speed;

public class ModifyBreakSpeedPower extends CraftPower implements Listener {

    String MODIFYING_KEY = "modify_break_speed";
    // TODO: use 1.20.5 attributes instead of effects

    @EventHandler
    public void swing(BlockDamageEvent e) {
        Player p = e.getPlayer();
        if (modify_break_speed.contains(p)) {
            if (p.getGameMode().equals(GameMode.CREATIVE)) return;
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
                try {
                    for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getType(), layer)) {
                        if (e.getBlock() == null || e.getBlock().getState() == null) return;
                        if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p) && ConditionExecutor.testBlock(power.getJsonObject("block_condition"), (CraftBlock) e.getBlock())) {
                            setActive(p, power.getTag(), true);
                            if (p.hasPotionEffect(PotionEffectType.HASTE)) return;
                            // if(power.getPossibleModifiers("modifier", "modifiers"))
                            for (Modifier modifier : power.getModifiers()) {
                                if (modifier.value() <= 0) {
                                    // Slower mine
                                    p.addPotionEffect(
                                        new PotionEffect(
                                            PotionEffectType.HASTE,
                                            120,
                                            (Math.round(valueModifyingSuperClass.getPersistentAttributeContainer(p, MODIFYING_KEY)) + 1) * 17,
                                            false, false, false
                                        )
                                    );
                                } else {
                                    // Speed up
                                    p.addPotionEffect(
                                        new PotionEffect(
                                            PotionEffectType.HASTE,
                                            120,
                                            (Math.round(valueModifyingSuperClass.getPersistentAttributeContainer(p, MODIFYING_KEY)) + 1) * 17,
                                            false, false, false
                                        )
                                    );
                                }
                                p.addScoreboardTag("breaking_genesis_block_at_key_holder");
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        p.getScoreboardTags().remove("breaking_genesis_block_at_key_holder");
                                    }
                                }.runTaskLater(GenesisMC.getPlugin(), 120);
                            }
                        } else {
                            if (p.getScoreboardTags().contains("breaking_genesis_block_at_key_holder")) {
                                p.removePotionEffect(PotionEffectType.HASTE);
                            }
                            setActive(p, power.getTag(), false);
                        }
                    }
                } catch (Exception ev) {
                    ev.printStackTrace();
                }
            }
        }
    }

    public void apply(Player p) {
        ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
        if (modify_break_speed.contains(p)) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getType(), layer)) {
                    for (Modifier modifier : power.getModifiers()) {
                        Float value = modifier.value();
                        valueModifyingSuperClass.saveValueInPDC(p, MODIFYING_KEY, value); // Why does there need to be a binary operator if the operator does nothing?
                    }
                }
            }
        } else {
            valueModifyingSuperClass.saveValueInPDC(p, MODIFYING_KEY, valueModifyingSuperClass.getDefaultValue(MODIFYING_KEY));
        }
    }

    @Override
    public String getType() {
        return "apoli:modify_break_speed";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return modify_break_speed;
    }
}
