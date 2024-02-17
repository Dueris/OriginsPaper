package me.dueris.genesismc.command;

import com.mojang.brigadier.CommandDispatcher;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.registry.LayerContainer;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.util.PowerUtils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
                                                if(context.getInput().split(" ").length == 3 || (power.getTag().startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])
                                                        || power.getTag().split(":")[1].startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1]))){
                                                    builder.suggest(power.getTag());
                                                }
                                            });
                                            return builder.buildFuture();
                                        }).executes(context -> {
                                            PowerContainer power = CraftApoli.keyedPowerContainers.get(CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "power")).asString());
                                            for(String string : power.getJsonData()){
                                                context.getSource().sendSystemMessage(Component.literal(string));
                                            }
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
                                                            PowerContainer poweR = CraftApoli.keyedPowerContainers.get(CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "power")).asString());
                                                            ArrayList<PowerContainer> powersToEdit = new ArrayList<>();
                                                            powersToEdit.add(poweR);
                                                            powersToEdit.addAll(CraftApoli.getNestedPowers(poweR));
                                                            for (PowerContainer power : powersToEdit) {
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
                                                                    PowerContainer poweR = CraftApoli.keyedPowerContainers.get(CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "power")).asString());
                                                                    ArrayList<PowerContainer> powersToEdit = new ArrayList<>();
                                                                    powersToEdit.add(poweR);
                                                                    powersToEdit.addAll(CraftApoli.getNestedPowers(poweR));
                                                                    for (PowerContainer power : powersToEdit) {
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
                                                        for (LayerContainer layer : OriginCommand.commandProvidedLayers) {
                                                            for (PowerContainer power : OriginPlayerAccessor.playerPowerMapping.get(player.getBukkitEntity()).get(layer)) {
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
                                                for (LayerContainer layerContainer : OriginCommand.commandProvidedLayers) {
                                                    java.util.List<PowerContainer> powers = OriginPlayerAccessor.playerPowerMapping.get(player.getBukkitEntity()).get(layerContainer);
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
                                                        String arg = CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "power")).asString();
                                                        String layer = "origins:origin";

                                                        try {
                                                            PowerUtils.remove(context.getSource().getBukkitSender(), CraftApoli.getPowerContainerFromTag(arg), p.getBukkitEntity(), CraftApoli.getLayerFromTag(layer));
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
                                                                String arg = CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "power")).asString();
                                                                String layer = CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "layer")).asString();

                                                                try {
                                                                    PowerUtils.remove(context.getSource().getBukkitSender(), CraftApoli.getPowerContainerFromTag(arg), p.getBukkitEntity(), CraftApoli.getLayerFromTag(layer));
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
