package me.dueris.genesismc.core.utils;

import java.io.Serializable;
import java.util.HashMap;

public class PowerContainer implements Serializable {

    String powerTag;
    HashMap<String, Object> powerFile;
    String powerSource;


    public PowerContainer(String powerTag, HashMap<String, Object> powerFile, String powerSource) {
        this.powerTag = powerTag;
        this.powerFile = powerFile;
        this.powerSource = powerSource;
    }

    public String getPowerTag() {
        return this.powerTag;
    }

    public HashMap<String, Object> getPowerFile() {
        return this.powerFile;
    }

    public String getPowerSource() {
        return this.powerSource;
    }


    /**
     * @return The powerContainer formatted for debugging, not to be used in other circumstances.
     */
    public String toString() {
        return "powerTag: " + this.powerTag + ", PowerFile: " + this.powerFile.toString() + ", PowerSource: " + this.powerSource.toString();
    }


    public String getName() {
        if (this.powerTag.equals("origins:fall_immunity")) return "Acrobatics";
        if (this.powerTag.equals("origins:aerial_combatant")) return "Aerial Combatant";
        if (this.powerTag.equals("origins:aqua_affinity")) return "Aqua Affinity";
        if (this.powerTag.equals("origins:aquatic")) return "Hidden";
        if (this.powerTag.equals("origins:arthropod")) return "Hidden";
        if (this.powerTag.equals("origins:more_kinetic_damage")) return "Brittle Bones";
        if (this.powerTag.equals("origins:burning_wrath")) return "Burning Wrath";
        if (this.powerTag.equals("origins:carnivore")) return "Carnivore";
        if (this.powerTag.equals("origins:scare_creepers")) return "Catlike Appearance";
        if (this.powerTag.equals("origins:claustrophobia")) return "Claustrophobia";
        if (this.powerTag.equals("origins:climbing")) return "Climbing";
        if (this.powerTag.equals("origins:hunger_over_time")) return "Fast Metabolism";
        if (this.powerTag.equals("origins:slow_falling")) return "Featherweight";
        if (this.powerTag.equals("origins:swim_speed")) return "Fins";
        if (this.powerTag.equals("origins:fire_immunity")) return "Fire Immunity";
        if (this.powerTag.equals("origins:fragile")) return "Fragile";
        if (this.powerTag.equals("origins:fresh_air")) return "Fresh Air";
        if (this.powerTag.equals("origins:launch_into_air")) return "Gift of the Winds";
        if (this.powerTag.equals("origins:water_breathing")) return "Gills";
        if (this.powerTag.equals("origins:shulker_inventory")) return "Hoarder";
        if (this.powerTag.equals("origins:hotblooded")) return "Hotblooded";
        if (this.powerTag.equals("origins:water_vulnerability")) return "Hydrophobia";
        if (this.powerTag.equals("origins:invisibility")) return "Invisibility";
        if (this.powerTag.equals("origins:more_exhaustion")) return "Large Appetite";
        if (this.powerTag.equals("origins:like_air")) return "Like Air";
        if (this.powerTag.equals("origins:like_water")) return "Like Water";
        if (this.powerTag.equals("origins:master_of_webs")) return "Master of Webs";
        if (this.powerTag.equals("origins:light_armor")) return "Need for Mobility";
        if (this.powerTag.equals("origins:nether_spawn")) return "Nether Inhabitant";
        if (this.powerTag.equals("origins:nine_lives")) return "Nine Lives";
        if (this.powerTag.equals("origins:cat_vision")) return "Nocturnal";
        if (this.powerTag.equals("origins:lay_eggs")) return "Oviparous";
        if (this.powerTag.equals("origins:phasing")) return "Phasing";
        if (this.powerTag.equals("origins:burn_in_daylight")) return "Photoallergic";
        if (this.powerTag.equals("origins:arcane_skin")) return "Arcane Skin";
        if (this.powerTag.equals("origins:end_spawn")) return "End Inhabitant";
        if (this.powerTag.equals("origins:phantomize_overlay")) return "Hidden";
        if (this.powerTag.equals("origins:pumpkin_hate")) return "Scared of Gourds";
        if (this.powerTag.equals("origins:extra_reach")) return "Slender Body";
        if (this.powerTag.equals("origins:sprint_jump")) return "Strong Ankles";
        if (this.powerTag.equals("origins:strong_arms")) return "Strong Arms";
        if (this.powerTag.equals("origins:natural_armor")) return "Sturdy Skin";
        if (this.powerTag.equals("origins:tailwind")) return "Tailwind";
        if (this.powerTag.equals("origins:throw_ender_pearl")) return "Teleportation";
        if (this.powerTag.equals("origins:translucent")) return "Translucent";
        if (this.powerTag.equals("origins:no_shield")) return "Unwieldy";
        if (this.powerTag.equals("origins:vegetarian")) return "Vegetarian";
        if (this.powerTag.equals("origins:velvet_paws")) return "Velvet Paws";
        if (this.powerTag.equals("origins:weak_arms")) return "Weak Arms";
        if (this.powerTag.equals("origins:webbing")) return "Webbing";
        if (this.powerTag.equals("origins:water_vision")) return "Wet Eyes";
        if (this.powerTag.equals("origins:elytra")) return "Winged";
        if (this.powerTag.equals("origins:air_from_potions")) return "Hidden";
        if (this.powerTag.equals("origins:conduit_power_on_land")) return "Hidden";
        if (this.powerTag.equals("origins:damage_from_potions")) return "Hidden";
        if (this.powerTag.equals("origins:damage_from_snowballs")) return "Hidden";
        if (this.powerTag.equals("origins:ender_particles")) return "Hidden";
        if (this.powerTag.equals("origins:flame_particles")) return "Hidden";
        if (this.powerTag.equals("origins:no_cobweb_slowdown")) return "Hidden";
        if (this.powerTag.equals("origins:phantomize")) return "Phantom Form";
        if (this.powerTag.equals("origins:strong_arms_break_speed")) return "Hidden";

        if (this.powerTag.equals("genesis:hot_hands")) return "Hot Hands";
        if (this.powerTag.equals("genesis:extra_fire_tick")) return "Flammable";
        if (this.powerTag.equals("genesis:silk_touch")) return "Delicate Touch";
        if (this.powerTag.equals("genesis:bow_inability")) return "Horrible Coordination";

        Object name = this.powerFile.get("name");
        if (name == null) return "No Name";
        return (String) name;
    }

