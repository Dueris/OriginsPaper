package me.dueris.genesismc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import it.unimi.dsi.fastutil.Pair;
import me.dueris.calio.registry.Registrar;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.apoli.Resource;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static me.dueris.genesismc.factory.actions.Actions.resourceChangeTimeout;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ResourceCommand {

	public static HashMap<Player, HashMap<String, Pair<BossBar, Double>>> registeredBars = new HashMap();

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
			literal("resource").requires(source -> source.hasPermission(2))
				.then(literal("has")
					.then(argument("targets", EntityArgument.players())
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
								boolean tru = false;
								for (ServerPlayer player : EntityArgument.getPlayers(context, "targets")) {
									Player p = player.getBukkitEntity();
									for (Layer layerContainer : OriginCommand.commandProvidedLayers) {
										for (Power powerContainer : OriginPlayerAccessor.playerPowerMapping.get(p).get(layerContainer)) {
											if (powerContainer.getType().equals("apoli:cooldown") || powerContainer.getType().equals("apoli:resource")) {
												if (powerContainer.getTag().equals(CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "power")).asString())) {
													context.getSource().sendSystemMessage(Component.literal("Test passed."));
													tru = true;
												}
											}
										}
									}
								}
								if (!tru) {
									context.getSource().sendFailure(Component.literal("Test failed."));
								}
								return SINGLE_SUCCESS;
							})
						)
					)
				).then(literal("get")
					.then(argument("targets", EntityArgument.players())
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
									if (registeredBars.containsKey(player.getBukkitEntity()) && registeredBars.get(player.getBukkitEntity()).containsKey(CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "power")).asString())) {
										context.getSource().sendSystemMessage(Component.literal("$1 has %value% $2"
											.replace("$2", CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "power")).asString())
											.replace("$1", player.getBukkitEntity().getName())
											.replace("%value%", String.valueOf(registeredBars.get(player.getBukkitEntity()).get(CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "power")).asString()).left().getProgress()))));
									} else {
										context.getSource().sendFailure(Component.literal("Can't get value of $2 for $1; none is set"
											.replace("$2", CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "power")).asString())
											.replace("$1", player.getBukkitEntity().getName())));
									}
								});
								return SINGLE_SUCCESS;
							})
						)
					)
				).then(literal("change")
					.then(argument("targets", EntityArgument.players())
						.then(argument("power", ResourceLocationArgument.id())
							.suggests((context, builder) -> {
								OriginCommand.commandProvidedPowers.forEach((power) -> {
									if (context.getInput().split(" ").length == 3 || (power.getTag().startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1])
										|| power.getTag().split(":")[1].startsWith(context.getInput().split(" ")[context.getInput().split(" ").length - 1]))) {
										builder.suggest(power.getTag());
									}
								});
								return builder.buildFuture();
							}).then(argument("value", IntegerArgumentType.integer())
								.executes(context -> {
									int value = IntegerArgumentType.getInteger(context, "value");
									Power power = ((Registrar<Power>) GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).get(CraftNamespacedKey.fromMinecraft(ResourceLocationArgument.getId(context, "power")));
									EntityArgument.getPlayers(context, "targets").forEach(player -> {
										if (resourceChangeTimeout.containsKey(player.getBukkitEntity())) return;
										String resource = power.getTag();
										int change = value;
										double finalChange = 1.0 / Resource.getResource(player.getBukkitEntity(), resource).right();
										BossBar bossBar = Resource.getResource(player.getBukkitEntity(), resource).left();
										double toRemove = finalChange * change;
										double newP = bossBar.getProgress() + toRemove;
										if (newP > 1.0) {
											newP = 1.0;
										} else if (newP < 0) {
											newP = 0.0;
										}
										bossBar.setProgress(newP);
										bossBar.addPlayer(player.getBukkitEntity());
										bossBar.setVisible(true);
										resourceChangeTimeout.put(player.getBukkitEntity(), true);
										new BukkitRunnable() {
											@Override
											public void run() {
												resourceChangeTimeout.remove(player.getBukkitEntity());
											}
										}.runTaskLater(GenesisMC.getPlugin(), 2);
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
