package me.dueris.genesismc.command;

import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;

import joptsimple.internal.Strings;
import me.dueris.calio.registry.Registrar;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.PowerUtils;
import me.dueris.genesismc.util.apoli.JsonTextFormatter;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class PowerCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                literal("power").requires(source -> source.hasPermission(2))
                        .then(literal("dump")
                                .then(argument("power", ResourceLocationArgument.id())
                                        .suggests((context, builder) -> {
                                            OriginCommand.commandProvidedPowers.forEach((power) -> {
                                                if(context.getInput().split(" ").length == 2 || (power.getTag().startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])
                                                        || power.getTag().split(":")[1].startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1]))){
                                                    builder.suggest(power.getTag());
                                                }
                                            });
                                            return builder.buildFuture();
                                        }).executes(context -> {
                                            Power power = (Power)GenesisMC.getPlugin().registry.retrieve(Registries.POWER).get(CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "power")));
                                            if (power == null) {
                                            ((CommandSourceStack)context.getSource()).sendFailure(Component.literal("Power not found."));
                                            return 1;
                                            }

                                            String indent = Strings.repeat(' ', 4);
                                            context.getSource().sendSuccess(() -> {
                                                String append = context.getSource().isPlayer() ? "" : "\n";
                                                return Component.literal(append).append((new JsonTextFormatter(indent)).apply(JsonParser.parseString(power.getJsonData())));
                                            }, false);
                                            return SINGLE_SUCCESS;
                                        })
                                )
                        ).then(literal("grant")
                                .then(argument("targets", EntityArgument.players())
                                        .then(argument("power", ResourceLocationArgument.id())
                                                .suggests((context, builder) -> {
                                                    OriginCommand.commandProvidedPowers.forEach((power) -> {
                                                        if(context.getInput().split(" ").length == 3 || (power.getTag().startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])
                                                        || power.getTag().split(":")[1].startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1]))){
                                                            builder.suggest(power.getTag());
                                                        }
                                                    });
                                                    return builder.buildFuture();
                                                }).executes(context -> {
                                                    EntityArgument.getPlayers(context, "targets").forEach(player -> {
                                                        if (OriginPlayerAccessor.playerPowerMapping.get(player.getBukkitEntity()) != null) {
                                                            Power poweR = ((Registrar<Power>)GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).get(CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "power")));
                                                            ArrayList<Power> powersToEdit = new ArrayList<>();
                                                            powersToEdit.add(poweR);
                                                            powersToEdit.addAll(CraftApoli.getNestedPowers(poweR));
                                                            for (Power power : powersToEdit) {
                                                                try {
                                                                    PowerUtils.grant(context.getSource().getBukkitSender(), power, player.getBukkitEntity(), CraftApoli.getLayerFromTag("origins:origin"));
                                                                } catch (InstantiationException ex) {
                                                                    throw new RuntimeException(ex);
                                                                } catch (IllegalAccessException ex) {
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
                                                                    Power poweR = ((Registrar<Power>)GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).get(CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "power")));
                                                                    ArrayList<Power> powersToEdit = new ArrayList<>();
                                                                    powersToEdit.add(poweR);
                                                                    powersToEdit.addAll(CraftApoli.getNestedPowers(poweR));
                                                                    for (Power power : powersToEdit) {
                                                                        try {
                                                                            PowerUtils.grant(context.getSource().getBukkitSender(), power, player.getBukkitEntity(), CraftApoli.getLayerFromTag(CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "layer")).asString()));
                                                                        } catch (InstantiationException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        } catch (IllegalAccessException ex) {
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
                                .then(argument("targets", EntityArgument.players())
                                        .then(argument("power", ResourceLocationArgument.id())
                                                .suggests((context, builder) -> {
                                                    OriginCommand.commandProvidedPowers.forEach((power) -> {
                                                        if(context.getInput().split(" ").length == 3 || (power.getTag().startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])
                                                                || power.getTag().split(":")[1].startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1]))){
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
                                .then(argument("targets", EntityArgument.players())
                                        .executes(context -> {
                                            for(ServerPlayer player : EntityArgument.getPlayers(context, "targets")){
                                                for (Layer layerContainer : OriginCommand.commandProvidedLayers) {
                                                    java.util.List<Power> powers = OriginPlayerAccessor.playerPowerMapping.get(player.getBukkitEntity()).get(layerContainer);
                                                    if(powers == null || powers.isEmpty()){
                                                        context.getSource().sendFailure(Component.literal("Entity %name% does not have any powers".replace("%name%", player.getBukkitEntity().getName())));
                                                    }else{
                                                        String msg = "Entity %name% has %size% powers: [%powers%]".replace("%name%", player.getBukkitEntity().getName()).replace("%size%", String.valueOf(powers.size()));
                                                        final String[] powerString = {""};
                                                        powers.forEach((power) -> {
                                                            powerString[0] = powerString[0] + power.getTag() + ", ";
                                                        });
                                                        String finMsg = msg.replace("%powers%", powerString[0]);
                                                        context.getSource().sendSystemMessage(Component.literal(finMsg.replace(", ]", "]")));
                                                    }
                                                }
                                            }
                                            return SINGLE_SUCCESS;
                                        })
                                )
                        ).then(literal("remove")
                                .then(argument("targets", EntityArgument.players())
                                        .then(argument("power", ResourceLocationArgument.id())
                                                .suggests((context, builder) -> {
                                                    OriginCommand.commandProvidedPowers.forEach((power) -> {
                                                        if(context.getInput().split(" ").length == 3 || (power.getTag().startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])
                                                                || power.getTag().split(":")[1].startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1]))){
                                                            builder.suggest(power.getTag());
                                                        }
                                                    });
                                                    return builder.buildFuture();
                                                }).executes(context -> {
                                                    EntityArgument.getPlayers(context, "targets").forEach(p -> {
                                                        NamespacedKey arg = CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "power"));
                                                        String layer = "origins:origin";

                                                        try {
                                                            PowerUtils.remove(context.getSource().getBukkitSender(), ((Registrar<Power>)GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).get(arg), p.getBukkitEntity(), CraftApoli.getLayerFromTag(layer));
                                                        } catch (InstantiationException e) {
                                                            throw new RuntimeException(e);
                                                        } catch (IllegalAccessException e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    });

                                                    return SINGLE_SUCCESS;
                                                }).then(argument("layer", ResourceLocationArgument.id())
                                                        .executes(context -> {
                                                            EntityArgument.getPlayers(context, "targets").forEach(p -> {
                                                                NamespacedKey arg = CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "power"));
                                                                String layer = CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "layer")).asString();

                                                                try {
                                                                    PowerUtils.remove(context.getSource().getBukkitSender(), ((Registrar<Power>)GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).get(arg), p.getBukkitEntity(), CraftApoli.getLayerFromTag(layer));
                                                                } catch (InstantiationException e) {
                                                                    throw new RuntimeException(e);
                                                                } catch (IllegalAccessException e) {
                                                                    throw new RuntimeException(e);
                                                                }
                                                            });

                                                            return SINGLE_SUCCESS;
                                                        })
                                                )
                                        )
                                )
                        )

        );
    }
}
