package me.dueris.genesismc.core.utils;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.files.GenesisDataFiles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Lang {
    public static File getLangFile(){

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

}
