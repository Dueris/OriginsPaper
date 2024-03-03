package me.dueris.genesismc.factory.powers;

import me.dueris.calio.registry.Registerable;
import me.dueris.genesismc.factory.powers.apoli.provider.PowerProvider;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public interface ApoliPower extends Registerable {

    default NamespacedKey getKey(){
        try {
            if(this instanceof PowerProvider && this.getClass().getDeclaredField("powerReference") != null){
                Field field = this.getClass().getDeclaredField("powerReference");
                field.setAccessible(true);

                return NamespacedKey.fromString(field.get(this).toString());
            }else{
                return NamespacedKey.fromString(getPowerFile());
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("An unhandled exception occurred when retrieving a key from a power");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    HashMap<Player, HashMap<String, Boolean>> powers_active = new HashMap<>();
    ArrayList<Player> game_event_listener = new ArrayList<>();
    ArrayList<Player> multiple = new ArrayList<>();
    ArrayList<Player> entity_set = new ArrayList<>();
    ArrayList<Player> resource = new ArrayList<>();
    ArrayList<Player> fall_immunity = new ArrayList<>();
    ArrayList<Player> climbing = new ArrayList<>();
    ArrayList<Player> fire_immunity = new ArrayList<>();
    ArrayList<Player> launch_into_air = new ArrayList<>();
    ArrayList<Player> water_breathing = new ArrayList<>();
    ArrayList<Player> shulker_inventory = new ArrayList<>();
    ArrayList<Player> invisibility = new ArrayList<>();
    ArrayList<Player> more_exhaustion = new ArrayList<>();
    ArrayList<Player> phasing = new ArrayList<>();
    ArrayList<Player> pumpkin_hate = new ArrayList<>();
    ArrayList<Player> extra_reach = new ArrayList<>();
    ArrayList<Player> extra_reach_attack = new ArrayList<>();
    ArrayList<Player> translucent = new ArrayList<>();
    ArrayList<Player> water_vision = new ArrayList<>();
    ArrayList<Player> elytra = new ArrayList<>();
    ArrayList<Player> apply_effect = new ArrayList<>();
    ArrayList<Player> effect_immunity = new ArrayList<>();
    ArrayList<Player> attribute = new ArrayList<>();
    ArrayList<Player> conditioned_attribute = new ArrayList<>();
    ArrayList<Player> creative_flight = new ArrayList<>();
    ArrayList<Player> burn = new ArrayList<>();
    ArrayList<Player> restrict_armor = new ArrayList<>();
    ArrayList<Player> dmg_invulnerable = new ArrayList<>();
    ArrayList<Player> disable_regen = new ArrayList<>();
    ArrayList<Player> entity_glow = new ArrayList<>();
    ArrayList<Player> entity_group = new ArrayList<>();
    ArrayList<Player> fire_projectile = new ArrayList<>();
    ArrayList<Player> freeze = new ArrayList<>();
    ArrayList<Player> grounded = new ArrayList<>();
    ArrayList<Player> keep_inventory = new ArrayList<>();
    ArrayList<Player> model_color = new ArrayList<>();
    ArrayList<Player> night_vision = new ArrayList<>();
    ArrayList<Player> overlay = new ArrayList<>();
    ArrayList<Player> particle = new ArrayList<>();
    ArrayList<Player> recipe = new ArrayList<>();
    ArrayList<Player> self_glow = new ArrayList<>();
    ArrayList<Player> simple = new ArrayList<>();
    ArrayList<Player> stacking_status_effect = new ArrayList<>();
    ArrayList<Player> starting_equip = new ArrayList<>();
    ArrayList<Player> swimming = new ArrayList<>();
    ArrayList<Player> toggle_night_vision = new ArrayList<>();
    ArrayList<Player> toggle_power = new ArrayList<>();
    ArrayList<Player> tooltip = new ArrayList<>();
    ArrayList<Player> walk_on_fluid = new ArrayList<>();
    ArrayList<Player> bioluminescent = new ArrayList<>();
    ArrayList<Player> damage_over_time = new ArrayList<>();
    //actions
    ArrayList<Player> action_on_being_used = new ArrayList<>();
    ArrayList<Player> action_on_block_break = new ArrayList<>();
    ArrayList<Player> action_on_block_place = new ArrayList<>();
    ArrayList<Player> action_on_block_use = new ArrayList<>();
    ArrayList<Player> action_on_callback = new ArrayList<>();
    ArrayList<Player> action_on_entity_use = new ArrayList<>();
    ArrayList<Player> action_on_hit = new ArrayList<>();
    ArrayList<Player> action_on_death = new ArrayList<>();
    ArrayList<Player> action_on_item_use = new ArrayList<>();
    ArrayList<Player> action_on_land = new ArrayList<>();
    ArrayList<Player> action_on_wake_up = new ArrayList<>();
    ArrayList<Player> action_ove_time = new ArrayList<>();
    ArrayList<Player> action_when_damage_taken = new ArrayList<>();
    ArrayList<Player> action_when_hit = new ArrayList<>();
    ArrayList<Player> active_self = new ArrayList<>();
    ArrayList<Player> attacker_action_when_hit = new ArrayList<>();
    ArrayList<Player> self_action_on_hit = new ArrayList<>();
    ArrayList<Player> self_action_on_kill = new ArrayList<>();
    ArrayList<Player> self_action_when_hit = new ArrayList<>();
    ArrayList<Player> target_action_on_hit = new ArrayList<>();
    //genesis
    ArrayList<Player> bow_nope = new ArrayList<>();
    ArrayList<Player> silk_touch = new ArrayList<>();
    ArrayList<Player> explode_tick = new ArrayList<>();
    ArrayList<Player> big_leap_tick = new ArrayList<>();
    ArrayList<Player> attribute_modify_transfer = new ArrayList<>();
    ArrayList<Player> no_gravity = new ArrayList<>();

    void run(Player p);

    String getPowerFile();

    ArrayList<Player> getPowerArray();

    void setActive(Player p, String tag, Boolean bool);

}