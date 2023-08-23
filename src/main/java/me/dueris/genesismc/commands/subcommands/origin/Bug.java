package me.dueris.genesismc.commands.subcommands.origin;

import me.dueris.genesismc.commands.subcommands.SubCommand;
import me.dueris.genesismc.utils.translation.LangConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import static me.dueris.genesismc.utils.BukkitColour.YELLOW;

public class Bug extends SubCommand {
    @Override
    public String getName() {
        return "Bug";
    }

    @Override
    public String getDescription() {
        return LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "command.origin.bug.description");
    }

    @Override
    public String getSyntax() {
        return "/origin bug";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        sender.sendMessage(Component.text(LangConfig.getLocalizedString(sender, "command.origin.bug.message")).color(TextColor.fromHexString(YELLOW)));

        TextComponent git = Component.text("GitHub: https://github.com/Dueris/GenesisMC-Minecraft_Plugin/issues").color(TextColor.fromHexString(YELLOW)).decorate(TextDecoration.UNDERLINED);
        git = git.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/Dueris/GenesisMC-Minecraft_Plugin/issues"));
        sender.sendMessage(git);

        TextComponent discord = Component.text("Discord: https://discord.gg/RKmQnU6SRt").color(TextColor.fromHexString(YELLOW)).decorate(TextDecoration.UNDERLINED);
        discord = discord.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/RKmQnU6SRt"));
        sender.sendMessage(discord);
    }
}
