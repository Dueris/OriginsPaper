package me.dueris.genesismc.core.utils;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.files.GenesisDataFiles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Lang {
    public static String lang_test = getLocalizedString("lang.test");
    //commands
    public static String command_origin_set_given = getLocalizedString("command.origin.set.given");
    public static String command_origin_set_sender = getLocalizedString("command.origin.set.sender");
    public static String command_origin_get_sender = getLocalizedString("command.origin.get.sender");
    public static String command_origin_gui_sender = getLocalizedString("command.origin.gui.sender");
    public static String command_origin_has_sender_failed = getLocalizedString("command.origin.has.sender.failed");
    public static String command_origin_has_sender_passed = getLocalizedString("command.origin.has.sender.passed");
    public static String command_layer_get_sender = getLocalizedString("command.layer.get.sender");
    public static String command_layer_set_given = getLocalizedString("command.layer.set.given");
    public static String command_layer_add_given = getLocalizedString("command.layer.add.given");
    public static String command_layer_remove_given = getLocalizedString("command.layer.remove.given");
    public static String command_toggle_on = getLocalizedString("command.toggle.on");
    public static String command_toggle_off = getLocalizedString("command.toggle.off");
    //menu
    public static String menu_original_human_nothing_title = getLocalizedString("menu.original.human.nothing.title");
    public static String menu_original_human_nothing_description = getLocalizedString("menu.original.human.nothing.description");
    public static String menu_original_avian_featherweight_title = getLocalizedString("menu.original.avian.featherweight.title");
    public static String menu_original_avian_featherweight_description = getLocalizedString("menu.original.avian.featherweight.description");
    public static String menu_original_avian_tailwind_title = getLocalizedString("menu.original.avian.tailwind.title");
    public static String menu_original_avian_tailwind_description = getLocalizedString("menu.original.avian.tailwind.description");
    public static String menu_original_avian_oviparous_title = getLocalizedString("menu.original.avian.oviparous.title");
    public static String menu_original_avian_oviparous_description = getLocalizedString("menu.original.oviparous.description");
    public static String menu_original_avian_vegitarian_title = getLocalizedString("menu.original.vegitarian.title");
    public static String menu_original_avian_vegitarian_description = getLocalizedString("menu.original.vegitarian.description");
    public static String menu_original_avian_freshair_title = getLocalizedString("menu.original.avian.fresh_air.title");
    public static String menu_original_avian_freshair_description = getLocalizedString("menu.original.avian.fresh_air.description");
    public static String menu_original_arachnid_spiderman_title = getLocalizedString("menu.original.arachnid.spiderman.title");
    public static String menu_original_arachnid_spiderman_description = getLocalizedString("menu.original.arachnid.spiderman.description");
    public static String menu_original_arachnid_weaver_title = getLocalizedString("menu.original.arachnid.weaver.title");
    public static String menu_original_arachnid_weaver_description = getLocalizedString("menu.original.arachnid.weaver.description");
    public static String menu_original_arachnid_squishable_title = getLocalizedString("menu.original.arachnid.squishable.title");
    public static String menu_original_arachnid_squishable_description = getLocalizedString("menu.original.arachnid.squishable.description");
    public static String menu_original_arachnid_tinycarnivore_title = getLocalizedString("menu.original.arachnid.tiny_carnivore.title");
    public static String menu_original_arachnid_tinycarnivore_description = getLocalizedString("menu.original.arachnid.tiny_carnivore.description");
    public static String menu_original_elytrian_winged_title = getLocalizedString("menu.original.elytrian.winged.title");
    public static String menu_original_elytrian_winged_description = getLocalizedString("menu.original.elytrian.winged.description");
    public static String menu_original_elytrian_giftwinds_title = getLocalizedString("menu.original.elytrian.gift_of_the_winds.title");
    public static String menu_original_elytrian_giftwinds_description = getLocalizedString("menu.original.elytrian.gift_of_the_winds.description");
    public static String menu_original_elytrian_claustrophobia_title = getLocalizedString("menu.original.elytrian.claustrophobia.title");
    public static String menu_original_elytrian_claustrophobia_description = getLocalizedString("menu.original.elytrian.claustrophobia.description");
    public static String menu_original_elytrian_mobility_title = getLocalizedString("menu.original.elytrian.need_for_mobility.title");
    public static String menu_original_elytrian_mobility_description = getLocalizedString("menu.original.elytrian.need_for_mobility.description");
    public static String menu_original_elytrian_bones_title = getLocalizedString("menu.original.elytrian.brittle_bones.title");
    public static String menu_original_elytrian_bones_description = getLocalizedString("menu.original.elytrian.brittle_bones.description");
    public static String menu_original_shulk_hoarder_title = getLocalizedString("menu.original.shulk.hoarder.title");
    public static String menu_original_shulk_hoarder_description = getLocalizedString("menu.original.shulk.hoarder.description");
    public static String menu_original_shulk_sturdy_title = getLocalizedString("menu.original.shulk.sturdy_skin.title");
    public static String menu_original_shulk_sturdy_description = getLocalizedString("menu.original.shulk.sturdy_skin.description");
    public static String menu_original_shulk_strong_title = getLocalizedString("menu.original.shulk.strong_arms.title");
    public static String menu_original_shulk_strong_description = getLocalizedString("menu.original.shulk.strong_arms.description");
    public static String menu_original_shulk_unweildy_title = getLocalizedString("menu.original.shulk.unweildy.title");
    public static String menu_original_shulk_unweildy_description = getLocalizedString("menu.original.shulk.unweildy.description");
    public static String menu_original_shulk_large_title = getLocalizedString("menu.original.shulk.large_appetite.title");
    public static String menu_original_shulk_large_description = getLocalizedString("menu.original.shulk.large_appetite.description");
    public static String menu_original_enderian_teleport_title = getLocalizedString("menu.original.enderian.teleportation.title");
    public static String menu_original_enderian_teleport_description = getLocalizedString("menu.original.enderian.teleportation.description");
    public static String menu_original_enderian_hydro_title = getLocalizedString("menu.original.enderian.hydrophobia.title");
    public static String menu_original_enderian_hydro_description = getLocalizedString("menu.original.enderian.hydrophobia.description");
    public static String menu_original_enderian_touch_title = getLocalizedString("menu.original.enderian.delicate_touch.title");
    public static String menu_original_enderian_touch_description = getLocalizedString("menu.original.enderian.delicate_touch.description");
    public static String menu_original_enderian_slender_title = getLocalizedString("menu.original.enderian.slender_body.title");
    public static String menu_original_enderian_slender_description = getLocalizedString("menu.original.enderian.slender_body.description");
    public static String menu_original_enderian_pearls_title = getLocalizedString("menu.original.enderian.bearer_of_pearls.title");
    public static String menu_original_enderian_pearls_description = getLocalizedString("menu.original.enderian.bearer_of_pearls.description");
    public static String menu_original_merling_gills_title = getLocalizedString("menu.original.merling.gills.title");
    public static String menu_original_merling_gills_description = getLocalizedString("menu.original.merling.gills.descriptions");
    public static String menu_original_merling_eyes_title = getLocalizedString("menu.original.merling.wet_eyes.title");
    public static String menu_original_merling_eyes_description = getLocalizedString("menu.original.merling.wet_eyes.description");
    public static String menu_original_merling_forces_title = getLocalizedString("menu.original.merling.opposing_forces.title");
    public static String menu_original_merling_forces_description = getLocalizedString("menu.original.merling.opposing_forces.description");
    public static String menu_original_merling_fins_title = getLocalizedString("menu.original.merling.fins.title");
    public static String menu_original_merling_fins_description = getLocalizedString("menu.original.merling.fins.description");
    public static String menu_original_merling_dont_title = getLocalizedString("menu.original.merling.please_dont.title");
    public static String menu_original_merling_dont_description = getLocalizedString("menu.original.merling.please_dont.description");
    public static String menu_original_merling_luck_title = getLocalizedString("menu.original.merling.luck_of_the_sea.title");
    public static String menu_original_merling_luck_description = getLocalizedString("menu.original.merling.luck_of_the_sea.description");
    public static String menu_original_blazeborn_born_title = getLocalizedString("menu.original.blazeborn.born_from_flames.title");
    public static String menu_original_blazeborn_born_description = getLocalizedString("menu.original.blazeborn.born_from_flames.description");
    public static String menu_original_blazeborn_wrath_title = getLocalizedString("menu.original.blazeborn.burning_wrath.title");
    public static String menu_original_blazeborn_wrath_description = getLocalizedString("menu.original.blazeborn.burning_wrath.description");
    public static String menu_original_blazeborn_immunity_title = getLocalizedString("menu.original.blazeborn.fire_immunity.title");
    public static String menu_original_blazeborn_immunity_description = getLocalizedString("menu.original.blazeborn.fire_immunity.description");
    public static String menu_original_blazeborn_hotwater_title = getLocalizedString("menu.original.blazeborn.to_hot_for_water.title");
    public static String menu_original_blazeborn_hotwater_description = getLocalizedString("menu.original.blazeborn.to_hot_for_water.description");
    public static String menu_original_blazeborn_hotblooded_title = getLocalizedString("menu.original.blazeborn.hotblooded.title");
    public static String menu_original_blazeborn_hotblooded_description = getLocalizedString("menu.original.blazeborn.hotblooded.description");
    public static String menu_original_blazeborn_forces_title = getLocalizedString("menu.original.blazeborn.opposite_forces.title");
    public static String menu_original_blazeborn_forces_description = getLocalizedString("menu.original.blazeborn.opposite_forces.description");
    public static String menu_original_blazeborn_nether_title = getLocalizedString("menu.original.blazeborn.flames_of_nether.title");
    public static String menu_original_blazeborn_nether_description = getLocalizedString("menu.original.blazeborn.flames_of_nether.description");
    public static String menu_original_phantom_translucent_title = getLocalizedString("menu.original.phantom.translucent.title");
    public static String menu_original_phantom_translucent_description = getLocalizedString("menu.original.phantom.translucent.description");
    public static String menu_original_phantom_vampire_title = getLocalizedString("menu.original.phantom.not_vampire.title");
    public static String menu_original_phantom_vampire_description = getLocalizedString("menu.original.phantom.not_vampire.description");
    public static String menu_original_phantom_phasing_title = getLocalizedString("menu.original.phantom.phasing.title");
    public static String menu_original_phantom_phasing_description = getLocalizedString("menu.original.phantom.phasing.description");
    public static String menu_original_phantom_metabolism_title = getLocalizedString("menu.original.phantom.fast_metabolism.title");
    public static String menu_original_phantom_metabolism_description = getLocalizedString("menu.original.phantom.fast_metabolism.description");
    public static String menu_original_phantom_creature_title = getLocalizedString("menu.original.phantom.fragile_creature.title");
    public static String menu_original_phantom_creature_description = getLocalizedString("menu.original.phantom.fragile_creature.description");
    public static String menu_original_phantom_invis_title = getLocalizedString("menu.original.phantom.invisibility.title");
    public static String menu_original_phantom_invis_description = getLocalizedString("menu.original.phantom.invisibility.description");
    public static String menu_original_feline_nine_title = getLocalizedString("menu.original.feline.nine_lives.title");
    public static String menu_original_feline_nine_description = getLocalizedString("menu.original.feline.nine_lives.description");
    public static String menu_original_feline_jumper_title = getLocalizedString("menu.original.feline.good_jumper.title");
    public static String menu_original_feline_nocturnal_title = getLocalizedString("menu.original.feline.nocturnal.title");
    public static String menu_original_feline_jumper_description = getLocalizedString("menu.original.feline.good_jumper.description");
    public static String menu_original_feline_nocturnal_description = getLocalizedString("menu.original.feline.nocturnal.description");
    public static String menu_original_feline_catlike_title = getLocalizedString("menu.original.feline.catlike_appearance.title");
    public static String menu_original_feline_catlike_description = getLocalizedString("menu.original.feline.catlike_appearance.description");
    public static String menu_original_feline_velvet_title = getLocalizedString("menu.original.feline.velvet_paws.title");
    public static String menu_original_feline_velvet_description = getLocalizedString("menu.original.feline.velvet_paws.description");
    public static String menu_original_feline_acrobatics_title = getLocalizedString("menu.original.feline.acrobatics.title");
    public static String menu_original_feline_acrobatics_description = getLocalizedString("menu.original.feline.acrobatics.description");
    public static String menu_original_feline_ankles_title = getLocalizedString("menu.original.feline.strong_ankles.title");
    public static String menu_original_feline_ankles_description = getLocalizedString("menu.original.feline.strong_ankles.description");
    public static String menu_expanded_starborne_wanderer_title = getLocalizedString("menu.expanded.starborne.wanderer.title");
public static String menu_expanded_starborne_wanderer_description = getLocalizedString("menu.expanded.starborne.wanderer.description");
public static String menu_expanded_starborne_shooting_title = getLocalizedString("menu.expanded.starborne.shooting_star.title");
public static String menu_expanded_starborne_shooting_description = getLocalizedString("menu.expanded.starborne.shooting_star.description");
public static String menu_expanded_starborne_falling_title = getLocalizedString("menu.expanded.starborne.falling_stars.title");
public static String menu_expanded_starborne_falling_description = getLocalizedString("menu.expanded.starborne.falling_stars.description");
public static String menu_expanded_starborne_supernova_title = getLocalizedString("menu.expanded.starborne.supernova.title");
public static String menu_expanded_starborne_supernova_description = getLocalizedString("menu.expanded.starborne.supernova.description");
public static String menu_expanded_starborne_vacuum_title = getLocalizedString("menu.expanded.starborne.cold_vacuum.title");
public static String menu_expanded_starborne_vacuum_description = getLocalizedString("menu.expanded.starborne.cold_vacuum.description");
public static String menu_expanded_starborne_stargazer_title = getLocalizedString("menu.expanded.starborne.stargazer.title");
public static String menu_expanded_starborne_stargazer_description = getLocalizedString("menu.expanded.starborne.stargazer.description");
public static String menu_expanded_starborne_realms_title = getLocalizedString("menu.expanded.starborne.unknown_realms.title");
public static String menu_expanded_starborne_realms_description = getLocalizedString("menu.expanded.starborne.unknown_realms.description");
public static String menu_expanded_starborne_nonviolent_title = getLocalizedString("menu.expanded.starborne.nonviolent.title");
public static String menu_expanded_starborne_nonviolent_description = getLocalizedString("menu.expanded.starborne.nonviolent.description");
public static String menu_expanded_starborne_guardian_title = getLocalizedString("menu.expanded.starborne.gaurdian.title");
public static String menu_expanded_starborne_guardian_description = getLocalizedString("menu.expanded.starborne.gaurdian.description");
public static String menu_expanded_allay_fairy_title = getLocalizedString("menu.expanded.alllay.little_fairy.title");
public static String menu_expanded_allay_fairy_description = getLocalizedString("menu.expanded.alllay.little_fairy.description");
public static String menu_expanded_allay_blue_title = getLocalizedString("menu.expanded.alllay.blue_spirit.title");
public static String menu_expanded_allay_blue_description = getLocalizedString("menu.expanded.alllay.blue_spirit.description");
public static String menu_expanded_allay_music_title = getLocalizedString("menu.expanded.alllay.sound_of_music.title");
public static String menu_expanded_allay_music_description = getLocalizedString("menu.expanded.alllay.sound_of_music.description");
public static String menu_expanded_allay_cookies_title = getLocalizedString("menu.expanded.alllay.cookies.title");
public static String menu_expanded_allay_cookies_description = getLocalizedString("menu.expanded.alllay.cookies.description");
public static String menu_expanded_allay_finder_title = getLocalizedString("menu.expanded.alllay.treasure_finder.title");
public static String menu_expanded_allay_finder_description = getLocalizedString("menu.expanded.alllay.treasure_finder.description");
public static String menu_expanded_allay_flamable_title = getLocalizedString("menu.expanded.alllay.kinda_flamable.title");
public static String menu_expanded_allay_flamable_description = getLocalizedString("menu.expanded.alllay.kinda_flamable.description");
public static String menu_expanded_allay_angel_title = getLocalizedString("menu.expanded.alllay.angel.title");
public static String menu_expanded_allay_angel_description = getLocalizedString("menu.expanded.alllay.angel.description");
public static String menu_expanded_bee_featherweight_title = getLocalizedString("menu.expanded.bee_featherweight_title");
public static String menu_expanded_bee_featherweight_description = getLocalizedString("menu.expanded.bee.featherweight.description");
public static String menu_expanded_bee_poisonous_title = getLocalizedString("menu.expanded.bee.poisonous.title");
public static String menu_expanded_bee_poisonous_description = getLocalizedString("menu.expanded.bee.poisonous.description");
public static String menu_expanded_bee_bloom_title = getLocalizedString("menu.expanded.bee.bloom.title");
public static String menu_expanded_bee_bloom_description = getLocalizedString("menu.expanded.bee.bloom.description");
public static String menu_expanded_bee_flight_title = getLocalizedString("menu.expanded.bee.flight.title");
public static String menu_expanded_bee_flight_description = getLocalizedString("menu.expanded.bee.flight.description");
public static String menu_expanded_bee_nighttime_title = getLocalizedString("menu.expanded.bee.nighttime.title");
public static String menu_expanded_bee_nighttime_description = getLocalizedString("menu.expanded.bee.nighttime.description");
public static String menu_expanded_bee_lifespan_title = getLocalizedString("menu.expanded.bee.lifespan.title");
public static String menu_expanded_bee_lifespan_description = getLocalizedString("menu.expanded.bee.lifespan.description");
public static String menu_expanded_bee_rain_title = getLocalizedString("menu.expanded.bee.rain.title");
public static String menu_expanded_piglin_shiny_title = getLocalizedString("menu.expanded.piglin.shiny.title");
public static String menu_expanded_piglin_shiny_description = getLocalizedString("menu.expanded.piglin.shiny.description");
public static String menu_expanded_piglin_frenemies_title = getLocalizedString("menu.expanded.piglin.friendly_frenemies.title");
public static String menu_expanded_piglin_frenemies_description = getLocalizedString("menu.expanded.piglin.friendly_frenemies.description");
public static String menu_expanded_piglin_nether_title = getLocalizedString("menu.expanded.piglin.nether_dweller.title");
public static String menu_expanded_piglin_nether_description = getLocalizedString("menu.expanded.piglin.nether_dweller.description");
public static String menu_expanded_piglin_colder_title = getLocalizedString("menu.expanded.piglin.colder_realms.title");
public static String menu_expanded_piglin_colder_description = getLocalizedString("menu.expanded.piglin.colder_realms.description");
public static String menu_expanded_piglin_spooky_title = getLocalizedString("menu.expanded.piglin.blue_spooky.title");
public static String menu_expanded_piglin_spooky_description = getLocalizedString("menu.expanded.piglin.blue_spooky.description");
public static String menu_expanded_sculk_them_title = getLocalizedString("menu.expanded.sculk.one_of_them.title");
public static String menu_expanded_sculk_them_description = getLocalizedString("menu.expanded.sculk.one_of_them.description");
public static String menu_expanded_sculk_amongst_title = getLocalizedString("menu.expanded.sculk.amongst_people.title");
public static String menu_expanded_sculk_amongst_description = getLocalizedString("menu.expanded.sculk.amongst_people.description");
public static String menu_expanded_sculk_besties_title = getLocalizedString("menu.expanded.sculk.besties_forever.title");
public static String menu_expanded_sculk_besties_description = getLocalizedString("menu.expanded.sculk.besties_forever.description");
public static String menu_expanded_sculk_light_title = getLocalizedString("menu.expanded.sculk.afraid_of_light.title");
public static String menu_expanded_sculk_light_description = getLocalizedString("menu.expanded.sculk.afraid_of_light.description");
public static String menu_expanded_sculk_grows_title = getLocalizedString("menu.expanded.sculk.it_grows.title");
public static String menu_expanded_sculk_grows_description = getLocalizedString("menu.expanded.sculk.it_grows.description");
public static String menu_expanded_sculk_pulse_title = getLocalizedString("menu.expanded.sculk.echo_pulse.title");
public static String menu_expanded_sculk_pulse_description = getLocalizedString("menu.expanded.sculk.echo_pulse.description");
public static String menu_expanded_sculk_essence_title = getLocalizedString("menu.expanded.sculk.decaying_essence.title");
public static String menu_expanded_sculk_essence_description = getLocalizedString("menu.expanded.sculk.decaying_essence.description");
public static String menu_expanded_sculk_carrier_title = getLocalizedString("menu.expanded.sculk.carrier_echos.title");
public static String menu_expanded_sculk_carrier_description = getLocalizedString("menu.expanded.sculk.carrier_echos.description");
public static String menu_expanded_creep_boom_title = getLocalizedString("menu.expanded.creep.boom.title");
public static String menu_expanded_creep_boom_description = getLocalizedString("menu.expanded.creep.boom.description");
public static String menu_expanded_creep_charged_title = getLocalizedString("menu.expanded.creep.charged.title");
public static String menu_expanded_creep_charged_description = getLocalizedString("menu.expanded.creep.charged.description");
public static String menu_expanded_creep_friendinme_title = getLocalizedString("menu.expanded.creep.friend_in_me.title");
public static String menu_expanded_creep_friendinme_description = getLocalizedString("menu.expanded.creep.friend_in_me.description");
public static String menu_expanded_creep_felinephobia_title = getLocalizedString("menu.expanded.creep.felinephobia.title");
public static String menu_expanded_slimeling_bouncy_title = getLocalizedString("menu.expanded.slimeling.bouncy.title");
public static String menu_expanded_slimeling_bouncy_description = getLocalizedString("menu.expanded.slimeling.bouncy.description");
public static String menu_expanded_slimeling_solid_title = getLocalizedString("menu.expanded.slimeling.not_very_solid.title");
public static String menu_expanded_slimeling_solid_description = getLocalizedString("menu.expanded.slimeling.not_very_solid.description");
public static String menu_expanded_slimeling_jump_title = getLocalizedString("menu.expanded.slimeling.improved_jump.title");
public static String menu_expanded_slimeling_jump_description = getLocalizedString("menu.expanded.slimeling.improved_jump.description");
public static String menu_expanded_slimeling_leap_title = getLocalizedString("menu.expanded.slimeling.great_leap.title");
public static String menu_expanded_slimeling_leap_description = getLocalizedString("menu.expanded.slimeling.great_leap.description                ");
public static String menu_expanded_slimeling_skin_title = getLocalizedString("menu.expanded.slimeling.slimy_skin.title");
public static String menu_expanded_slimeling_skin_description = getLocalizedString("menu.expanded.slimeling.slimy_skin.description");
public static String menu_expanded_slimeling_burnable_title = getLocalizedString("menu.expanded.slimeling.burnable.title");
public static String menu_expanded_slimeling_burnable_description = getLocalizedString("menu.expanded.slimeling.burnable.description");

    public static File getLangFile() {

        String langFileName = GenesisDataFiles.getMainConfig().getString("lang");
        String filePath = GenesisMC.getPlugin().getDataFolder() + File.separator + "lang" + File.separator + langFileName + "-lang.yml";
        File langFile = new File(filePath);

        try {
            if (!langFile.exists()) {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Error finding lang file, please restart the server, or use a valid lang file");
                return null;
            }
        } catch (SecurityException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Error accessing lang file: " + ChatColor.WHITE + e.getMessage());
            return null;
        }

        return langFile;

    }

    public static String getLocalizedString(String key) {
        File langFile = getLangFile();

        if (langFile != null) {
            YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);
            return langConfig.getString(key);
        }

        return null;
    }

}
