package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import me.dueris.genesismc.core.utils.Lang;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

import static me.dueris.genesismc.core.utils.BukkitColour.YELLOW;

public class References extends SubCommand {
    @Override
    public String getName() {
        return "references";
    }

    @Override
    public String getDescription() {
        return Lang.getLocalizedString("command.origin.references.description");
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        sender.sendMessage(Component.text(Lang.getLocalizedString("command.origin.references.Apace")));
        TextComponent ApaceLink = Component.text("https://github.com/apace100/origins-fabric").color(TextColor.fromHexString(YELLOW)).decorate(TextDecoration.UNDERLINED);
        sender.sendMessage(ApaceLink.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/apace100/origins-fabric")));

        sender.sendMessage(Component.text(Lang.getLocalizedString("command.origin.references.Slayer")));
        TextComponent SlayerLink = Component.text("https://www.curseforge.com/minecraft/customization/origins-starborne").color(TextColor.fromHexString(YELLOW)).decorate(TextDecoration.UNDERLINED);
        sender.sendMessage(SlayerLink.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "https://www.curseforge.com/minecraft/customization/origins-starborne")));

        sender.sendMessage(Component.text(Lang.getLocalizedString("command.origin.references.TotalElipse")));
        TextComponent TotalElipseLink = Component.text("https://www.curseforge.com/minecraft/mc-mods/slime-origin").color(TextColor.fromHexString(YELLOW)).decorate(TextDecoration.UNDERLINED);
        sender.sendMessage(TotalElipseLink.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/slime-origin")));

        sender.sendMessage(Component.text(Lang.getLocalizedString("command.origin.references.Sakisiil")));
        TextComponent SakisiilLink = Component.text("https://github.com/sakisiil/Origin-Datapacks").color(TextColor.fromHexString(YELLOW)).decorate(TextDecoration.UNDERLINED);
        sender.sendMessage(SakisiilLink.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/sakisiil/Origin-Datapacks")));
    }
}
