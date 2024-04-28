package me.dueris.genesismc.integration.pehuki;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.CraftServer;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class CraftPehuki {
    public static void onLoad() {
        register(((CraftServer) Bukkit.getServer()).getServer().vanillaCommandDispatcher.getDispatcher());
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            literal("scale").requires(source -> source.hasPermission(3))
                .then(literal("set")
                    .then(argument("scale", FloatArgumentType.floatArg()).executes(context -> execute(context, Attribute.GENERIC_SCALE)))
                    .then(buildNew("motion", Attribute.GENERIC_MOVEMENT_SPEED))
                    .then(buildNew("entity_reach", Attribute.PLAYER_ENTITY_INTERACTION_RANGE))
                    .then(buildNew("block_reach", Attribute.PLAYER_BLOCK_INTERACTION_RANGE))
                    .then(literal("flight").then(argument("scale", FloatArgumentType.floatArg())
                            .executes(context -> {
                                try {
                                    if (context.getSource().isPlayer()) {
                                        ServerPlayer player = context.getSource().getPlayer();
                                        player.getBukkitEntity().setFlySpeed(FloatArgumentType.getFloat(context, "scale"));
                                    } else {
                                        context.getSource().sendFailure(Component.literal("This must be executed by a player!"));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return SINGLE_SUCCESS;
                            })
                        )
                    )
                    .then(buildNew("knockback", Attribute.GENERIC_ATTACK_KNOCKBACK))
                    .then(buildNew("scale", Attribute.GENERIC_SCALE))
                    .then(buildNew("step-height", Attribute.GENERIC_STEP_HEIGHT))
                    .then(buildNew("visibility", Attribute.GENERIC_FOLLOW_RANGE))
                )
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildNew(String e, Attribute attribute) {
        return literal(e).then(argument("scale", FloatArgumentType.floatArg())
            .executes(context -> execute(context, attribute))
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context, Attribute attribute) {
        try {
            if (context.getSource().isPlayer()) {
                ServerPlayer player = context.getSource().getPlayer();
                player.getBukkitEntity().getAttribute(attribute).setBaseValue(FloatArgumentType.getFloat(context, "scale"));
            } else {
                context.getSource().sendFailure(Component.literal("This must be executed by a player!"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SINGLE_SUCCESS;
    }
}
