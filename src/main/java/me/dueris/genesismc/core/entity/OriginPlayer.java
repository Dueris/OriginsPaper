package me.dueris.genesismc.core.entity;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.enums.OriginDataType;
import me.dueris.genesismc.core.enums.OriginMenu;
import me.dueris.genesismc.core.events.OriginChooseEvent;
import me.dueris.genesismc.core.factory.CraftApoliRewriten;
import me.dueris.genesismc.core.utils.OriginContainer;
import me.dueris.genesismc.core.utils.PowerContainer;
import me.dueris.genesismc.core.utils.SendCharts;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static me.dueris.genesismc.core.choosing.ChoosingCORE.*;
import static me.dueris.genesismc.core.choosing.contents.ChooseMenuContents.ChooseMenuContent;
import static me.dueris.genesismc.core.choosing.contents.MainMenuContents.GenesisMainMenuContents;
import static me.dueris.genesismc.core.factory.powers.Powers.*;
import static org.bukkit.Bukkit.getServer;
import static org.bukkit.ChatColor.GRAY;

public class OriginPlayer {

    public static boolean hasChosenOrigin(Player player) {
        return true; //!OriginPlayer.getOriginTag(player).equalsIgnoreCase("");
    }

    public static void removeArmor(Player player, EquipmentSlot slot) {
        ItemStack armor = player.getInventory().getItem(slot);

        if (armor != null && armor.getType() != Material.AIR) {
            // Remove the armor from the player's equipped slot
            player.getInventory().setItem(slot, new ItemStack(Material.AIR));

            // Add the armor to the player's inventory
            HashMap<Integer, ItemStack> excess = player.getInventory().addItem(armor);

            // If there is excess armor that couldn't fit in the inventory, drop it
            for (ItemStack item : excess.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            }
        }
    }

    public static void moveEquipmentInventory(Player player, EquipmentSlot equipmentSlot) {
        ItemStack item = player.getInventory().getItem(equipmentSlot);

        if (item != null && item.getType() != Material.AIR) {
            // Find an empty slot in the player's inventory
            int emptySlot = player.getInventory().firstEmpty();

            if (emptySlot != -1) {
                // Set the equipment slot to empty
                player.getInventory().setItem(equipmentSlot, null);

                // Move the item to the empty slot
                player.getInventory().setItem(emptySlot, item);
            }
        }
    }

    public static void launchElytra(Player player) {
        Location location = player.getEyeLocation();
        double speed = 2.0;
        @NotNull Vector direction = location.getDirection().normalize();
        Vector velocity = direction.multiply(speed);
        player.setVelocity(velocity);
    }

