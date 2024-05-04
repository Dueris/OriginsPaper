package me.dueris.genesismc.command;

import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import joptsimple.internal.Strings;
import me.dueris.calio.registry.Registrar;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.JsonTextFormatter;
import me.dueris.genesismc.util.PowerUtils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class PowerCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> main = literal("power").requires(source -> source.hasPermission(2));
        main.then(literal("dump")
                .then(argument("power", ResourceLocationArgument.id())
                        .suggests((context, builder) -> {
                            OriginCommand.commandProvidedPowers.forEach((power) -> {
                                if (context.getInput().split(" ").length == 2 || (power.getTag().startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])
                                        || power.getTag().split(":")[1].startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1]))) {
                                    builder.suggest(power.getTag());
                                }
                            });
                            return builder.buildFuture();
                        }).executes(context -> {
                            try {
                                Power power = ((Registrar<Power>) GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).get(NamespacedKey.fromString(ResourceLocationArgument.getId(context, "power").getNamespace() + ":" + ResourceLocationArgument.getId(context, "power").getPath()));
                                if (power == null) {
                                    context.getSource().sendFailure(Component.literal("Power not found."));
                                    return 1;
                                }

                                String indent = Strings.repeat(' ', 4);
                                context.getSource().sendSuccess(() -> {
                                    String append = context.getSource().isPlayer() ? "" : "\n";
                                    return Component.literal(append).append((new JsonTextFormatter(indent)).apply(JsonParser.parseString(power.getJsonData())));
                                }, false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return SINGLE_SUCCESS;
                        })
                )
        ).then(literal("grant")
                .then(argument("targets", EntityArgument.entities())
                        .then(argument("power", ResourceLocationArgument.id())
                                .suggests((context, builder) -> {
                                    OriginCommand.commandProvidedPowers.forEach((power) -> {
                                        if (context.getInput().split(" ").length == 3 || (power.getTag().startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])
                                                || power.getTag().split(":")[1].startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1]))) {
                                            builder.suggest(power.getTag());
                                        }
                                    });
                                    return builder.buildFuture();
                                }).executes(context -> {
                                    EntityArgument.getPlayers(context, "targets").forEach(player -> {
                                        if (OriginPlayerAccessor.playerPowerMapping.get(player.getBukkitEntity()) != null) {
                                            Power poweR = ((Registrar<Power>) GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).get(CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "power")));
                                            ArrayList<Power> powersToEdit = new ArrayList<>(CraftApoli.getNestedPowers(poweR));
                                            powersToEdit.add(poweR);
                                            for (Power power : powersToEdit) {
                                                try {
                                                    PowerUtils.grant(context.getSource().getBukkitSender(), power, player.getBukkitEntity(), CraftApoli.getLayerFromTag("apoli:command"), context.getSource().isSilent());
                                                } catch (InstantiationException | IllegalAccessException ex) {
                                                    throw new RuntimeException(ex);
                                                }
                                            }
                                        }
                                    });
                                    return SINGLE_SUCCESS;
                                }).then(argument("layer", ResourceLocationArgument.id())
                                        .executes(context -> {
                                            EntityArgument.getPlayers(context, "targets").forEach(player -> {
                                                if (OriginPlayerAccessor.playerPowerMapping.get(player.getBukkitEntity()) != null) {
                                                    Power poweR = ((Registrar<Power>) GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).get(CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "power")));
                                                    ArrayList<Power> powersToEdit = new ArrayList<>(CraftApoli.getNestedPowers(poweR));
                                                    powersToEdit.add(poweR);
                                                    for (Power power : powersToEdit) {
                                                        try {
                                                            PowerUtils.grant(context.getSource().getBukkitSender(), power, player.getBukkitEntity(), CraftApoli.getLayerFromTag(CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "layer")).asString()), context.getSource().isSilent());
                                                        } catch (InstantiationException | IllegalAccessException ex) {
                                                            throw new RuntimeException(ex);
                                                        }
                                                    }
                                                }
                                            });
                                            return SINGLE_SUCCESS;
                                        })
                                )
                        )
                )
        ).then(literal("has")
                .then(argument("targets", EntityArgument.entities())
                        .then(argument("power", ResourceLocationArgument.id())
                                .suggests((context, builder) -> {
                                    OriginCommand.commandProvidedPowers.forEach((power) -> {
                                        if (context.getInput().split(" ").length == 3 || (power.getTag().startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])
                                                || power.getTag().split(":")[1].startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1]))) {
                                            builder.suggest(power.getTag());
                                        }
                                    });
                                    return builder.buildFuture();
                                }).executes(context -> {
                                    AtomicBoolean passed = new AtomicBoolean(false);
                                    EntityArgument.getPlayers(context, "targets").forEach(player -> {
                                        for (Layer layer : OriginCommand.commandProvidedLayers) {
                                            for (Power power : OriginPlayerAccessor.playerPowerMapping.get(player.getBukkitEntity()).get(layer)) {
                                                if (passed.get()) continue;
                                                if (power.getTag().equals(CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "power")).asString())) {
                                                    passed.set(true);
                                                }
                                            }
                                        }
                                        if (passed.get()) {
                                            context.getSource().sendSystemMessage(Component.literal("Test passed"));
                                        } else {
                                            context.getSource().sendFailure(Component.literal("Test failed"));
                                        }
                                    });

                                    return SINGLE_SUCCESS;
                                })
                        )
                )
        ).then(literal("list")
                .then(argument("targets", EntityArgument.entities())
                        .executes(context -> {
                            for (ServerPlayer player : EntityArgument.getPlayers(context, "targets")) {
                                ConcurrentLinkedQueue<Power> allPowers = new ConcurrentLinkedQueue<>();
                                for (Layer layerContainer : OriginCommand.commandProvidedLayers) {
                                    ConcurrentLinkedQueue<Power> powers = OriginPlayerAccessor.playerPowerMapping.get(player.getBukkitEntity()).get(layerContainer);
                                    if (!(powers == null || powers.isEmpty())) {
                                        allPowers.addAll(powers);
                                    }
                                }

                                if (allPowers.isEmpty()) {
                                    context.getSource().sendFailure(Component.literal("Entity %name% does not have any powers".replace("%name%", player.getBukkitEntity().getName())));
                                } else {
                                    String msg = "Entity %name% has %size% powers: [%powers%]".replace("%name%", player.getBukkitEntity().getName()).replace("%size%", String.valueOf(allPowers.size()));
                                    final String[] powerString = {""};
                                    allPowers.forEach((power) -> powerString[0] = powerString[0] + power.getTag() + ", ");
                                    String finMsg = msg.replace("%powers%", powerString[0]);
                                    context.getSource().sendSystemMessage(Component.literal(finMsg.replace(", ]", "]")));
                                }
                            }
                            return SINGLE_SUCCESS;
                        })
                )
        );
        addRemoveArg(main, "remove");
        addRemoveArg(main, "revoke");
        dispatcher.register(main);
    }

    private static void addRemoveArg(LiteralArgumentBuilder<CommandSourceStack> main, String name) {
        main.then(literal(name).then(argument("targets", EntityArgument.entities())
                .then(argument("power", ResourceLocationArgument.id())
                        .suggests((context, builder) -> {
                            OriginCommand.commandProvidedPowers.forEach((power) -> {
                                if (context.getInput().split(" ").length == 3 || (power.getTag().startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])
                                        || power.getTag().split(":")[1].startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1]))) {
                                    builder.suggest(power.getTag());
                                }
                            });
                            return builder.buildFuture();
                        }).executes(context -> {
                            EntityArgument.getPlayers(context, "targets").forEach(p -> {
                                NamespacedKey arg = CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "power"));
                                CraftApoli.getLayersFromRegistry().forEach(layer -> {
                                    try {
                                        PowerUtils.remove(context.getSource().getBukkitSender(), ((Registrar<Power>) GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).get(arg), p.getBukkitEntity(), layer, context.getSource().isSilent());
                                    } catch (InstantiationException | IllegalAccessException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                            });

                            return SINGLE_SUCCESS;
                        }).then(argument("layer", ResourceLocationArgument.id())
                                .executes(context -> {
                                    EntityArgument.getPlayers(context, "targets").forEach(p -> {
                                        NamespacedKey arg = CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "power"));
                                        String layer = CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "layer")).asString();

                                        try {
                                            PowerUtils.remove(context.getSource().getBukkitSender(), ((Registrar<Power>) GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).get(arg), p.getBukkitEntity(), CraftApoli.getLayerFromTag(layer), context.getSource().isSilent());
                                        } catch (InstantiationException | IllegalAccessException e) {
                                            throw new RuntimeException(e);
                                        }
                                    });

                                    return SINGLE_SUCCESS;
                                })
                        )
                )
        ));
    }
}
