package me.dueris.genesismc.core.bukkitrunnables;

import me.dueris.genesismc.core.GenesisMC;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;

import static org.bukkit.ChatColor.GRAY;

public class PhantomRunnable extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard scoreboard = manager.getNewScoreboard();
            PersistentDataContainer data = p.getPersistentDataContainer();
            int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
            if (originid == 7300041) {
                if(!p.getWorld().isDayTime()){
                    p.setInvisible(false);
                }else{
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20, 0, false, false, false));
                    Team team = scoreboard.getTeam("origin-players");
                    if(!p.getScoreboard().equals(team) && team != null){
                        team.addPlayer(p);
                    }else{
                        scoreboard.registerNewTeam("origin-players");
                    }
                }
                ItemStack spectatorswitch = new ItemStack(Material.FEATHER);
                ItemMeta switch_meta = spectatorswitch.getItemMeta();
                switch_meta.setDisplayName(GRAY + "Phantom Form");
                ArrayList<String> feather_lore = new ArrayList();
                switch_meta.setUnbreakable(true);
                switch_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                switch_meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
                switch_meta.setLore(feather_lore);
                switch_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                spectatorswitch.setItemMeta(switch_meta);
                int phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER);
                        if(phantomid == 1){
                            if(p.getInventory().getItemInMainHand().isSimilar(spectatorswitch)){
                                //deactivate
                            }
                            }else{
                            //activate
                            p.setFlySpeed(0.06F);

                            CraftPlayer craftPlayer = (CraftPlayer) p;
                            ServerPlayer serverPlayer = craftPlayer.getHandle();
                            //when walking into a block, put player in spectator and make it so they cant go up and down. rewrite movement engine
                            //upon walk into block, add signal plugin can detect
                            //just loaded origins bukkit and it doesnt have phantom origin??!?!?



                        }
                Team team = scoreboard.getTeam("origin-players");
                if(!p.getScoreboard().equals(team) && team != null){
                    team.addPlayer(p);
                }

                if(p.getInventory().getItemInMainHand().isSimilar(spectatorswitch)){
                    p.getInventory().getItemInMainHand().setAmount(1);
                }else if(p.getInventory().getItemInOffHand().isSimilar(spectatorswitch)){
                    p.getInventory().getItemInOffHand().setAmount(1);
                }

            }

        }
    }
}
