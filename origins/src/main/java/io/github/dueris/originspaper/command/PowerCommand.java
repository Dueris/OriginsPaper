package io.github.dueris.originspaper.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.serialization.JsonOps;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.command.argument.PowerArgumentType;
import io.github.dueris.originspaper.command.argument.PowerHolderArgumentType;
import io.github.dueris.originspaper.command.argument.suggestion.PowerSuggestionProvider;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.util.JsonTextFormatter;
import io.github.dueris.originspaper.util.Util;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;

@SuppressWarnings("UnstableApiUsage")
public class PowerCommand {

	public static ResourceLocation POWER_SOURCE = OriginsPaper.apoliIdentifier("command");

	public static @NotNull LiteralCommandNode<CommandSourceStack> node() {

		//	The main node of the command
		var powerNode = literal("power")
			.requires(source -> ((net.minecraft.commands.CommandSourceStack) source).hasPermission(2))
			.build();

		//	Add the sub-nodes as children of the main node
		powerNode.addChild(GrantNode.get());
		powerNode.addChild(RevokeNode.get());
		powerNode.addChild(ListNode.get());
		powerNode.addChild(HasNode.get());
		powerNode.addChild(SourcesNode.get());
		powerNode.addChild(RemoveNode.get());
		powerNode.addChild(ClearNode.get());
		powerNode.addChild(DumpNode.get());

		return powerNode;

	}

	public static class GrantNode {

		public static LiteralCommandNode<CommandSourceStack> get() {
			return literal("grant")
				.then(argument("targets", PowerHolderArgumentType.entities())
					.then(argument("power", PowerArgumentType.power())
						.executes(context -> execute(context, false))
						.then(argument("source", ResourceLocationArgument.id())
							.executes(context -> execute(context, true))))).build();
		}

		public static int execute(CommandContext<CommandSourceStack> context, boolean specifiedSource) throws CommandSyntaxException {

			List<LivingEntity> targets = PowerHolderArgumentType.getHolders(context, "targets");
			Power power = PowerArgumentType.getPower(context, "power");

			ResourceLocation source = specifiedSource
				? Util.getId(context, "source")
				: POWER_SOURCE;

			net.minecraft.commands.CommandSourceStack commandSource = (net.minecraft.commands.CommandSourceStack) context.getSource();
			List<LivingEntity> processedTargets = targets.stream()
				.filter(e -> PowerHolderComponent.grantPower(e, power, source, true))
				.toList();

			if (processedTargets.isEmpty()) {

				if (targets.size() == 1) {
					commandSource.sendFailure(Component.translatable("commands.apoli.grant.fail.single", targets.getFirst().getName(), power.getName(), source.toString()));
				} else {
					commandSource.sendFailure(Component.translatable("commands.apoli.grant.fail.multiple", targets.size(), power.getName(), source.toString()));
				}

			} else if (specifiedSource) {

				if (processedTargets.size() == 1) {
					commandSource.sendSuccess(() -> Component.translatable("commands.apoli.grant_from_source.success.single", processedTargets.getFirst().getName(), power.getName(), source.toString()), true);
				} else {
					commandSource.sendSuccess(() -> Component.translatable("commands.apoli.grant_from_source.success.multiple", processedTargets.size(), power.getName(), source.toString()), true);
				}

			} else {

				if (processedTargets.size() == 1) {
					commandSource.sendSuccess(() -> Component.translatable("commands.apoli.grant.success.single", processedTargets.getFirst().getName(), power.getName()), true);
				} else {
					commandSource.sendSuccess(() -> Component.translatable("commands.apoli.grant.success.multiple", processedTargets.size(), power.getName()), true);
				}

			}

			return processedTargets.size();

		}

	}

	public static class RevokeNode {

		public static LiteralCommandNode<CommandSourceStack> get() {
			return literal("revoke")
				.then(argument("targets", PowerHolderArgumentType.entities())
					.then(argument("power", PowerArgumentType.power())
						.suggests(PowerSuggestionProvider.powersFromEntities("targets"))
						.executes(context -> executeSingle(context, false))
						.then(argument("source", ResourceLocationArgument.id())
							.executes(context -> executeSingle(context, true))))
					.then(literal("all")
						.then(argument("source", ResourceLocationArgument.id())
							.executes(RevokeNode::executeAll)))).build();
		}

