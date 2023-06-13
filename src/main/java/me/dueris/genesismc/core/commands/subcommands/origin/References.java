package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
    public void perform(CommandSender sender, String[] args) {
        sender.sendMessage(Component.text("Apace - Original mod creator - https://github.com/apace100/origins-fabric"));
        sender.sendMessage(Component.text("Slayer - Starborne datapack creator - https://www.curseforge.com/minecraft/customization/origins-starborne"));
        sender.sendMessage(Component.text("TotalElipse - Slime origin add-on creator - https://www.curseforge.com/minecraft/mc-mods/slime-origin"));
        sender.sendMessage(Component.text("Sakisiil - Bee origin datapack creator - https://github.com/sakisiil/Origin-Datapacks"));
    }
}
