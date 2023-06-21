package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

import static me.dueris.genesismc.core.utils.BukkitColour.YELLOW;

public class Bug extends SubCommand {
    @Override
    public String getName() {
        return "Bug";
    }

    @Override
    public String getDescription() {
        return "returns the links to submit any bugs found to.";
    }

    @Override
    public String getSyntax() {
        return "/origin bug";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        sender.sendMessage(Component.text("To report a bug either open an issue on github or join our discord server and open a post in the bug-reports channel!").color(TextColor.fromHexString(YELLOW)));

        TextComponent git = Component.text("GitHub: https://github.com/Dueris/GenesisMC-Minecraft_Plugin/issues").color(TextColor.fromHexString(YELLOW)).decorate(TextDecoration.UNDERLINED);
        git = git.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/Dueris/GenesisMC-Minecraft_Plugin/issues"));
        sender.sendMessage(git);

        TextComponent discord = Component.text("Discord: https://discord.gg/RKmQnU6SRt").color(TextColor.fromHexString(YELLOW)).decorate(TextDecoration.UNDERLINED);
        discord = discord.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/RKmQnU6SRt"));
        sender.sendMessage(discord);
    }
}
