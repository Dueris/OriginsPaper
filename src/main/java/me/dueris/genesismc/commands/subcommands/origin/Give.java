package me.dueris.genesismc.commands.subcommands.origin;

import me.dueris.genesismc.commands.PlayerSelector;
import me.dueris.genesismc.commands.subcommands.SubCommand;
import me.dueris.genesismc.factory.powers.block.RecipePower;
import me.dueris.genesismc.utils.translation.LangConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;

import static me.dueris.genesismc.items.OrbOfOrigins.orb;
import static me.dueris.genesismc.utils.text.BukkitColour.RED;

public class Give extends SubCommand {
    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "command.origin.give.description");
    }

    @Override
    public String getSyntax() {
        return "/origin give <player> <item> <amount>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("genesismc.origins.cmd.give")) return;
        if (args.length == 1) {
            sender.sendMessage(Component.text(LangConfig.getLocalizedString(sender, "command.origin.give.noPlayer")).color(TextColor.fromHexString(RED)));
            return;
        }
        if(args.length == 2){
            sender.sendMessage(Component.text("You must give a valid recipe tag.").color(TextColor.fromHexString(RED)));
            return;
        }
        int amt = 1;
        if(args.length > 3){
            amt = Integer.parseInt(args[3]);
        }
        String tag = args[2];
        if(RecipePower.tags.contains(tag)){
            Recipe recipe = RecipePower.taggedRegistry.get(tag);
            ItemStack itemStack = recipe.getResult().clone();
            itemStack.setAmount(amt);
            if(sender instanceof InventoryHolder inventoryHolder){
                inventoryHolder.getInventory().addItem(itemStack);
            }else{
                sender.sendMessage(Component.text("Target not instanceof InventoryHolder").color(TextColor.fromHexString(RED)));
            }
        }else{
            sender.sendMessage(Component.text("Item not found in origins registry.").color(TextColor.fromHexString(RED)));
        }
    }
}