    public static boolean hasOrigin(Player player, String origintag) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        @Nullable String origin = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (origin.equalsIgnoreCase("")) return false;
        return origin.contains(origintag);
    }

    public static OriginContainer getOrigin(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
//        if (data.get(new NamespacedKey(GenesisMC.getPlugin(), "origin"), PersistentDataType.BYTE_ARRAY) == null)
//            return new CraftApoliRewriten().nullOrigin();
        return CraftApoliRewriten.toOriginContainer(data.get(new NamespacedKey(GenesisMC.getPlugin(), "origin"), PersistentDataType.BYTE_ARRAY));
    }

    public static void removeOrigin(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        data.set(new NamespacedKey(GenesisMC.getPlugin(), "origin"), PersistentDataType.BYTE_ARRAY, CraftApoliRewriten.toByteArray(CraftApoliRewriten.nullOrigin()));
    }

    public static boolean hasCoreOrigin(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        @Nullable String origintagPlayer = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (origintagPlayer.contains("genesis:origin-human")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-enderian")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-merling")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-phantom")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-elytrian")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-blazeborn")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-avian")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-arachnid")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-shulk")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-feline")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-starborne")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-allay")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-rabbit")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-bee")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-sculkling")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-creep")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-slimeling")) {
            return true;
        } else return origintagPlayer.contains("genesis:origin-piglin");
    }

    public static void setOrigin(Player player, OriginContainer origin) {
        unassignPowers(player);
        player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origin"), PersistentDataType.BYTE_ARRAY, CraftApoliRewriten.toByteArray(origin));
        String originTag = origin.getTag();
        if (originTag.contains("genesis:origin-human")) {
            setAttributesToDefault(player);
            removeItemPhantom(player);
            removeItemEnder(player);
        } else if (originTag.contains("genesis:origin-enderian")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                ItemStack infinpearl = new ItemStack(Material.ENDER_PEARL);
                ItemMeta pearl_meta = infinpearl.getItemMeta();
                pearl_meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleport");
                ArrayList<String> pearl_lore = new ArrayList();
                pearl_meta.setUnbreakable(true);
                pearl_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                pearl_meta.setLore(pearl_lore);
                infinpearl.setItemMeta(pearl_meta);
                player.getInventory().addItem(infinpearl);
                removeItemPhantom(player);
            }, 1);
        } else if (originTag.contains("genesis:origin-shulk")) {
            float walk = 0.185F;
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                player.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(8.0);
                player.setWalkSpeed(walk);
                player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.45F);
                player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(2.2);
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else if (originTag.contains("genesis:origin-arachnid")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(14);
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else if (originTag.contains("genesis:origin-creep")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(18);
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else if (originTag.contains("genesis:origin-phantom")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(14);
                player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.11);
                ItemStack spectatorswitch = new ItemStack(Material.PHANTOM_MEMBRANE);
                ItemMeta switch_meta = spectatorswitch.getItemMeta();
                switch_meta.setDisplayName(GRAY + "Phantom Form");
                ArrayList<String> pearl_lore = new ArrayList();
                switch_meta.setUnbreakable(true);
                switch_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                switch_meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
                switch_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                switch_meta.setLore(pearl_lore);
                spectatorswitch.setItemMeta(switch_meta);
                player.getInventory().addItem(spectatorswitch);
                removeItemEnder(player);
            }, 1);
        } else if (originTag.contains("genesis:origin-slimeling")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else if (originTag.contains("genesis:origin-feline")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(18);
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else if (originTag.contains("genesis:origin-blaze")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else if (originTag.contains("genesis:origin-starborne")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else if (originTag.contains("geneis:origin-merling")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else if (originTag.contains("genesis:origin-allay")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else if (originTag.contains("genesis:origin-rabbit")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(14);
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else if (originTag.contains("genesis:origin-bee")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(14);
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else if (originTag.contains("genesis:origin-elytrian")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                removeItemPhantom(player);
                removeItemEnder(player);
                ItemStack launchitem = new ItemStack(Material.FEATHER);
                ItemMeta launchmeta = launchitem.getItemMeta();
                launchmeta.setDisplayName(GRAY + "Launch");
                launchmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                launchitem.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                launchitem.setItemMeta(launchmeta);
                player.getInventory().addItem(launchitem);
            }, 1);
        } else if (originTag.contains("genesis:origin-avian")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.17);
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else if (originTag.contains("genesis:origin-piglin")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else if (originTag.contains("genesis:origin-sculkling")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(24);
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        }
        SendCharts.originPopularity(player);
        assignPowers(player);
    }

    public static void resetOriginData(Player player, OriginDataType type) {
        if (type.equals(OriginDataType.CAN_EXPLODE)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
        } else if (type.equals(OriginDataType.PHANTOMIZED_ID)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "phantomid"), PersistentDataType.INTEGER, 1);
        } else if (type.equals(OriginDataType.ORIGINTAG)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-null");
        } else if (type.equals(OriginDataType.SHULKER_BOX_DATA)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "shulker-box"), PersistentDataType.STRING, "");
        } else if (type.equals(OriginDataType.TOGGLE)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "toggle"), PersistentDataType.INTEGER, 1);
        } else if (type.equals(OriginDataType.IN_PHANTOMIZED_FORM)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
        }

    }

    public static void setOriginData(Player player, OriginDataType type, int value) {
        if (type.equals(OriginDataType.CAN_EXPLODE)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, value);
        } else if (type.equals(OriginDataType.PHANTOMIZED_ID)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "phantomid"), PersistentDataType.INTEGER, value);
        } else if (type.equals(OriginDataType.TOGGLE)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "toggle"), PersistentDataType.INTEGER, value);
        } else if (type.equals(OriginDataType.IN_PHANTOMIZED_FORM)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, value);
        }
    }

    public static void setOriginData(Player player, OriginDataType type, String value) {
        if (type.equals(OriginDataType.ORIGINTAG)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, value);
        } else if (type.equals(OriginDataType.SHULKER_BOX_DATA)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "shulker-box"), PersistentDataType.STRING, value);
        }
    }

    public static boolean hasChosen(Player player) {
        return player.getScoreboardTags().contains("chosen");
    }

    public static void triggerChooseEvent(Player player) {
        OriginChooseEvent chooseEvent = new OriginChooseEvent(player);
        getServer().getPluginManager().callEvent(chooseEvent);
    }

    public static void openOriginGUI(Player player, OriginMenu menu) {
        if (menu.equals(OriginMenu.CHOOSE_MAIN)) {
            @NotNull Inventory custommenu = Bukkit.createInventory(player, 54, "Choosing Menu");
            custommenu.setContents(GenesisMainMenuContents(player));
            player.openInventory(custommenu);
        } else if (menu.equals(OriginMenu.CUSTOM_MAIN)) {
            @NotNull Inventory custommenu = Bukkit.createInventory(player, 54, "Custom Origins");
            custommenu.setContents(ChooseMenuContent());
            player.openInventory(custommenu);
        }
    }

    public static void assignPowers(Player player) {
        OriginContainer origin = getOrigin(player);
        for (PowerContainer power : origin.getPowerContainers()) {
            switch (power.getType()) {
                case "genesis:hot_hands" -> hot_hands.add(player);
                case "genesis:extra_fire_tick" -> extra_fire.add(player);
                case "genesis:bow_inability" -> bow_nope.add(player);
                case "genesis:silk_touch" -> silk_touch.add(player);
                case "genesis:explode_tick" -> explode_tick.add(player);
                case "genesis:projectile-immune" -> projectile_immune.add(player);
                case "genesis:charged" -> charged.add(player);
                case "genesis:felinephobia" -> felinephobia.add(player);
                case "genesis:fire_weak" -> fire_weak.add(player);
                case "genesis:gold_armour_buff" -> gold_armour_buff.add(player);
                case "genesis:gold_item_buff" -> gold_item_buff.add(player);
                case "genesis:big_leap_charge" -> big_leap_tick.add(player);
                case "genesis:carrots_only" -> carrot_only.add(player);
                case "genesis:jump_boost" -> jump_increased.add(player);
                case "genesis:drop_rabbit_foot_damage" -> rabbit_drop_foot.add(player);
                case "genesis:decreased_explosion_damage" -> decreased_explosion.add(player);
                case "genesis:creeper_head_death_drop" -> creeper_head_death_drop.add(player);
                case "genesis:resist_fall" -> resist_fall.add(player);
                case "genesis:cold_biomes_weak" -> weak_biome_cold.add(player);

                case "origins:fall_immunity" -> fall_immunity.add(player);
                case "origins:aerial_combatant" -> aerial_combatant.add(player);
                case "origins:aqua_affinity" -> aqua_affinity.add(player);
                case "origins:aquatic" -> aquatic.add(player);
                case "origins:arthropod" -> arthropod.add(player);
                case "origins:more_kinetic_damage" -> more_kinetic_damage.add(player);
                case "origins:burning_wrath" -> burning_wrath.add(player);
                case "origins:carnivore" -> carnivore.add(player);
                case "origins:scare_creepers" -> scare_creepers.add(player);
                case "origins:claustrophobia" -> claustrophobia.add(player);
                case "origins:climbing" -> climbing.add(player);
                case "origins:hunger_over_time" -> hunger_over_time.add(player);
                case "origins:slow_falling" -> slow_falling.add(player);
                case "origins:swim_speed" -> swim_speed.add(player);
                case "origins:fire_immunity" -> fire_immunity.add(player);
                case "origins:fragile" -> fragile.add(player);
                case "origins:fresh_air" -> fresh_air.add(player);
                case "origins:launch_into_air" -> launch_into_air.add(player);
                case "origins:water_breathing" -> water_breathing.add(player);
                case "origins:shulker_inventory" -> shulker_inventory.add(player);
                case "origins:hotblooded" -> hotblooded.add(player);
                case "origins:water_vulnerability" -> water_vulnerability.add(player);
                case "origins:invisibility" -> invisibility.add(player);
                case "origins:more_exhaustion" -> more_exhaustion.add(player);
                case "origins:like_air" -> like_air.add(player);
                case "origins:like_water" -> like_water.add(player);
                case "origins:master_of_webs" -> master_of_webs.add(player);
                case "origins:light_armor" -> light_armor.add(player);
                case "origins:nether_spawn" -> nether_spawn.add(player);
                case "origins:nine_lives" -> nine_lives.add(player);
                case "origins:cat_vision" -> cat_vision.add(player);
                case "origins:lay_eggs" -> lay_eggs.add(player);
                case "origins:phasing" -> phasing.add(player);
                case "origins:burn_in_daylight" -> burn_in_daylight.add(player);
                case "origins:arcane_skin" -> arcane_skin.add(player);
                case "origins:end_spawn" -> end_spawn.add(player);
                case "origins:phantomize_overlay" -> phantomize_overlay.add(player);
                case "origins:pumpkin_hate" -> pumpkin_hate.add(player);
                case "origins:extra_reach" -> extra_reach.add(player);
                case "origins:sprint_jump" -> sprint_jump.add(player);
                case "origins:strong_arms" -> strong_arms.add(player);
                case "origins:natural_armor" -> natural_armor.add(player);
                case "origins:tailwind" -> tailwind.add(player);
                case "origins:throw_ender_pearl" -> throw_ender_pearl.add(player);
                case "origins:translucent" -> translucent.add(player);
                case "origins:no_shield" -> no_shield.add(player);
                case "origins:vegetarian" -> vegetarian.add(player);
                case "origins:velvet_paws" -> velvet_paws.add(player);
                case "origins:weak_arms" -> weak_arms.add(player);
                case "origins:webbing" -> webbing.add(player);
                case "origins:water_vision" -> water_vision.add(player);
                case "origins:elytra_flight" -> elytra.add(player);
                case "origins:air_from_potions" -> air_from_potions.add(player);
                case "origins:conduit_power_on_land" -> conduit_power_on_land.add(player);
                case "origins:damage_from_potions" -> damage_from_potions.add(player);
                case "origins:damage_from_snowballs" -> damage_from_snowballs.add(player);
                case "origins:ender_particles" -> ender_particles.add(player);
                case "origins:flame_particles" -> flame_particles.add(player);
                case "origins:no_cobweb_slowdown" -> no_cobweb_slowdown.add(player);
                case "origins:phantomize" -> phantomize.add(player);
                case "origins:strong_arms_break_speed" -> strong_arms_break_speed.add(player);

                case "origins:attribute_modify_transfer" -> attribute_modify_transfer.add(player);

            }
        }
    }

    public static void unassignPowers(Player player) {
        OriginContainer origin = getOrigin(player);
        for (PowerContainer power : origin.getPowerContainers()) {
            if (!origin.getTag().equals(power.getSource())) continue;
            switch (power.getType()) {
                case "genesis:hot_hands" -> hot_hands.remove(player);
                case "genesis:extra_fire_tick" -> extra_fire.remove(player);
                case "genesis:bow_inability" -> bow_nope.remove(player);
                case "genesis:silk_touch" -> silk_touch.remove(player);
                case "genesis:explode_tick" -> explode_tick.remove(player);
                case "genesis:projectile-immune" -> projectile_immune.remove(player);
                case "genesis:charged" -> charged.remove(player);
                case "genesis:felinephobia" -> felinephobia.remove(player);
                case "genesis:fire_weak" -> fire_weak.remove(player);
                case "genesis:gold_armour_buff" -> gold_armour_buff.remove(player);
                case "genesis:gold_item_buff" -> gold_item_buff.remove(player);
                case "genesis:big_leap_charge" -> big_leap_tick.remove(player);
                case "genesis:carrots_only" -> carrot_only.remove(player);
                case "genesis:jump_boost" -> jump_increased.remove(player);
                case "genesis:drop_rabbit_foot_damage" -> rabbit_drop_foot.remove(player);
                case "genesis:decreased_explosion_damage" -> decreased_explosion.remove(player);
                case "genesis:creeper_head_death_drop" -> creeper_head_death_drop.remove(player);
                case "genesis:resist_fall" -> resist_fall.remove(player);
                case "genesis:cold_biomes_weak" -> weak_biome_cold.remove(player);

                case "origins:fall_immunity" -> fall_immunity.remove(player);
                case "origins:aerial_combatant" -> aerial_combatant.remove(player);
                case "origins:aqua_affinity" -> aqua_affinity.remove(player);
                case "origins:aquatic" -> aquatic.remove(player);
                case "origins:arthropod" -> arthropod.remove(player);
                case "origins:more_kinetic_damage" -> more_kinetic_damage.remove(player);
                case "origins:burning_wrath" -> burning_wrath.remove(player);
                case "origins:carnivore" -> carnivore.remove(player);
                case "origins:scare_creepers" -> scare_creepers.remove(player);
                case "origins:claustrophobia" -> claustrophobia.remove(player);
                case "origins:climbing" -> climbing.remove(player);
                case "origins:hunger_over_time" -> hunger_over_time.remove(player);
                case "origins:slow_falling" -> slow_falling.remove(player);
                case "origins:swim_speed" -> swim_speed.remove(player);
                case "origins:fire_immunity" -> fire_immunity.remove(player);
                case "origins:fragile" -> fragile.remove(player);
                case "origins:fresh_air" -> fresh_air.remove(player);
                case "origins:launch_into_air" -> launch_into_air.remove(player);
                case "origins:water_breathing" -> water_breathing.remove(player);
                case "origins:shulker_inventory" -> shulker_inventory.remove(player);
                case "origins:hotblooded" -> hotblooded.remove(player);
                case "origins:water_vulnerability" -> water_vulnerability.remove(player);
                case "origins:invisibility" -> invisibility.remove(player);
                case "origins:more_exhaustion" -> more_exhaustion.remove(player);
                case "origins:like_air" -> like_air.remove(player);
                case "origins:like_water" -> like_water.remove(player);
                case "origins:master_of_webs" -> master_of_webs.remove(player);
                case "origins:light_armor" -> light_armor.remove(player);
                case "origins:nether_spawn" -> nether_spawn.remove(player);
                case "origins:nine_lives" -> nine_lives.remove(player);
                case "origins:cat_vision" -> cat_vision.remove(player);
                case "origins:lay_eggs" -> lay_eggs.remove(player);
                case "origins:phasing" -> phasing.remove(player);
                case "origins:burn_in_daylight" -> burn_in_daylight.remove(player);
                case "origins:arcane_skin" -> arcane_skin.remove(player);
                case "origins:end_spawn" -> end_spawn.remove(player);
                case "origins:phantomize_overlay" -> phantomize_overlay.remove(player);
                case "origins:pumpkin_hate" -> pumpkin_hate.remove(player);
                case "origins:extra_reach" -> extra_reach.remove(player);
                case "origins:sprint_jump" -> sprint_jump.remove(player);
                case "origins:strong_arms" -> strong_arms.remove(player);
                case "origins:natural_armor" -> natural_armor.remove(player);
                case "origins:tailwind" -> tailwind.remove(player);
                case "origins:throw_ender_pearl" -> throw_ender_pearl.remove(player);
                case "origins:translucent" -> translucent.remove(player);
                case "origins:no_shield" -> no_shield.remove(player);
                case "origins:vegetarian" -> vegetarian.remove(player);
                case "origins:velvet_paws" -> velvet_paws.remove(player);
                case "origins:weak_arms" -> weak_arms.remove(player);
                case "origins:webbing" -> webbing.remove(player);
                case "origins:water_vision" -> water_vision.remove(player);
                case "origins:elytra_flight" -> elytra.remove(player);
                case "origins:air_from_potions" -> air_from_potions.remove(player);
                case "origins:conduit_power_on_land" -> conduit_power_on_land.remove(player);
                case "origins:damage_from_potions" -> damage_from_potions.remove(player);
                case "origins:damage_from_snowballs" -> damage_from_snowballs.remove(player);
                case "origins:ender_particles" -> ender_particles.remove(player);
                case "origins:flame_particles" -> flame_particles.remove(player);
                case "origins:no_cobweb_slowdown" -> no_cobweb_slowdown.remove(player);
                case "origins:phantomize" -> phantomize.remove(player);
                case "origins:strong_arms_break_speed" -> strong_arms_break_speed.remove(player);

                case "origins:attribute_modify_transfer" -> attribute_modify_transfer.remove(player);
            }
        }
    }

}
