package me.dueris.genesismc.factory.powers;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public interface Power {

    public abstract void run();
    public abstract String getPowerFile();
    public abstract ArrayList<Player> getPowerArray();
    public abstract void setActive(Boolean bool);
//    {
//        if(powers_active.containsKey(getPowerFile())){
//            powers_active.replace(getPowerFile(), bool);
//        }else{
//            powers_active.put(getPowerFile(), bool);
//        }
//    }
    public abstract Boolean getActive();
//    {
//        return powers_active.get(getPowerFile());
//    }

    public HashMap<String, Boolean> powers_active = new HashMap<>();

    public ArrayList<Player> fall_immunity = new ArrayList<>();
    public ArrayList<Player> aerial_combatant = new ArrayList<>();
    public ArrayList<Player> aqua_affinity = new ArrayList<>();
    public ArrayList<Player> aquatic = new ArrayList<>();
    public ArrayList<Player> arthropod = new ArrayList<>();
    public ArrayList<Player> more_kinetic_damage = new ArrayList<>();
    public ArrayList<Player> burning_wrath = new ArrayList<>();
    public ArrayList<Player> carnivore = new ArrayList<>();
    public ArrayList<Player> scare_creepers = new ArrayList<>();
    public ArrayList<Player> claustrophobia = new ArrayList<>();
    public ArrayList<Player> climbing = new ArrayList<>();
    public ArrayList<Player> hunger_over_time = new ArrayList<>();
    public ArrayList<Player> slow_falling = new ArrayList<>();
    public ArrayList<Player> swim_speed = new ArrayList<>();
    public ArrayList<Player> fire_immunity = new ArrayList<>();
    public ArrayList<Player> fragile = new ArrayList<>();
    public ArrayList<Player> fresh_air = new ArrayList<>();
    public ArrayList<Player> launch_into_air = new ArrayList<>();
    public ArrayList<Player> water_breathing = new ArrayList<>();
    public ArrayList<Player> shulker_inventory = new ArrayList<>();
    public ArrayList<Player> hotblooded = new ArrayList<>();
    public ArrayList<Player> water_vulnerability = new ArrayList<>();
    public ArrayList<Player> invisibility = new ArrayList<>();
    public ArrayList<Player> more_exhaustion = new ArrayList<>();
    public ArrayList<Player> like_air = new ArrayList<>();
    public ArrayList<Player> like_water = new ArrayList<>();
    public ArrayList<Player> master_of_webs = new ArrayList<>();
    public ArrayList<Player> light_armor = new ArrayList<>();
    public ArrayList<Player> nether_spawn = new ArrayList<>();
    public ArrayList<Player> nine_lives = new ArrayList<>();
    public ArrayList<Player> cat_vision = new ArrayList<>();
    public ArrayList<Player> lay_eggs = new ArrayList<>();
    public ArrayList<Player> phasing = new ArrayList<>();
    public ArrayList<Player> burn_in_daylight = new ArrayList<>();
    public ArrayList<Player> arcane_skin = new ArrayList<>();
    public ArrayList<Player> end_spawn = new ArrayList<>();
    public ArrayList<Player> phantomize_overlay = new ArrayList<>();
    public ArrayList<Player> pumpkin_hate = new ArrayList<>();
    public ArrayList<Player> extra_reach = new ArrayList<>();
    public ArrayList<Player> extra_reach_attack = new ArrayList<>();
    public ArrayList<Player> sprint_jump = new ArrayList<>();
    public ArrayList<Player> strong_arms = new ArrayList<>();
    public ArrayList<Player> natural_armor = new ArrayList<>();
    public ArrayList<Player> tailwind = new ArrayList<>();
    public ArrayList<Player> throw_ender_pearl = new ArrayList<>();
    public ArrayList<Player> translucent = new ArrayList<>();
    public ArrayList<Player> no_shield = new ArrayList<>();
    public ArrayList<Player> vegetarian = new ArrayList<>();
    public ArrayList<Player> velvet_paws = new ArrayList<>();
    public ArrayList<Player> weak_arms = new ArrayList<>();
    public ArrayList<Player> webbing = new ArrayList<>();
    public ArrayList<Player> water_vision = new ArrayList<>();
    public ArrayList<Player> elytra = new ArrayList<>();
    public ArrayList<Player> air_from_potions = new ArrayList<>();
    public ArrayList<Player> conduit_power_on_land = new ArrayList<>();
    public ArrayList<Player> damage_from_potions = new ArrayList<>();
    public ArrayList<Player> damage_from_snowballs = new ArrayList<>();
    public ArrayList<Player> ender_particles = new ArrayList<>();
    public ArrayList<Player> flame_particles = new ArrayList<>();
    public ArrayList<Player> no_cobweb_slowdown = new ArrayList<>();
    public ArrayList<Player> phantomize = new ArrayList<>();
    public ArrayList<Player> strong_arms_break_speed = new ArrayList<>();
    public ArrayList<Player> apply_effect = new ArrayList<>();
    public ArrayList<Player> effect_immunity = new ArrayList<>();
    public ArrayList<Player> attribute = new ArrayList<>();
    public ArrayList<Player> conditioned_attribute = new ArrayList<>();
    public ArrayList<Player> creative_flight = new ArrayList<>();
    public ArrayList<Player> burn = new ArrayList<>();
    public ArrayList<Player> restrict_armor = new ArrayList<>();
    public ArrayList<Player> dmg_invulnerable = new ArrayList<>();
    public ArrayList<Player> disable_regen = new ArrayList<>();
    public ArrayList<Player> entity_glow = new ArrayList<>();
    public ArrayList<Player> entity_group = new ArrayList<>();
    public ArrayList<Player> fire_projectile = new ArrayList<>();
    public ArrayList<Player> freeze = new ArrayList<>();
    public ArrayList<Player> grounded = new ArrayList<>();
    public ArrayList<Player> keep_inventory = new ArrayList<>();
    public ArrayList<Player> model_color = new ArrayList<>();
    public ArrayList<Player> night_vision = new ArrayList<>();
    public ArrayList<Player> overlay = new ArrayList<>();
    public ArrayList<Player> particle = new ArrayList<>();
    public ArrayList<Player> recipe = new ArrayList<>();
    public ArrayList<Player> self_glow = new ArrayList<>();
    public ArrayList<Player> simple = new ArrayList<>();
    public ArrayList<Player> stacking_status_effect = new ArrayList<>();
    public ArrayList<Player> starting_equip = new ArrayList<>();
    public ArrayList<Player> swimming = new ArrayList<>();
    public ArrayList<Player> toggle_night_vision = new ArrayList<>();
    public ArrayList<Player> toggle_power = new ArrayList<>();
    public ArrayList<Player> tooltip = new ArrayList<>();
    public ArrayList<Player> walk_on_fluid = new ArrayList<>();
    public ArrayList<Player> bioluminescent = new ArrayList<>();
    public ArrayList<Player> damage_over_time = new ArrayList<>();

    //actions
    public ArrayList<Player> action_on_being_used = new ArrayList<>();
    public ArrayList<Player> action_on_being_hit = new ArrayList<>();
    public ArrayList<Player> action_on_block_break = new ArrayList<>();
    public ArrayList<Player> action_on_block_use = new ArrayList<>();
    public ArrayList<Player> action_on_callback = new ArrayList<>();
    public ArrayList<Player> action_on_entity_use = new ArrayList<>();
    public ArrayList<Player> action_on_hit = new ArrayList<>();
    public ArrayList<Player> action_on_item_use = new ArrayList<>();
    public ArrayList<Player> action_on_land = new ArrayList<>();
    public ArrayList<Player> action_on_wake_up = new ArrayList<>();
    public ArrayList<Player> action_ove_time = new ArrayList<>();
    public ArrayList<Player> action_when_damage_taken = new ArrayList<>();
    public ArrayList<Player> action_when_hit = new ArrayList<>();
    public ArrayList<Player> active_self = new ArrayList<>();
    public ArrayList<Player> attacker_action_when_hit = new ArrayList<>();
    public ArrayList<Player> self_action_on_hit = new ArrayList<>();
    public ArrayList<Player> self_action_on_kill = new ArrayList<>();
    public ArrayList<Player> self_action_when_hit = new ArrayList<>();
    public ArrayList<Player> target_action_on_hit = new ArrayList<>();

    //TODO: yeah gotta come back to Attribute Modifier, and Item on Item(*crys*)

    //genesis
    public ArrayList<Player> hot_hands = new ArrayList<>();
    public ArrayList<Player> extra_fire = new ArrayList<>();
    public ArrayList<Player> bow_nope = new ArrayList<>();
    public ArrayList<Player> silk_touch = new ArrayList<>();
    public ArrayList<Player> explode_tick = new ArrayList<>();
    public ArrayList<Player> projectile_immune = new ArrayList<>();
    public ArrayList<Player> charged = new ArrayList<>();
    public ArrayList<Player> felinephobia = new ArrayList<>();
    public ArrayList<Player> fire_weak = new ArrayList<>();
    public ArrayList<Player> gold_armour_buff = new ArrayList<>();
    public ArrayList<Player> gold_item_buff = new ArrayList<>();
    public ArrayList<Player> big_leap_tick = new ArrayList<>();
    public ArrayList<Player> carrot_only = new ArrayList<>();
    public ArrayList<Player> jump_increased = new ArrayList<>();
    public ArrayList<Player> rabbit_drop_foot = new ArrayList<>();
    public ArrayList<Player> decreased_explosion = new ArrayList<>();
    public ArrayList<Player> creeper_head_death_drop = new ArrayList<>();
    public ArrayList<Player> resist_fall = new ArrayList<>();
    public ArrayList<Player> weak_biome_cold = new ArrayList<>();
    public ArrayList<Player> overworld_piglin_zombified = new ArrayList<>();
    public ArrayList<Player> attribute_modify_transfer = new ArrayList<>();
    public ArrayList<Player> no_gravity = new ArrayList<>();

}