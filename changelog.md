<h3>0.1.4-BETA_RELEASE<h3>
<h4><code>Created by PurpleWolfMC</code><h4>
<hr>
<h2>THE PLUGIN WILL RUN ON SPIGOT AND BUKKIT, BUT MOST FEATURES WILL NOT WORK. USE AT YOUR OWN RISK</h2>
<h3>Changelog:</h3>
<pre>
<h4>Main update stuff</h4>
-Updated to 1.19.4
-Added Shulk Origin
<h3>DELETE THE CONFIG FOLDERS FOR GENESISMC. NEW CONFIG OPTIONS HAVE COME AND THEY WILL BREAK IF NOT LOADED CORRECTLY</h3>
<hr>
-fixed bug where orb of origins would be displayed as eye of ender in choosing menu
-added particle effect for when you have chosen your origin
-changed config version to 94
-added purge permission(genesismc.origins.purge):
    default: op
-added reload permission(genesismc.origins.reload):
    default: op
-added reload command(usage: /origins reload)
    -reloads configuration file
-added genesisfiles.class
    -manager for custom files and folders.
-added origins deactivation config
    -was in previous version but now actually works lol
-added enderian silk touch
-removed upcomingplans
-fixed bukkitrunnable that caused reload success message to constantly send
-removed ender-reach-beta config
-removed enderian-cooldown config
-created infinpearl class.
-fixed issue where attributes wouldnt be removed when set to human origin/origin was purged
-updated readme.md
-added new command(usage: /genesismc [any subcommand that can be used with /origins]
-added new data folder paths:
    -GenesisMC
        -origins
            =holds data for origin config options
        -items
            =holds data for item config options
        -beta
            =holds data for beta config options
        -choosing_menu
            =holds data for choosing menu config options
-fixed bug where player would play damage sound when hit by arrow(enderian)
-made reload command reload all custom configs(tbh i think its still broken lol)
-added /shulker command. sub command is open
    -opens shulker custom inventory(keeps items upon death)
-edited shulker choosing to match new command feature
-broke mc with clouds when testin so thats fun lmfao
-edited startup to include "Created by PurpleWolfMC" which is in purple ofc
-did not add herobrine
-changed enderian loot table to drop 0-2 ender pearls upon death
-changed config.yml to hold new values to match with new config methods
-fixed error that would cause plugin to shut down because configs had not been generated yet
-implemented origins rewrite
-fixed bug that would cause allay choosing menu to not have interactions
-fixed bug that would make the shulk inventory not load and to be "null"
-fixed bug that would cause allay to still display as witch origin in rechoosing
-fixed bug where any origin had enderian silk touch
-added new gallery image for modrinth page
-fixed MASSIVE bug that caused enderians to never take damage. ever lol
-implemented a bad way to make shulk block break work...
-added config option to disable orb of origins
-fixed geyser compatibility issues
-changed to maven plugin
-made random chance for shulk to loose more hunger upon sprinting
-disabled shield for shulk
-fixed bug that would cause enderians to have particles enabled when invisible and when in spectator mode
-fixed duplication bug for enderian ender pearl
-made infinpearls not useable for other origins
-implemented disable method
-fixed bug where orb of origins can only be used with 1 orb in hand
-added new bukkitrunnable method
-added /origins command
-added /origins texture
-added /origins config
-added new permission for config subcommand
--fixed all bugs for enderian water detection
    -has near perfect detection
-added auto-tabcompletion for all commands
-added ingorecase for enderian water damage
    -protection enchantment no longer affects enderian damage amt
-changed config format to 0141
-added beta command called "/beta waterprot"
-added waterprot beta cmd to tabcomplete
-added waterprot enchantment
    -can only be accessed by random chance when enchanting an item in enchantment table. will not show
        up on the menu, will be enchanted along with another enchantment
    -currently cannot be changed by anvils or removed by grindstones.
-removed orboforigin lore
-fixed bug that would cause enderians to die to water while in creative mode
-fixed enchant detection bug
-added water protection enchantment function
-fixed water protection calculation bug
-fixed shulk bug on origins choosing
-fixed bug for the disable config option
<hr>
Side notes:
-Texture command kinda works, works fine, resource pack doesnt load. Will fix in 0.1.5 or 0.1.6
-Only Shulk, Enderian, and Human origins exist.

</pre>
<hr>
<header>Again, still in early beta. Most features do not work atm.</header>
<blockquote><a href="https://modrinth.com/plugins/GenesisMC" rel="noopener nofollow ugc">Download link</a>

<a href="https://streamelements.com/purplewolfmc/tip" rel="noopener nofollow ugc">Donate to support the project!</a>
</blockquote>