    public String getDesription() {
        if (this.powerTag.equals("origins:fall_immunity")) return "You never take fall damage, no matter from which height you fall.";
        if (this.powerTag.equals("origins:aerial_combatant")) return "You deal substantially more damage while in Elytra flight.";
        if (this.powerTag.equals("origins:aqua_affinity")) return "You may break blocks underwater as others do on land.";
        if (this.powerTag.equals("origins:aquatic")) return "Hidden";
        if (this.powerTag.equals("origins:arthropod")) return "Hidden";
        if (this.powerTag.equals("origins:more_kinetic_damage")) return "You take more damage from falling and flying into blocks.";
        if (this.powerTag.equals("origins:burning_wrath")) return "When on fire, you deal additional damage with your attacks.";
        if (this.powerTag.equals("origins:carnivore")) return "Your diet is restricted to meat, you can't eat vegetables.";
        if (this.powerTag.equals("origins:scare_creepers")) return "Creepers are scared of you and will only explode if you attack them first.";
        if (this.powerTag.equals("origins:claustrophobia")) return "Being somewhere with a low ceiling for too long will weaken you ad make you slower.";
        if (this.powerTag.equals("origins:climbing")) return "You are able to climb up any kind of wall, just not ladders.";
        if (this.powerTag.equals("origins:hunger_over_time")) return "Being phantomized causes you to become hungry";
        if (this.powerTag.equals("origins:slow_falling")) return "You fall as gently to the ground as a feather would, unless you sneak.";
        if (this.powerTag.equals("origins:swim_speed")) return "Your underwater speed is increased.";
        if (this.powerTag.equals("origins:fire_immunity")) return "You are immune to all types of fire damage.";
        if (this.powerTag.equals("origins:fragile")) return "You have 3 less hearts of health than humans.";
        if (this.powerTag.equals("origins:fresh_air")) return "When sleeping, your bed needs to be at an altitude of at least 86 blocks, so you can breathe fresh air.";
        if (this.powerTag.equals("origins:launch_into_air")) return "Every 30 seconds, you are able to launch about 20 blocks up into the air";
        if (this.powerTag.equals("origins:water_breathing")) return "You can breathe underwater, but not on land";
        if (this.powerTag.equals("origins:shulker_inventory")) return "You have access ti an additional 9 slots of inventory, which keep the items on death.";
        if (this.powerTag.equals("origins:hotblooded")) return "Due to your hot body, venom's burn up, making you immune to poison and hunger status effects.";
        if (this.powerTag.equals("origins:water_vulnerability")) return "You receive damage over time while in contact with water.";
        if (this.powerTag.equals("origins:invisibility")) return "While phantomized, you are invisible.";
        if (this.powerTag.equals("origins:more_exhaustion")) return "You exhaust much quicker than others., thus requiring you to eat more.";
        if (this.powerTag.equals("origins:like_air")) return "Modifiers to your walking speed also apply while you're airborne.";
        if (this.powerTag.equals("origins:like_water")) return "When underwater, you do not sink to the ground unless you want to.";
        if (this.powerTag.equals("origins:master_of_webs")) return "You navigate cobwebs perfectly, and are able to climb in them. When you hit an enemy in melee, they get stuck in cobweb for a while. Non-arthropods stuck in cobweb will be sensed by you. You are able to craft cobwebs from string.";
        if (this.powerTag.equals("origins:light_armor")) return "You can not wear any heavy armour (armour with protection values higher than chainmail).";
        if (this.powerTag.equals("origins:nether_spawn")) return "Your natural spawn will be in the Nether.";
        if (this.powerTag.equals("origins:nine_lives")) return "You have 1 less heart of health than humans.";
        if (this.powerTag.equals("origins:cat_vision")) return "you can slightly see in the dark when not in water.";
        if (this.powerTag.equals("origins:lay_eggs")) return "Whenever you wake up in the morning, you will lay an egg.";
        if (this.powerTag.equals("origins:phasing")) return "While phantomized, you can walk though solid material, expect Obsidian";
        if (this.powerTag.equals("origins:burn_in_daylight")) return "You begin to burn in daylight if you are not invisible.";
        if (this.powerTag.equals("origins:arcane_skin")) return "Your skin is a dark blue hue";
        if (this.powerTag.equals("origins:end_spawn")) return "Your journey begins in the End";
        if (this.powerTag.equals("origins:phantomize_overlay")) return "Hidden";
        if (this.powerTag.equals("origins:pumpkin_hate")) return "You are afraid of pumpkins. For a good reason.";
        if (this.powerTag.equals("origins:extra_reach")) return "You can reach blocks and entities further away.";
        if (this.powerTag.equals("origins:sprint_jump")) return "You are able to jump higher by jumping while sprinting.";
        if (this.powerTag.equals("origins:strong_arms")) return "You are strong enough to break natural stones without using a pickaxe.";
        if (this.powerTag.equals("origins:natural_armor")) return "Even without wearing armor, your skin provides natural protection.";
        if (this.powerTag.equals("origins:tailwind")) return "You are a little bit quicker on foot than others.";
        if (this.powerTag.equals("origins:throw_ender_pearl")) return "Whenever you want, you may throw an ender pearl, which deals no damage, allowing you to teleport.";
        if (this.powerTag.equals("origins:translucent")) return "Your skin is translucent.";
        if (this.powerTag.equals("origins:no_shield")) return "The way your hands are formed provides no way of holding a shield upright.";
        if (this.powerTag.equals("origins:vegetarian")) return "You can't digest any meat";
        if (this.powerTag.equals("origins:velvet_paws")) return "Your footsteps don't cause any vibrations which could otherwise be picked up by nearby lifeforms.";
        if (this.powerTag.equals("origins:weak_arms")) return "When no under the effect of a strength potion, you can only mine natural stone if there are at most 2 other natural stone blocks adjacent to it.";
        if (this.powerTag.equals("origins:webbing")) return "When you hit an enemy in melee, they get stuck in cobwebs.";
        if (this.powerTag.equals("origins:water_vision")) return "Your vision underwater is perfect.";
        if (this.powerTag.equals("origins:elytra")) return "You have Elytra wings without needing to equip any";
        if (this.powerTag.equals("origins:air_from_potions")) return "Hidden";
        if (this.powerTag.equals("origins:conduit_power_on_land")) return "Hidden";
        if (this.powerTag.equals("origins:damage_from_potions")) return "Hidden";
        if (this.powerTag.equals("origins:damage_from_snowballs")) return "Hidden";
        if (this.powerTag.equals("origins:ender_particles")) return "Hidden";
        if (this.powerTag.equals("origins:flame_particles")) return "Hidden";
        if (this.powerTag.equals("origins:no_cobweb_slowdown")) return "Hidden";
        if (this.powerTag.equals("origins:phantomize")) return "You can switch between human and phantom form at wil, but only while you are saturated enough to sprint.";
        if (this.powerTag.equals("origins:strong_arms_break_speed")) return "Hidden";

        if (this.powerTag.equals("genesis:hot_hands")) return "Your punches set mobs alight.";
        if (this.powerTag.equals("genesis:extra_fire_tick")) return "You take 50% more damage from fire";
        if (this.powerTag.equals("genesis:silk_touch")) return "You have silk touch hands";
        if (this.powerTag.equals("genesis:bow_inability")) return "You are not able to use a bow, you are WAY too clumsy";

        Object description = this.powerFile.get("name");
        if (description == null) return "No Description.";
        return (String) description;
    }

