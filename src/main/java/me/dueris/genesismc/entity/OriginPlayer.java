package me.dueris.genesismc.entity;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.enums.OriginDataType;
import me.dueris.genesismc.events.OriginChooseEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.Power;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.files.GenesisDataFiles;
import me.dueris.genesismc.utils.LayerContainer;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import me.dueris.genesismc.utils.SendCharts;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static me.dueris.genesismc.factory.CraftApoli.fileToFileContainer;
import static me.dueris.genesismc.factory.powers.OriginsMod.prevent.PreventSuperClass.*;
import static me.dueris.genesismc.factory.powers.OriginsMod.value_modifying.ValueModifyingSuperClass.*;
import static org.bukkit.Bukkit.getServer;

public class OriginPlayer {

//    public static boolean hasChosenOrigin(Player player) {
//        return !OriginPlayer.getOrigin(player).getTag().equalsIgnoreCase("");
//    }

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

    /**
     * @param originTag The tag of the origin.
     * @return true if the player has the origin.
     */
    public static boolean hasOrigin(Player player, String originTag) {
        HashMap<LayerContainer, OriginContainer> origins = CraftApoli.toOrigin(player.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origins"), PersistentDataType.BYTE_ARRAY));
        for (OriginContainer origin : origins.values()) if (origin.getTag().equals(originTag)) return true;
        return false;
    }

    /**
     * @param layer The layer the origin is in
     * @return The OriginContainer for the specified layer
     */

    public static OriginContainer getOrigin(Player player, LayerContainer layer) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        if (data.get(new NamespacedKey(GenesisMC.getPlugin(), "origins"), PersistentDataType.BYTE_ARRAY) == null) {
            setOrigin(player, layer, CraftApoli.nullOrigin());
            return CraftApoli.nullOrigin();
        }
        return CraftApoli.toOrigin(data.get(new NamespacedKey(GenesisMC.getPlugin(), "origins"), PersistentDataType.BYTE_ARRAY), layer);
    }

    /**
     * @return A HashMap of layers and OriginContainer that the player has.
     */

    public static HashMap<LayerContainer, OriginContainer> getOrigin(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        if (data.get(new NamespacedKey(GenesisMC.getPlugin(), "origins"), PersistentDataType.BYTE_ARRAY) == null) {
            ArrayList<LayerContainer> layers = CraftApoli.getLayers();
            for (LayerContainer layer : layers) {
                setOrigin(player, layer, CraftApoli.nullOrigin());
                return new HashMap<>(Map.of(layer, CraftApoli.nullOrigin()));
            }
        }
        return CraftApoli.toOrigin(data.get(new NamespacedKey(GenesisMC.getPlugin(), "origins"), PersistentDataType.BYTE_ARRAY));
    }

