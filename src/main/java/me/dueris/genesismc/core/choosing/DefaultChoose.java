package me.dueris.genesismc.core.choosing;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.events.OriginChangeEvent;
import me.dueris.genesismc.core.events.OriginChooseEvent;
import me.dueris.genesismc.core.items.OrbOfOrigins;
import me.dueris.genesismc.core.utils.SendCharts;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import static me.dueris.genesismc.core.items.OrbOfOrigins.orb;
import static org.bukkit.Bukkit.getServer;
import static org.bukkit.ChatColor.AQUA;

public class DefaultChoose {

    public static void DefaultChoose(Player p) {
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);

        //default choose
        p.closeInventory();
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10, 2);
        p.sendMessage(AQUA + "You have chosen an origin!");
        p.spawnParticle(Particle.CLOUD, p.getLocation(), 100);
        p.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, p.getLocation(), 6);
        p.setCustomNameVisible(false);
        p.getScoreboardTags().add("chosen");
        p.setHealthScaled(false);
        if (p.getScoreboardTags().contains("choosing")) {
            p.removeScoreboardTag("choosing");
        }

        OriginChooseEvent chooseEvent = new OriginChooseEvent(p);
        getServer().getPluginManager().callEvent(chooseEvent);
        OriginChangeEvent Event = new OriginChangeEvent(p);
        getServer().getPluginManager().callEvent(Event);

        if (p.getInventory().getItemInMainHand().isSimilar(OrbOfOrigins.orb) && !p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING).equals("genesis:origin-null")) {
            int amt = p.getInventory().getItemInMainHand().getAmount();
            p.getInventory().getItemInMainHand().setAmount(amt - 1);
        } else {
            if (p.getInventory().getItemInOffHand().isSimilar(orb) && !p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING).equals("genesis:origin-null")) {
                int amt = p.getInventory().getItemInOffHand().getAmount();
                p.getInventory().getItemInOffHand().setAmount(amt - 1);
            }
        }

        SendCharts.originPopularity(p);

    }
}