		public static int executeSingle(CommandContext<CommandSourceStack> context, boolean specifiedSource) throws CommandSyntaxException {

			List<LivingEntity> targets = PowerHolderArgumentType.getHolders(context, "targets");
			Power power = PowerArgumentType.getPower(context, "power");

			ResourceLocation source = specifiedSource
				? Util.getId(context, "source")
				: POWER_SOURCE;

			net.minecraft.commands.CommandSourceStack commandSource = (net.minecraft.commands.CommandSourceStack) context.getSource();
			List<LivingEntity> processedTargets = targets.stream()
				.filter(target -> PowerHolderComponent.revokePower(target, power, source, true))
				.toList();

			if (processedTargets.isEmpty()) {

				if (targets.size() == 1) {
					commandSource.sendFailure(Component.translatable("commands.apoli.revoke.fail.single", targets.getFirst().getName(), power.getName(), source.toString()));
				} else {
					commandSource.sendFailure(Component.translatable("commands.apoli.revoke.fail.multiple", power.getName(), source.toString()));
				}

			} else if (specifiedSource) {

				if (processedTargets.size() == 1) {
					commandSource.sendSuccess(() -> Component.translatable("commands.apoli.revoke_from_source.success.single", processedTargets.getFirst().getName(), power.getName(), source.toString()), true);
				} else {
					commandSource.sendSuccess(() -> Component.translatable("commands.apoli.revoke_from_source.success.multiple", processedTargets.size(), power.getName(), source.toString()), true);
				}

			} else {

				if (processedTargets.size() == 1) {
					commandSource.sendSuccess(() -> Component.translatable("commands.apoli.revoke.success.single", processedTargets.getFirst().getName(), power.getName()), true);
				} else {
					commandSource.sendSuccess(() -> Component.translatable("commands.apoli.revoke.success.multiple", processedTargets.size(), power.getName()), true);
				}

			}

			return processedTargets.size();

		}

		public static int executeAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

			List<LivingEntity> targets = PowerHolderArgumentType.getHolders(context, "targets");
			ResourceLocation source = Util.getId(context, "source");

			net.minecraft.commands.CommandSourceStack commandSource = (net.minecraft.commands.CommandSourceStack) context.getSource();
			List<LivingEntity> processedTargets = new ObjectArrayList<>();

			AtomicInteger revokedPowers = new AtomicInteger();
			for (LivingEntity target : targets) {

				int revokedPowersFromSource = PowerHolderComponent.revokeAllPowersFromSource(target, source, true);
				revokedPowers.accumulateAndGet(revokedPowersFromSource, Integer::sum);

				if (revokedPowersFromSource > 0) {
					processedTargets.add(target);
				}

			}

			if (processedTargets.isEmpty()) {

				if (targets.size() == 1) {
					commandSource.sendFailure(Component.translatable("commands.apoli.revoke_all.fail.single", targets.getFirst().getName(), source.toString()));
				} else {
					commandSource.sendFailure(Component.translatableEscape("commands.apoli.revoke_all.fail.multiple", source));
				}

			} else {

				if (processedTargets.size() == 1) {
					commandSource.sendSuccess(() -> Component.translatable("commands.apoli.revoke_all.success.single", processedTargets.getFirst().getName(), revokedPowers.get(), source.toString()), true);
				} else {
					commandSource.sendSuccess(() -> Component.translatable("commands.apoli.revoke_all.success.multiple", processedTargets.size(), revokedPowers.get(), source.toString()), true);
				}

			}

