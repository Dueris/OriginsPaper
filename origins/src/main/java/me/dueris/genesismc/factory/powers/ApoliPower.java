package me.dueris.genesismc.factory.powers;

import me.dueris.calio.registry.Registrable;
import me.dueris.genesismc.factory.powers.apoli.provider.PowerProvider;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.Reflector;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public interface ApoliPower extends Registrable {

	HashMap<Player, HashMap<String, Boolean>> powers_active = new HashMap<>();
	ArrayList<Player> game_event_listener = new ArrayList<>();
	ArrayList<Player> cooldown = new ArrayList<>();
	ArrayList<Player> multiple = new ArrayList<>();
	ArrayList<Player> entity_set = new ArrayList<>();
	ArrayList<Player> edible_item = new ArrayList<>();
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
	ArrayList<Player> replace_loot_table = new ArrayList<>();
	ArrayList<Player> translucent = new ArrayList<>();
	ArrayList<Player> water_vision = new ArrayList<>();
	ArrayList<Player> elytra = new ArrayList<>();
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
	ArrayList<Player> damage_over_time = new ArrayList<>();
	ArrayList<Player> action_on_being_used = new ArrayList<>();
	ArrayList<Player> action_on_item_pickup = new ArrayList<>();
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
	ArrayList<Player> attribute_modify_transfer = new ArrayList<>();
	ArrayList<Player> no_gravity = new ArrayList<>();
	ArrayList<Player> item_on_item = new ArrayList<>();
	ArrayList<Player> modify_air_speed = new ArrayList<>();
	ArrayList<Player> modify_block_render = new ArrayList<>();
	ArrayList<Player> modify_break_speed = new ArrayList<>();
	ArrayList<Player> modify_crafting = new ArrayList<>();
	ArrayList<Player> modify_damage_dealt = new ArrayList<>();
	ArrayList<Player> modify_damage_taken = new ArrayList<>();
	ArrayList<Player> modify_exhaustion = new ArrayList<>();
	ArrayList<Player> modify_falling = new ArrayList<>();
	ArrayList<Player> modify_food = new ArrayList<>();
	ArrayList<Player> modify_harvest = new ArrayList<>();
	ArrayList<Player> modify_healing = new ArrayList<>();
	ArrayList<Player> modify_jump = new ArrayList<>();
	ArrayList<Player> modify_lava_speed = new ArrayList<>();
	ArrayList<Player> modify_world_spawn = new ArrayList<>();
	ArrayList<Player> modify_projectile_damage = new ArrayList<>();
	ArrayList<Player> modify_effect_amplifier = new ArrayList<>();
	ArrayList<Player> modify_effect_duration = new ArrayList<>();
	ArrayList<Player> modify_enchantment_level = new ArrayList<>();
	ArrayList<Player> modify_swim_speed = new ArrayList<>();
	ArrayList<Player> modify_velocity = new ArrayList<>();
	ArrayList<Player> modify_xp_gain = new ArrayList<>();

	default NamespacedKey getKey() {
		try {
			if (this instanceof PowerProvider && this.getClass().getDeclaredField("powerReference") != null) {
				String refrence = Reflector.accessField("powerReference", this.getClass(), this, NamespacedKey.class).asString();
				return NamespacedKey.fromString(refrence);
			} else {
				return NamespacedKey.fromString(getType());
			}
		} catch (NoSuchFieldException e) {
			throw new RuntimeException("An unhandled exception occurred when retrieving a key from a power");
		}
	}

	default void run(Player p, Power power) {
	}

	default void runAsync(Player p, Power power) {
	}

	default void doesntHavePower(Player p) {
	}

	default void bukkitRunnable() {
	}

	String getType();

	ArrayList<Player> getPlayersWithPower();

	default void setActive(Player p, String tag, Boolean bool) {
		if (powers_active.containsKey(p)) {
			if (powers_active.get(p).containsKey(tag)) {
				powers_active.get(p).replace(tag, bool);
			} else {
				powers_active.get(p).put(tag, bool);
			}
		} else {
			powers_active.put(p, new HashMap());
			setActive(p, tag, bool);
		}
	}

}