    public Boolean getHidden() {
        if (this.powerTag.equals("origins:aquatic")) return true;
        if (this.powerTag.equals("origins:arthropod")) return true;
        if (this.powerTag.equals("origins:phantomize_overlay")) return true;
        if (this.powerTag.equals("origins:air_from_potions")) return true;
        if (this.powerTag.equals("origins:conduit_power_on_land")) return true;
        if (this.powerTag.equals("origins:damage_from_potions")) return true;
        if (this.powerTag.equals("origins:damage_from_snowballs")) return true;
        if (this.powerTag.equals("origins:ender_particles")) return true;
        if (this.powerTag.equals("origins:flame_particles")) return true;
        if (this.powerTag.equals("origins:no_cobweb_slowdown")) return true;
        if (this.powerTag.equals("origins:strong_arms_break_speed")) return true;

        Object hidden = (Boolean) powerFile.get("hidden");
        if (hidden == null) return false;
        return (Boolean) hidden;
    }

//    public void add(String powerTag, HashMap<String, Object> powerFile, String powerSource) {
//        this.powerTag.add(powerTag);
//        this.powerFile.add(powerFile);
//        this.powerSource.add(powerSource);
//    }
//
//    public void removeByTag(String powerTag) {
//        int index = this.powerTag.indexOf(powerTag);
//        this.powerTag.remove(index);
//        this.powerFile.remove(index);
//        this.powerSource.remove(index);
//    }
//
//    public void removeByFile(HashMap<String, String> powerFile) {
//        int index = this.powerFile.indexOf(powerFile);
//        this.powerTag.remove(index);
//        this.powerFile.remove(index);
//        this.powerSource.remove(index);
//    }
//
//    public void removeBySource(String powerSource) {
//        int index = this.powerSource.indexOf(powerSource);
//        this.powerTag.remove(index);
//        this.powerFile.remove(index);
//        this.powerSource.remove(index);
//    }

//    public HashMap<String, Object> getPowerFileFromTag(String powerTag) {
//        return this.powerFile.get(this.powerTag.indexOf(powerTag));
//    }
}