    public static boolean hasCoreOrigin(Player player, LayerContainer layer) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        String originTag = OriginPlayer.getOrigin(player, layer).getTag();
        if (originTag.contains("origins:human")) {
            return true;
        } else if (originTag.contains("origins:enderian")) {
            return true;
        } else if (originTag.contains("origins:merling")) {
            return true;
        } else if (originTag.contains("origins:phantom")) {
            return true;
        } else if (originTag.contains("origins:elytrian")) {
            return true;
        } else if (originTag.contains("origins:blazeborn")) {
            return true;
        } else if (originTag.contains("origins:avian")) {
            return true;
        } else if (originTag.contains("origins:arachnid")) {
            return true;
        } else if (originTag.contains("origins:shulk")) {
            return true;
        } else if (originTag.contains("origins:feline")) {
            return true;
        } else if (originTag.contains("origins:starborne")) {
            return true;
        } else if (originTag.contains("origins:allay")) {
            return true;
        } else if (originTag.contains("origins:rabbit")) {
            return true;
        } else if (originTag.contains("origins:bee")) {
            return true;
        } else if (originTag.contains("origins:sculkling")) {
            return true;
        } else if (originTag.contains("origins:creep")) {
            return true;
        } else if (originTag.contains("origins:slimeling")) {
            return true;
        } else return originTag.contains("origins:piglin");
    }

    public static void setOrigin(Player player, LayerContainer layer, OriginContainer origin) {
        NamespacedKey key = new NamespacedKey(GenesisMC.getPlugin(), "origins");
        HashMap<LayerContainer, OriginContainer> origins = CraftApoli.toOrigin(player.getPersistentDataContainer().get(key, PersistentDataType.BYTE_ARRAY));
        assert origins != null;
        if (!CraftApoli.getLayers().contains(layer)) {
            return;
        }

        unassignPowers(player, layer);
        for (LayerContainer layers : origins.keySet()) {
            if (layer.getTag().equals(layers.getTag())) origins.replace(layers, origin);
        }
        player.getPersistentDataContainer().set(key, PersistentDataType.BYTE_ARRAY, CraftApoli.toByteArray(origins));

        String originTag = origin.getTag();
        if (!originTag.equals(CraftApoli.nullOrigin().getTag())) SendCharts.originPopularity(player);
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    assignPowers(player, layer);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskLater(GenesisMC.getPlugin(), 3L);
    }

    /**
     * WARNING: will remove the layer containing the origin from the playerdata. If you need to make a player re choose an origin use setOrigin and pass in CraftApoli.nullOrigin().
     *
     * @param player player.
     * @param layer  the layer to remove from playerdata.
     */
    public static void removeOrigin(Player player, LayerContainer layer) {
        HashMap<LayerContainer, OriginContainer> origins = getOrigin(player);
        ArrayList<LayerContainer> layers = new ArrayList<>(origins.keySet());
        for (LayerContainer playerLayer : layers) {
            if (playerLayer.getTag().equals(layer.getTag())) origins.remove(playerLayer);
        }
        player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origins"), PersistentDataType.BYTE_ARRAY, CraftApoli.toByteArray(origins));
    }

    public static LayerContainer getLayer(Player p, OriginContainer origin) {
        HashMap<LayerContainer, OriginContainer> origins = getOrigin(p);
        for (LayerContainer layer : origins.keySet()) {
            if (origins.get(layer).getTag().equals(origin.getTag())) return layer;
        }
        return null;
    }

    public static void resetOriginData(Player player, OriginDataType type) {
        if (type.equals(OriginDataType.CAN_EXPLODE)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
        } else if (type.equals(OriginDataType.SHULKER_BOX_DATA)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "shulker-box"), PersistentDataType.STRING, "");
        } else if (type.equals(OriginDataType.TOGGLE)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "toggle"), PersistentDataType.INTEGER, 1);
        } else if (type.equals(OriginDataType.IN_PHASING_FORM)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.BOOLEAN, false);
        }

    }

    public static void setOriginData(Player player, OriginDataType type, int value) {
        if (type.equals(OriginDataType.CAN_EXPLODE)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, value);
        } else if (type.equals(OriginDataType.TOGGLE)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "toggle"), PersistentDataType.INTEGER, value);
        }
    }

    public static void setOriginData(Player player, OriginDataType type, boolean value) {
        if (type.equals(OriginDataType.IN_PHASING_FORM)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.BOOLEAN, value);
        }
    }

    public static void triggerChooseEvent(Player player) {
        OriginChooseEvent chooseEvent = new OriginChooseEvent(player);
        getServer().getPluginManager().callEvent(chooseEvent);
    }

    public static boolean isInPhantomForm(Player player) {
        return player.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.BOOLEAN);
    }

    public static void assignPowers(Player player) {
        HashMap<LayerContainer, OriginContainer> origins = getOrigin(player);
        for (LayerContainer layer : origins.keySet()) {
            try {
                assignPowers(player, layer);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void assignPowers(Player player, LayerContainer layer) throws InstantiationException, IllegalAccessException {
        OriginContainer origin = getOrigin(player, layer);
        for (PowerContainer power : origin.getPowerContainers()) {
            for (Class<? extends CraftPower> c : CraftPower.getRegistered()) {
                CraftPower craftPower = c.newInstance();
                player.sendMessage(origin.getPowerContainers().toString());
                player.sendMessage("12");
                if(power.getType() != "origins:multiple"){
                    player.sendMessage(power.getType());
                }
                if (power.getType().equals(craftPower.getPowerFile())) {
                    player.sendMessage("adddedded");
                    craftPower.getPowerArray().add(player);
                    if(GenesisDataFiles.getMainConfig().getString("console-startup-debug").equalsIgnoreCase("true")){
                        Bukkit.getConsoleSender().sendMessage("GenesisMC-Origins assigned power[" + craftPower.getPowerFile() + "] on layer[" + layer.getTag() +"] to player " + player.getName());
                    }
                }
            }
            //TODO: remove the switch statement due to using different methods of doing it. required due to some powers not being complete yet
            switch (power.getType()) {
                case "genesis:hot_hands" -> Power.hot_hands.add(player);
                case "genesis:extra_fire_tick" -> Power.extra_fire.add(player);
                case "genesis:bow_inability" -> Power.bow_nope.add(player);
                case "genesis:silk_touch" -> Power.silk_touch.add(player);
                case "genesis:explode_tick" -> Power.explode_tick.add(player);
                case "genesis:projectile-immune" -> Power.projectile_immune.add(player);
                case "genesis:charged" -> Power.charged.add(player);
                case "genesis:felinephobia" -> Power.felinephobia.add(player);
                case "genesis:blue_fire_weak" -> Power.fire_weak.add(player);
                case "genesis:gold_armour_buff" -> Power.gold_armour_buff.add(player);
                case "genesis:gold_item_buff" -> Power.gold_item_buff.add(player);
                case "genesis:leap" -> Power.big_leap_tick.add(player);
                case "genesis:carrots_only" -> Power.carrot_only.add(player);
                case "genesis:jump_boost" -> Power.jump_increased.add(player);
                case "genesis:drop_rabbit_foot_damage" -> Power.rabbit_drop_foot.add(player);
                case "genesis:decreased_explosion_damage" -> Power.decreased_explosion.add(player);
                case "genesis:creeper_head_death_drop" -> Power.creeper_head_death_drop.add(player);
                case "genesis:resist_fall" -> Power.resist_fall.add(player);
                case "genesis:cold_biomes_weak" -> Power.weak_biome_cold.add(player);
                case "genesis:overworld_piglin_zombified" -> Power.overworld_piglin_zombified.add(player);

                case "origins:fall_immunity" -> Power.fall_immunity.add(player);
                case "origins:aerial_combatant" -> Power.aerial_combatant.add(player);
                case "origins:aqua_affinity" -> Power.aqua_affinity.add(player);
                case "origins:aquatic" -> Power.aquatic.add(player);
                case "origins:arthropod" -> Power.arthropod.add(player);
                case "origins:more_kinetic_damage" -> Power.more_kinetic_damage.add(player);
                case "origins:burning_wrath" -> Power.burning_wrath.add(player);
                case "origins:carnivore" -> Power.carnivore.add(player);
                case "origins:scare_creepers" -> Power.scare_creepers.add(player);
                case "origins:claustrophobia" -> Power.claustrophobia.add(player);
                case "origins:climbing" -> Power.climbing.add(player);
                case "origins:hunger_over_time" -> Power.hunger_over_time.add(player);
                case "origins:slow_falling" -> Power.slow_falling.add(player);
                case "origins:swim_speed" -> Power.swim_speed.add(player);
                case "origins:fire_immunity" -> Power.fire_immunity.add(player);
                case "origins:fragile" -> Power.fragile.add(player);
                case "origins:fresh_air" -> Power.fresh_air.add(player);
                case "origins:launch" -> Power.launch_into_air.add(player);
                case "origins:water_breathing" -> Power.water_breathing.add(player);
                case "origins:inventory" -> Power.shulker_inventory.add(player);
                case "origins:hotblooded" -> Power.hotblooded.add(player);
                case "origins:water_vulnerability" -> Power.water_vulnerability.add(player);
                case "origins:invisibility" -> Power.invisibility.add(player);
                case "origins:like_air" -> Power.like_air.add(player);
                case "origins:like_water" -> Power.like_water.add(player);
                case "origins:master_of_webs" -> Power.master_of_webs.add(player);
                case "origins:light_armor" -> Power.light_armor.add(player);
                case "origins:nether_spawn" -> Power.nether_spawn.add(player);
                case "origins:nine_lives" -> Power.nine_lives.add(player);
                case "origins:cat_vision" -> Power.cat_vision.add(player);
                case "origins:lay_eggs" -> Power.lay_eggs.add(player);
                case "origins:phasing" -> Power.phasing.add(player);
                case "origins:burn_in_daylight" -> Power.burn_in_daylight.add(player);
                case "origins:arcane_skin" -> Power.arcane_skin.add(player);
                case "origins:end_spawn" -> Power.end_spawn.add(player);
                case "origins:phantomize_overlay" -> Power.phantomize_overlay.add(player);
                case "origins:pumpkin_hate" -> Power.pumpkin_hate.add(player);
                case "origins:extra_reach" -> Power.extra_reach.add(player);
                case "origins:sprint_jump" -> Power.sprint_jump.add(player);
                case "origins:strong_arms" -> Power.strong_arms.add(player);
                case "origins:natural_armor" -> Power.natural_armor.add(player);
                case "origins:tailwind" -> Power.tailwind.add(player);
                case "origins:throw_ender_pearl" -> Power.throw_ender_pearl.add(player);
                case "origins:translucent" -> Power.translucent.add(player);
                case "origins:no_shield" -> Power.no_shield.add(player);
                case "origins:vegetarian" -> Power.vegetarian.add(player);
                case "origins:velvet_paws" -> Power.velvet_paws.add(player);
                case "origins:weak_arms" -> Power.weak_arms.add(player);
                case "origins:webbing" -> Power.webbing.add(player);
                case "origins:water_vision" -> Power.water_vision.add(player);
                case "origins:elytra_flight" -> Power.elytra.add(player);
                case "origins:air_from_potions" -> Power.air_from_potions.add(player);
                case "origins:conduit_power_on_land" -> Power.conduit_power_on_land.add(player);
                case "origins:damage_from_potions" -> Power.damage_from_potions.add(player);
                case "origins:damage_from_snowballs" -> Power.damage_from_snowballs.add(player);
                case "origins:ender_particles" -> Power.ender_particles.add(player);
                case "origins:flame_particles" -> Power.flame_particles.add(player);
                case "origins:no_cobweb_slowdown" -> Power.no_cobweb_slowdown.add(player);
                case "origins:phantomize" -> Power.phantomize.add(player);
                case "origins:strong_arms_break_speed" -> Power.strong_arms_break_speed.add(player);
                case "origins:apply_effect" -> Power.apply_effect.add(player);
                case "origins:effect_immunity" -> Power.effect_immunity.add(player);
                case "origins:attribute" -> Power.attribute.add(player);
                case "origins:attribute_modify_transfer" -> Power.attribute_modify_transfer.add(player);
                case "origins:conditioned_attribute" -> Power.conditioned_attribute.add(player);
                case "origins:creative_flight" -> Power.creative_flight.add(player);
                case "origins:night_vision" -> Power.night_vision.add(player);
                case "origins:burn" -> Power.burn.add(player);
                case "origins:restrict_armor" -> Power.restrict_armor.add(player);
                case "origins:invulnerability" -> Power.dmg_invulnerable.add(player);
                case "origins:model_color" -> Power.model_color.add(player);
                case "genesis:bioluminescent" -> Power.bioluminescent.add(player);
                case "origins:entity_glow" -> Power.entity_glow.add(player);
                case "origins:entity_group" -> Power.entity_group.add(player);
                case "origins:exhaust" -> Power.more_exhaustion.add(player);
                case "origins:damage_over_time" -> Power.damage_over_time.add(player);
                case "origins:disable_regen" -> Power.disable_regen.add(player);
                case "origins:freeze" -> Power.freeze.add(player);
                case "origins:fire_projectile" -> Power.fire_projectile.add(player);
                case "origins:grounded" -> Power.grounded.add(player);
                case "genesis:no_gravity" -> Power.no_gravity.add(player);
                case "origins:toggle" -> Power.toggle_power.add(player);
                case "origins:keep_inventory" -> Power.keep_inventory.add(player);
                case "origins:overlay" -> Power.overlay.add(player);
                case "origins:particle" -> Power.particle.add(player);
                case "origins:recipe" -> Power.recipe.add(player);
                case "origins:modify_air_speed" -> modify_air_speed.add(player);
                case "origins:modify_break_speed" -> modify_break_speed.add(player);
                case "origins:modify_block_render" -> modify_block_render.add(player);
                case "origins:modify_crafting" -> modify_crafting.add(player);
                case "origins:modify_damage_taken" -> modify_damage_taken.add(player);
                case "origins:modify_damage_dealt" -> modify_damage_dealt.add(player);
                case "origins:modify_exhaustion" -> modify_exhaustion.add(player);
                case "origins:modify_falling" -> modify_falling.add(player);
                case "origins:modify_food" -> modify_food.add(player);
                case "origins:modify_harvest" -> modify_harvest.add(player);
                case "origins:modify_healing" -> modify_healing.add(player);
                case "origins:modify_jump" -> modify_jump.add(player);
                case "origins:modify_lava_speed" -> modify_lava_speed.add(player);
                case "origins:modify_player_spawn" -> modify_world_spawn.add(player);
                case "origins:modify_projectile_damage" -> modify_projectile_damage.add(player);
                case "origins:modify_status_effect_duration" -> modify_effect_duration.add(player);
                case "origins:modify_status_effect_amplifier" -> modify_effect_amplifier.add(player);
                case "origins:modify_swim_speed" -> modify_swim_speed.add(player);
                case "origins:modify_xp_gain" -> modify_xp_gain.add(player);
                case "origins:prevent_being_used" -> prevent_being_used.add(player);
                case "origins:prevent_block_selection" -> prevent_block_selection.add(player);
                case "origins:prevent_block_use" -> prevent_block_use.add(player);
                case "origins:prevent_death" -> prevent_death.add(player);
                case "origins:prevent_elytra_flight" -> prevent_elytra_flight.add(player);
                case "origins:prevent_entity_collision" -> prevent_entity_collision.add(player);
                case "origins:prevent_entity_render" -> prevent_entity_render.add(player);
                case "origins:prevent_entity_use" -> prevent_entity_use.add(player);
                case "origins:prevent_item_use" -> prevent_item_use.add(player);
                case "origins:prevent_sleep" -> prevent_sleep.add(player);

                //actions
                case "origins:action_on_being_used" -> Power.action_on_being_used.add(player);
                case "origins:action_on_being_hit" -> Power.action_on_being_hit.add(player);
                case "origins:action_on_block_break" -> Power.action_on_block_break.add(player);
                case "origins:action_on_block_use" -> Power.action_on_block_use.add(player);
                case "origins:action_on_callback" -> Power.action_on_callback.add(player);
                case "origins:action_on_entity_use" -> Power.action_on_entity_use.add(player);
                case "origins:action_on_hit" -> Power.action_on_hit.add(player);
                case "origins:action_on_item_use" -> Power.action_on_item_use.add(player);
                case "origins:action_on_land" -> Power.action_on_land.add(player);
                case "origins:action_on_wake_up" -> Power.action_on_wake_up.add(player);
                case "origins:action_ove_time" -> Power.action_ove_time.add(player);
                case "origins:action_when_damage_taken" -> Power.action_when_damage_taken.add(player);
                case "origins:action_when_hit" -> Power.action_when_hit.add(player);
                case "origins:active_self" -> Power.active_self.add(player);
                case "origins:attacker_action_when_hit" -> Power.attacker_action_when_hit.add(player);
                case "origins:self_action_on_hit" -> Power.self_action_on_hit.add(player);
                case "origins:self_action_on_kill" -> Power.self_action_on_kill.add(player);
                case "origins:self_action_when_hit" -> Power.self_action_when_hit.add(player);
                case "origins:target_action_on_hit" -> Power.target_action_on_hit.add(player);
            }
        }
    }

    public static void unassignPowers(Player player) {
        HashMap<LayerContainer, OriginContainer> origins = getOrigin(player);
        for (LayerContainer layer : origins.keySet()) {
            unassignPowers(player, layer);
        }
    }

    public static void unassignPowers(Player player, LayerContainer layer) {
        OriginContainer origin = getOrigin(player, layer);
        for (PowerContainer power : origin.getPowerContainers()) {
            for (Class<? extends CraftPower> c : CraftPower.getRegistered()) {
                CraftPower craftPower = null;
                try {
                    craftPower = c.newInstance();
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                if (power.getType().equals(craftPower.getPowerFile())) {
                    craftPower.getPowerArray().remove(player);
                    if(GenesisDataFiles.getMainConfig().getString("console-startup-debug").equalsIgnoreCase("true")){
                        Bukkit.getConsoleSender().sendMessage("GenesisMC-Origins removed power[" + craftPower.getPowerFile() + "] on layer[" + layer.getTag() +"] to player " + player.getName());
                    }
                }
            }
            //TODO: remove the switch statement due to using different methods of doing it. required due to some powers not being complete yet
            switch (power.getType()) {
                case "genesis:hot_hands" -> Power.hot_hands.remove(player);
                case "genesis:extra_fire_tick" -> Power.extra_fire.remove(player);
                case "genesis:bow_inability" -> Power.bow_nope.remove(player);
                case "genesis:silk_touch" -> Power.silk_touch.remove(player);
                case "genesis:explode_tick" -> Power.explode_tick.remove(player);
                case "genesis:projectile-immune" -> Power.projectile_immune.remove(player);
                case "genesis:charged" -> Power.charged.remove(player);
                case "genesis:felinephobia" -> Power.felinephobia.remove(player);
                case "genesis:blue_fire_weak" -> Power.fire_weak.remove(player);
                case "genesis:gold_armour_buff" -> Power.gold_armour_buff.remove(player);
                case "genesis:gold_item_buff" -> Power.gold_item_buff.remove(player);
                case "genesis:big_leap_charge" -> Power.big_leap_tick.remove(player);
                case "genesis:carrots_only" -> Power.carrot_only.remove(player);
                case "genesis:jump_boost" -> Power.jump_increased.remove(player);
                case "genesis:drop_rabbit_foot_damage" -> Power.rabbit_drop_foot.remove(player);
                case "genesis:decreased_explosion_damage" -> Power.decreased_explosion.remove(player);
                case "genesis:creeper_head_death_drop" -> Power.creeper_head_death_drop.remove(player);
                case "genesis:resist_fall" -> Power.resist_fall.remove(player);
                case "genesis:cold_biomes_weak" -> Power.weak_biome_cold.remove(player);
                case "genesis:overworld_piglin_zombified" -> Power.overworld_piglin_zombified.remove(player);

                case "origins:fall_immunity" -> Power.fall_immunity.remove(player);
                case "origins:aerial_combatant" -> Power.aerial_combatant.remove(player);
                case "origins:aqua_affinity" -> Power.aqua_affinity.remove(player);
                case "origins:aquatic" -> Power.aquatic.remove(player);
                case "origins:arthropod" -> Power.arthropod.remove(player);
                case "origins:more_kinetic_damage" -> Power.more_kinetic_damage.remove(player);
                case "origins:burning_wrath" -> Power.burning_wrath.remove(player);
                case "origins:carnivore" -> Power.carnivore.remove(player);
                case "origins:scare_creepers" -> Power.scare_creepers.remove(player);
                case "origins:claustrophobia" -> Power.claustrophobia.remove(player);
                case "origins:climbing" -> Power.climbing.remove(player);
                case "origins:hunger_over_time" -> Power.hunger_over_time.remove(player);
                case "origins:slow_falling" -> Power.slow_falling.remove(player);
                case "origins:swim_speed" -> Power.swim_speed.remove(player);
                case "origins:fire_immunity" -> Power.fire_immunity.remove(player);
                case "origins:fragile" -> Power.fragile.remove(player);
                case "origins:fresh_air" -> Power.fresh_air.remove(player);
                case "origins:launch" -> Power.launch_into_air.remove(player);
                case "origins:water_breathing" -> Power.water_breathing.remove(player);
                case "origins:inventory" -> Power.shulker_inventory.remove(player);
                case "origins:hotblooded" -> Power.hotblooded.remove(player);
                case "origins:water_vulnerability" -> Power.water_vulnerability.remove(player);
                case "origins:invisibility" -> Power.invisibility.remove(player);
                case "origins:more_exhaustion" -> Power.more_exhaustion.remove(player);
                case "origins:like_air" -> Power.like_air.remove(player);
                case "origins:like_water" -> Power.like_water.remove(player);
                case "origins:master_of_webs" -> Power.master_of_webs.remove(player);
                case "origins:light_armor" -> Power.light_armor.remove(player);
                case "origins:nether_spawn" -> Power.nether_spawn.remove(player);
                case "origins:nine_lives" -> Power.nine_lives.remove(player);
                case "origins:cat_vision" -> Power.cat_vision.remove(player);
                case "origins:lay_eggs" -> Power.lay_eggs.remove(player);
                case "origins:phasing" -> Power.phasing.remove(player);
                case "origins:burn_in_daylight" -> Power.burn_in_daylight.remove(player);
                case "origins:arcane_skin" -> Power.arcane_skin.remove(player);
                case "origins:end_spawn" -> Power.end_spawn.remove(player);
                case "origins:phantomize_overlay" -> Power.phantomize_overlay.remove(player);
                case "origins:pumpkin_hate" -> Power.pumpkin_hate.remove(player);
                case "origins:extra_reach" -> Power.extra_reach.remove(player);
                case "origins:sprint_jump" -> Power.sprint_jump.remove(player);
                case "origins:strong_arms" -> Power.strong_arms.remove(player);
                case "origins:natural_armor" -> Power.natural_armor.remove(player);
                case "origins:tailwind" -> Power.tailwind.remove(player);
                case "origins:throw_ender_pearl" -> Power.throw_ender_pearl.remove(player);
                case "origins:translucent" -> Power.translucent.remove(player);
                case "origins:no_shield" -> Power.no_shield.remove(player);
                case "origins:vegetarian" -> Power.vegetarian.remove(player);
                case "origins:velvet_paws" -> Power.velvet_paws.remove(player);
                case "origins:weak_arms" -> Power.weak_arms.remove(player);
                case "origins:webbing" -> Power.webbing.remove(player);
                case "origins:water_vision" -> Power.water_vision.remove(player);
                case "origins:elytra_flight" -> Power.elytra.remove(player);
                case "origins:air_from_potions" -> Power.air_from_potions.remove(player);
                case "origins:conduit_power_on_land" -> Power.conduit_power_on_land.remove(player);
                case "origins:damage_from_potions" -> Power.damage_from_potions.remove(player);
                case "origins:damage_from_snowballs" -> Power.damage_from_snowballs.remove(player);
                case "origins:ender_particles" -> Power.ender_particles.remove(player);
                case "origins:flame_particles" -> Power.flame_particles.remove(player);
                case "origins:no_cobweb_slowdown" -> Power.no_cobweb_slowdown.remove(player);
                case "origins:phantomize" -> Power.phantomize.remove(player);
                case "origins:attribute" -> Power.attribute.remove(player);
                case "origins:effect_immunity" -> Power.effect_immunity.remove(player);
                case "origins:strong_arms_break_speed" -> Power.strong_arms_break_speed.remove(player);
                case "origins:conditioned_attribute" -> Power.conditioned_attribute.remove(player);
                case "origins:attribute_modify_transfer" -> Power.attribute_modify_transfer.remove(player);
                case "origins:night_vision" -> Power.night_vision.remove(player);
                case "origins:creative_flight" -> Power.creative_flight.remove(player);
                case "origins:burn" -> Power.burn.remove(player);
                case "origins:restrict_armor" -> Power.restrict_armor.remove(player);
                case "origins:invulnerability" -> Power.dmg_invulnerable.remove(player);
                case "origins:model_color" -> Power.model_color.remove(player);
                case "genesis:bioluminescent" -> Power.bioluminescent.remove(player);
                case "origins:entity_glow" -> Power.entity_glow.remove(player);
                case "origins:entity_group" -> Power.entity_group.remove(player);
                case "origins:damage_over_time" -> Power.damage_over_time.remove(player);
                case "origins:disable_regen" -> Power.disable_regen.remove(player);
                case "origins:freeze" -> Power.freeze.remove(player);
                case "origins:fire_projectile" -> Power.fire_projectile.remove(player);
                case "origins:grounded" -> Power.grounded.remove(player);
                case "origins:toggle" -> Power.toggle_power.remove(player);
                case "origins:keep_inventory" -> Power.keep_inventory.remove(player);
                case "origins:overlay" -> Power.overlay.remove(player);
                case "origins:particle" -> Power.particle.remove(player);
                case "origins:recipe" -> Power.recipe.remove(player);
                case "origins:modify_air_speed" -> modify_air_speed.remove(player);
                case "origins:modify_block_render" -> modify_block_render.remove(player);
                case "origins:modify_break_speed" -> modify_break_speed.remove(player);
                case "origins:modify_crafting" -> modify_crafting.remove(player);
                case "origins:modify_damage_taken" -> modify_damage_taken.remove(player);
                case "origins:modify_damage_dealt" -> modify_damage_dealt.remove(player);
                case "origins:modify_exhaustion" -> modify_exhaustion.remove(player);
                case "origins:modify_falling" -> modify_falling.remove(player);
                case "origins:modify_food" -> modify_food.remove(player);
                case "origins:modify_harvest" -> modify_harvest.remove(player);
                case "origins:modify_healing" -> modify_healing.remove(player);
                case "origins:modify_jump" -> modify_jump.remove(player);
                case "origins:modify_lava_speed" -> modify_lava_speed.remove(player);
                case "origins:modify_player_spawn" -> modify_world_spawn.remove(player);
                case "origins:modify_projectile_damage" -> modify_projectile_damage.remove(player);
                case "origins:modify_status_effect_duration" -> modify_effect_duration.remove(player);
                case "origins:modify_status_effect_amplifier" -> modify_effect_amplifier.remove(player);
                case "origins:modify_swim_speed" -> modify_swim_speed.remove(player);
                case "origins:modify_xp_gain" -> modify_xp_gain.remove(player);
                case "origins:prevent_being_used" -> prevent_being_used.remove(player);
                case "origins:prevent_block_selection" -> prevent_block_selection.remove(player);
                case "origins:prevent_block_use" -> prevent_block_use.remove(player);
                case "origins:prevent_death" -> prevent_death.remove(player);
                case "origins:prevent_elytra_flight" -> prevent_elytra_flight.remove(player);
                case "origins:prevent_entity_collision" -> prevent_entity_collision.remove(player);
                case "origins:prevent_entity_render" -> prevent_entity_render.remove(player);
                case "origins:prevent_entity_use" -> prevent_entity_use.remove(player);
                case "origins:prevent_item_use" -> prevent_item_use.remove(player);
                case "origins:prevent_sleep" -> prevent_sleep.remove(player);

                //actions
                case "origins:action_on_being_used" -> Power.action_on_being_used.remove(player);
                case "origins:action_on_being_hit" -> Power.action_on_being_hit.remove(player);
                case "origins:action_on_block_break" -> Power.action_on_block_break.remove(player);
                case "origins:action_on_block_use" -> Power.action_on_block_use.remove(player);
                case "origins:action_on_callback" -> Power.action_on_callback.remove(player);
                case "origins:action_on_entity_use" -> Power.action_on_entity_use.remove(player);
                case "origins:action_on_hit" -> Power.action_on_hit.remove(player);
                case "origins:action_on_item_use" -> Power.action_on_item_use.remove(player);
                case "origins:action_on_land" -> Power.action_on_land.remove(player);
                case "origins:action_on_wake_up" -> Power.action_on_wake_up.remove(player);
                case "origins:action_ove_time" -> Power.action_ove_time.remove(player);
                case "origins:action_when_damage_taken" -> Power.action_when_damage_taken.remove(player);
                case "origins:action_when_hit" -> Power.action_when_hit.remove(player);
                case "origins:active_self" -> Power.active_self.remove(player);
                case "origins:attacker_action_when_hit" -> Power.attacker_action_when_hit.remove(player);
                case "origins:self_action_on_hit" -> Power.self_action_on_hit.remove(player);
                case "origins:self_action_on_kill" -> Power.self_action_on_kill.remove(player);
                case "origins:self_action_when_hit" -> Power.self_action_when_hit.remove(player);
                case "origins:target_action_on_hit" -> Power.target_action_on_hit.remove(player);
            }
        }
    }

    /**
     * @param p Player
     * @return The layers and origins currently assigned to the player
     */
    public static HashMap<LayerContainer, OriginContainer> returnOrigins(Player p) {
        return CraftApoli.toOrigin(p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origins"), PersistentDataType.BYTE_ARRAY));
    }

}
