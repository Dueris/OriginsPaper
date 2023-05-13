package me.dueris.genesismc.core.origins.enderian;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import io.papermc.paper.event.entity.WaterBottleSplashEvent;
import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.*;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import static me.dueris.genesismc.core.factory.powers.Powers.projectile_immune;
import static me.dueris.genesismc.core.factory.powers.Powers.water_vulnerability;


public class EnderMain implements Listener {
    private final HashMap<UUID, Long> cooldown;
    public EnderMain() {
        this.cooldown = new HashMap<>();
    }

    @EventHandler
    public void OnMovement(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (origintag.equalsIgnoreCase("genesis:origin-enderian")) {

            Random random = new Random();
            int r = random.nextInt(3000);
            if (r == 3 || r == 9 || r == 11 || r == 998 || r == 2279 || r == 989) {
                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_AMBIENT, 10, 9);
            }
        }
    }
}