			return revokedPowers.get();

		}

	}

	public static class ListNode {

		public static LiteralCommandNode<CommandSourceStack> get() {
			return literal("list")
				.executes(context -> execute(context, true, false))
				.then(argument("target", PowerHolderArgumentType.entities())
					.executes(context -> execute(context, false, false))
					.then(argument("subPowers", BoolArgumentType.bool())
						.executes(context -> execute(context, false, BoolArgumentType.getBool(context, "subPowers"))))).build();
		}

		public static int execute(CommandContext<CommandSourceStack> context, boolean self, boolean includeSubPowers) throws CommandSyntaxException {

			net.minecraft.commands.CommandSourceStack commandSource = (net.minecraft.commands.CommandSourceStack) context.getSource();
			Entity target = self
				? commandSource.getEntityOrException()
				: PowerHolderArgumentType.getHolder(context, "target");

			PowerHolderComponent powerComponent = PowerHolderComponent.KEY
				.maybeGet(target)
				.orElseThrow(() -> PowerHolderArgumentType.HOLDER_NOT_FOUND.create(target.getName()));

			List<Component> powersTooltip = new ObjectArrayList<>();
			for (Power power : powerComponent.getPowers(includeSubPowers)) {

				List<Component> sourcesTooltip = powerComponent.getSources(power)
					.stream()
					.map(Component::translationArg)
					.toList();

				Component joinedSourcesTooltip = Component.translatable("commands.apoli.list.sources", ComponentUtils.formatList(sourcesTooltip, Component.nullToEmpty(", ")));
				HoverEvent sourceHoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, joinedSourcesTooltip);

				powersTooltip.add(Component
					.literal(power.getId().toString())
					.setStyle(Style.EMPTY.withHoverEvent(sourceHoverEvent)));

			}

			if (powersTooltip.isEmpty()) {
				commandSource.sendFailure(Component.translatable("commands.apoli.list.fail", target.getName()));
			} else {
				commandSource.sendSuccess(() -> Component.translatable("commands.apoli.list.pass", target.getName(), powersTooltip.size(), ComponentUtils.formatList(powersTooltip, Component.nullToEmpty(", "))), false);
			}

			return powersTooltip.size();

		}

	}

	public static class HasNode {

		public static LiteralCommandNode<CommandSourceStack> get() {
			return literal("has")
				.then(argument("targets", PowerHolderArgumentType.entities())
					.then(argument("power", PowerArgumentType.power())
						.executes(HasNode::execute))).build();
		}

		public static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

			List<LivingEntity> targets = PowerHolderArgumentType.getHolders(context, "targets");
			Power power = PowerArgumentType.getPower(context, "power");

			net.minecraft.commands.CommandSourceStack commandSource = (net.minecraft.commands.CommandSourceStack) context.getSource();
			List<LivingEntity> processedTargets = targets.stream()
				.filter(target -> PowerHolderComponent.KEY.get(target).hasPower(power))
				.toList();

			if (processedTargets.isEmpty()) {

				if (targets.size() == 1) {
					commandSource.sendFailure(Component.translatable("commands.execute.conditional.fail"));
				} else {
					commandSource.sendFailure(Component.translatable("commands.execute.conditional.fail_count", targets.size()));
				}

			} else {

				if (processedTargets.size() == 1) {
					commandSource.sendSuccess(() -> Component.translatable("commands.execute.conditional.pass"), false);
				} else {
					commandSource.sendSuccess(() -> Component.translatable("commands.execute.conditional.pass_count", processedTargets.size()), false);
				}

			}

			return processedTargets.size();

		}

	}

	public static class SourcesNode {

		public static LiteralCommandNode<CommandSourceStack> get() {
			return literal("sources")
				.then(argument("target", PowerHolderArgumentType.entity())
					.then(argument("power", PowerArgumentType.power())
						.suggests(PowerSuggestionProvider.powersFromEntity("target"))
						.executes(SourcesNode::execute)
						.then(literal("")))).build();
		}

		public static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

			Entity target = PowerHolderArgumentType.getHolder(context, "target");
			Power power = PowerArgumentType.getPower(context, "power");

			net.minecraft.commands.CommandSourceStack commandSource = (net.minecraft.commands.CommandSourceStack) context.getSource();
			PowerHolderComponent powerComponent = PowerHolderComponent.KEY.get(target);

			List<ResourceLocation> sources = powerComponent.getSources(power);
			String joinedSources = sources
				.stream()
				.map(ResourceLocation::toString)
				.collect(Collectors.joining(", "));

			if (sources.isEmpty()) {
				commandSource.sendFailure(Component.translatable("commands.apoli.sources.fail", target.getName(), power.getName()));
			} else {
				commandSource.sendSuccess(() -> Component.translatable("commands.apoli.sources.pass", target.getName(), sources.size(), power.getName(), joinedSources), false);
			}

			return sources.size();

		}

	}

	public static class RemoveNode {

		public static LiteralCommandNode<CommandSourceStack> get() {
			return literal("remove")
				.then(argument("targets", PowerHolderArgumentType.entities())
					.then(argument("power", PowerArgumentType.power())
						.suggests(PowerSuggestionProvider.powersFromEntities("targets"))
						.executes(RemoveNode::execute)
						.then(literal("")))).build();
		}

		public static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

			List<LivingEntity> targets = PowerHolderArgumentType.getHolders(context, "targets");
			Power power = PowerArgumentType.getPower(context, "power");

			net.minecraft.commands.CommandSourceStack commandSource = (net.minecraft.commands.CommandSourceStack) context.getSource();
			List<LivingEntity> processedTargets = new ObjectArrayList<>();

			for (LivingEntity target : targets) {

				Map<ResourceLocation, Collection<Power>> powers = PowerHolderComponent.KEY.get(target).getSources(power)
					.stream()
					.collect(Collectors.toMap(Function.identity(), id -> ObjectOpenHashSet.of(power), Util.mergeCollections()));

				if (PowerHolderComponent.revokePowers(target, powers, true)) {
					processedTargets.add(target);
				}

			}

			if (processedTargets.isEmpty()) {

				if (targets.size() == 1) {
					commandSource.sendFailure(Component.translatable("commands.apoli.remove.fail.single", targets.getFirst().getName(), power.getName()));
				} else {
					commandSource.sendFailure(Component.translatable("commands.apoli.remove.fail.multiple", power.getName()));
				}

			} else {

				if (processedTargets.size() == 1) {
					commandSource.sendSuccess(() -> Component.translatable("commands.apoli.remove.success.single", processedTargets.getFirst().getName(), power.getName()), true);
				} else {
					commandSource.sendSuccess(() -> Component.translatable("commands.apoli.remove.success.multiple", processedTargets.size(), power.getName()), false);
				}

			}

			return processedTargets.size();

		}

	}

	public static class ClearNode {

		public static LiteralCommandNode<CommandSourceStack> get() {
			return literal("clear")
				.executes(context -> execute(context, true))
				.then(argument("targets", PowerHolderArgumentType.entities())
					.executes(context -> execute(context, false))).build();
		}

		public static int execute(CommandContext<CommandSourceStack> context, boolean self) throws CommandSyntaxException {

			List<Entity> targets = new ObjectArrayList<>();
			List<Entity> processedTargets = new ObjectArrayList<>();

			net.minecraft.commands.CommandSourceStack commandSource = (net.minecraft.commands.CommandSourceStack) context.getSource();
			AtomicInteger clearedPowers = new AtomicInteger();

			if (self) {

				Entity selfEntity = commandSource.getEntityOrException();

				PowerHolderComponent.KEY.maybeGet(selfEntity)
					.map(powerComponent -> targets.add(selfEntity))
					.orElseThrow(() -> PowerHolderArgumentType.HOLDER_NOT_FOUND.create(selfEntity.getName()));

			} else {
				targets.addAll(PowerHolderArgumentType.getHolders(context, "targets"));
			}

			for (Entity target : targets) {

				PowerHolderComponent component = PowerHolderComponent.KEY.get(target);
				List<ResourceLocation> sources = component.getPowers(false)
					.stream()
					.map(component::getSources)
					.flatMap(Collection::stream)
					.toList();

				if (sources.isEmpty()) {
					continue;
				}

				clearedPowers.accumulateAndGet(PowerHolderComponent.revokeAllPowersFromAllSources(target, sources, true), Integer::sum);
				processedTargets.add(target);

			}

			if (processedTargets.isEmpty()) {

				if (targets.size() == 1) {
					commandSource.sendFailure(Component.translatable("commands.apoli.clear.fail.single", targets.getFirst().getName()));
				} else {
					commandSource.sendFailure(Component.translatable("commands.apoli.clear.fail.multiple"));
				}

			} else {

				if (processedTargets.size() == 1) {
					commandSource.sendSuccess(() -> Component.translatable("commands.apoli.clear.success.single", processedTargets.getFirst().getName(), clearedPowers.get()), true);
				} else {
					commandSource.sendSuccess(() -> Component.translatable("commands.apoli.clear.success.multiple", processedTargets.size(), clearedPowers.get()), true);
				}

			}

			return clearedPowers.get();

		}

	}

	public static class DumpNode {

		public static LiteralCommandNode<CommandSourceStack> get() {
			return literal("dump")
				.then(argument("power", PowerArgumentType.power())
					.executes(context -> execute(context, 4))
					.then(argument("indent", IntegerArgumentType.integer(0))
						.executes(context -> execute(context, IntegerArgumentType.getInteger(context, "indent"))))).build();
		}

		public static int execute(CommandContext<CommandSourceStack> context, int indent) {

			Power power = PowerArgumentType.getPower(context, "power");
			net.minecraft.commands.CommandSourceStack commandSource = (net.minecraft.commands.CommandSourceStack) context.getSource();

			return Power.DATA_TYPE.write(commandSource.registryAccess().createSerializationContext(JsonOps.INSTANCE), power)
				.ifSuccess(powerJson -> commandSource.sendSuccess(() -> new JsonTextFormatter(indent).apply(powerJson), false))
				.ifError(error -> commandSource.sendFailure(Component.literal(error.message())))
				.mapOrElse(jsonElement -> 1, error -> 0);

		}

	}

}
