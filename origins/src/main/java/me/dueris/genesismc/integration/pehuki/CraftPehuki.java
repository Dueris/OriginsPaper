package me.dueris.genesismc.integration.pehuki;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
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
                    .then(argument("scale", FloatArgumentType.floatArg())
                        .executes(context -> execute(context, Attribute.GENERIC_SCALE))
                    )
                    .then(literal("motion").then(argument("scale", FloatArgumentType.floatArg())
                            .executes(context -> {
                                execute(context, Attribute.GENERIC_MOVEMENT_SPEED);
                                return SINGLE_SUCCESS;
                            })
                        )
                    ).then(literal("entity_reach").then(argument("scale", FloatArgumentType.floatArg())
                            .executes(context -> execute(context, Attribute.PLAYER_ENTITY_INTERACTION_RANGE))
                        )
                    ).then(literal("block_reach").then(argument("scale", FloatArgumentType.floatArg())
                            .executes(context -> execute(context, Attribute.PLAYER_BLOCK_INTERACTION_RANGE))
                        )
                    ).then(literal("flight").then(argument("scale", FloatArgumentType.floatArg())
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
                    ).then(literal("knockback").then(argument("scale", FloatArgumentType.floatArg())
                            .executes(context -> execute(context, Attribute.GENERIC_ATTACK_KNOCKBACK))
                        )
                    ).then(literal("scale").then(argument("scale", FloatArgumentType.floatArg())
                            .executes(context -> execute(context, Attribute.GENERIC_SCALE))
                        )
                    ).then(literal("step-height").then(argument("scale", FloatArgumentType.floatArg())
                            .executes(context -> execute(context, Attribute.GENERIC_STEP_HEIGHT))
                        )
                    ).then(literal("visibility").then(argument("scale", FloatArgumentType.floatArg())
                            .executes(context -> execute(context, Attribute.GENERIC_FOLLOW_RANGE))
                        )
                    )
                )
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
