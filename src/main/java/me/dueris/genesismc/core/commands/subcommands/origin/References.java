package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import org.bukkit.entity.Player;

import java.util.List;

public class References extends SubCommand {
    @Override
    public String getName() {
        return "references";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public void perform(Player p, String[] args) {
        p.sendMessage("Apace - Original mod creator - https://github.com/apace100/origins-fabric");
        p.sendMessage("Slayer - Starborne datapack creator - https://www.curseforge.com/minecraft/customization/origins-starborne");
        p.sendMessage("TotalElipse - Slime origin add-on creator - https://www.curseforge.com/minecraft/mc-mods/slime-origin");
        p.sendMessage("Sakisiil - Bee origin datapack creator - https://github.com/sakisiil/Origin-Datapacks");

    }
}
