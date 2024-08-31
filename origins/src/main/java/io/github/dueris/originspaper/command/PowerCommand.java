package io.github.dueris.originspaper.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.command.argument.PowerArgumentType;
import io.github.dueris.originspaper.command.argument.PowerHolderArgumentType;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.type.MultiplePower;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import io.github.dueris.originspaper.util.JsonTextFormatter;
import io.github.dueris.originspaper.util.LangFile;
import io.github.dueris.originspaper.util.Util;
import io.github.dueris.originspaper.util.entity.PowerUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import joptsimple.internal.Strings;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;

public class PowerCommand {

	public static ResourceLocation POWER_SOURCE = ResourceLocation.fromNamespaceAndPath("apoli", "command");

	public static void register(Commands dispatcher) {
		dispatcher.register(
			literal("power").requires(scs -> ((net.minecraft.commands.CommandSourceStack) scs).hasPermission(2))
				.then(literal("grant")
					.then(argument("targets", PowerHolderArgumentType.entities())
						.then(argument("power", PowerArgumentType.power())
							.executes(context -> grantPower(context, false))
							.then(argument("source", ResourceLocationArgument.id())
								.executes(context -> grantPower(context, true)))))
				)
				.then(Commands.literal("revoke")
					.then(Commands.argument("targets", PowerHolderArgumentType.entities())
						.then(Commands.argument("power", PowerArgumentType.power())
							.executes(context -> revokePower(context, false))
							.then(Commands.argument("source", ResourceLocationArgument.id())
								.executes(context -> revokePower(context, true))))
						.then(Commands.literal("all")
							.then(Commands.argument("source", ResourceLocationArgument.id())
								.executes(PowerCommand::revokeAllPowers))))
				)
				.then(literal("list")
					.then(argument("target", PowerHolderArgumentType.entity())
						.executes(context -> listPowers(context, false))
						.then(argument("subpowers", BoolArgumentType.bool())
							.executes(context -> listPowers(context, BoolArgumentType.getBool(context, "subpowers")))))
				)
				.then(literal("has")
					.then(argument("targets", PowerHolderArgumentType.entities())
						.then(argument("power", PowerArgumentType.power())
							.executes(PowerCommand::hasPower)))
				)
				.then(literal("sources")
					.then(argument("target", PowerHolderArgumentType.entity())
						.then(argument("power", PowerArgumentType.power())
							.executes(PowerCommand::getSourcesFromPower)))
				)
				.then(literal("remove")
					.then(argument("targets", PowerHolderArgumentType.entities())
						.then(argument("power", PowerArgumentType.power())
							.executes(PowerCommand::removePower)))
				)
				.then(literal("clear")
					.executes(context -> clearAllPowers(context, true))
					.then(argument("targets", PowerHolderArgumentType.entities())
						.executes(context -> clearAllPowers(context, false)))
				)
				.then(literal("dump")
					.then(argument("power", PowerArgumentType.power())
						.executes(context -> dumpPowerJson(context, false))
						.then(argument("indent", IntegerArgumentType.integer(0))
							.executes(context -> dumpPowerJson(context, true))))
				).build()
		);
	}

	private static int grantPower(@NotNull CommandContext<CommandSourceStack> context, boolean isSourceSpecified) throws CommandSyntaxException {

		net.minecraft.commands.CommandSourceStack source = (net.minecraft.commands.CommandSourceStack) context.getSource();

		List<LivingEntity> targets = PowerHolderArgumentType.getHolders(context, "targets");

		PowerType power = PowerArgumentType.getPower(context, "power");
		ResourceLocation powerSource = isSourceSpecified ? context.getArgument("source", ResourceLocation.class) : POWER_SOURCE;

		for (LivingEntity target : targets) {

			if (target instanceof ServerPlayer player) {
				PowerUtils.grantPower(source.getSender(), power, player.getBukkitEntity(), OriginsPaper.getLayer(powerSource), true);
			}

		}

		String powerTypeName = PlainTextComponentSerializer.plainText().serialize(power.name());
		String targetName = targets.getFirst().getName().getString();

		int targetsSize = targets.size();
		int processedTargetsSize = targets.size();

		if (processedTargetsSize == 0) {

			if (targetsSize == 1) {
				source.sendFailure(LangFile.translatable("commands.apoli.grant.fail.single", targetName, powerTypeName, powerSource.toString()));
			} else {
				source.sendFailure(LangFile.translatable("commands.apoli.grant.fail.multiple", targetsSize, powerTypeName, powerSource.toString()));
			}

			return processedTargetsSize;

		}

		String processedTargetName = targets.getFirst().getName().getString();
		if (isSourceSpecified) {
			if (processedTargetsSize == 1) {
				source.sendSuccess(() -> LangFile.translatable("commands.apoli.grant_from_source.success.single", processedTargetName, powerTypeName, powerSource.toString()), true);
			} else {
				source.sendSuccess(() -> LangFile.translatable("commands.apoli.grant_from_source.success.multiple", processedTargetsSize, powerTypeName, powerSource.toString()), true);
			}
		} else {
			if (processedTargetsSize == 1) {
				source.sendSuccess(() -> LangFile.translatable("commands.apoli.grant.success.single", processedTargetName, powerTypeName), true);
			} else {
				source.sendSuccess(() -> LangFile.translatable("commands.apoli.grant.success.multiple", processedTargetsSize, powerTypeName), true);
			}
		}

		return processedTargetsSize;

	}

	private static int revokePower(@NotNull CommandContext<CommandSourceStack> context, boolean isSourceSpecified) throws CommandSyntaxException {

		net.minecraft.commands.CommandSourceStack source = (net.minecraft.commands.CommandSourceStack) context.getSource();

		List<LivingEntity> targets = PowerHolderArgumentType.getHolders(context, "targets");

		PowerType power = PowerArgumentType.getPower(context, "power");
		ResourceLocation powerSource = isSourceSpecified ? context.getArgument("source", ResourceLocation.class) : POWER_SOURCE;

		for (LivingEntity target : targets) {

			if (target instanceof ServerPlayer player) {
				PowerUtils.removePower(source.getBukkitSender(), power, player.getBukkitEntity(), OriginsPaper.getLayer(powerSource), true);
			}

		}

		String powerTypeName = PlainTextComponentSerializer.plainText().serialize(power.name());

		int processedTargetsSize = targets.size();

		if (processedTargetsSize == 0) {

			source.sendFailure(LangFile.translatable("commands.apoli.revoke.fail.multiple", powerTypeName, powerSource.toString()));

			return processedTargetsSize;

		}

		String processedTargetName = targets.getFirst().getName().getString();
		if (isSourceSpecified) {
			if (processedTargetsSize == 1) {
				source.sendSuccess(() -> LangFile.translatable("commands.apoli.revoke_from_source.success.single", processedTargetName, powerTypeName, powerSource.toString()), true);
			} else {
				source.sendSuccess(() -> LangFile.translatable("commands.apoli.revoke_from_source.success.multiple", processedTargetsSize, powerTypeName, powerSource.toString()), true);
			}
		} else {
			if (processedTargetsSize == 1) {
				source.sendSuccess(() -> LangFile.translatable("commands.apoli.revoke.success.single", processedTargetName, powerTypeName), true);
			} else {
				source.sendSuccess(() -> LangFile.translatable("commands.apoli.revoke.success.multiple", processedTargetsSize, powerTypeName), true);
			}
		}

		return processedTargetsSize;

	}

	private static int revokeAllPowers(@NotNull CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

		net.minecraft.commands.CommandSourceStack source = (net.minecraft.commands.CommandSourceStack) context.getSource();

		List<LivingEntity> targets = PowerHolderArgumentType.getHolders(context, "targets");
		List<LivingEntity> processedTargets = new LinkedList<>();

		ResourceLocation powerSource = context.getArgument("source", ResourceLocation.class);
		int revokedPowers = 0;

		for (LivingEntity target : targets) {

			int revokedPowersFromSource = 0;
			OriginLayer layer = OriginsPaper.getLayer(powerSource);
			for (PowerType power : PowerHolderComponent.getPowersFromSource(layer, target.getBukkitEntity())) {
				PowerUtils.removePower(source.getBukkitSender(), power, (org.bukkit.entity.Player) target.getBukkitEntity(), layer, true);
				revokedPowersFromSource++;
			}
			revokedPowers += revokedPowersFromSource;

			if (revokedPowersFromSource > 0) {
				processedTargets.add(target);
			}

		}

		String targetName = targets.getFirst().getName().getString();

		int targetsSize = targets.size();
		int processedTargetsSize = processedTargets.size();

		if (processedTargetsSize == 0) {
			if (targetsSize == 1) {
				source.sendFailure(LangFile.translatable("commands.apoli.revoke_all.fail.single", targetName, powerSource.toString()));
			} else {
				source.sendFailure(LangFile.translatable("commands.apoli.revoke_all.fail.multiple", powerSource));
			}
		} else {

			String processedTargetName = processedTargets.getFirst().getName().getString();
			int finalRevokedPowers = revokedPowers;

			if (processedTargetsSize == 1) {
				source.sendSuccess(() -> LangFile.translatable("commands.apoli.revoke_all.success.single", processedTargetName, finalRevokedPowers, powerSource.toString()), true);
			} else {
				source.sendSuccess(() -> LangFile.translatable("commands.apoli.revoke_all.success.multiple", processedTargetsSize, finalRevokedPowers, powerSource.toString()), true);
			}

		}

		return processedTargetsSize;

	}

	private static int listPowers(@NotNull CommandContext<CommandSourceStack> context, boolean includeSubpowers) throws CommandSyntaxException {

		net.minecraft.commands.CommandSourceStack source = (net.minecraft.commands.CommandSourceStack) context.getSource();
		LivingEntity target = PowerHolderArgumentType.getHolder(context, "target");

		List<Component> powersTooltip = new LinkedList<>();
		int powers = 0;

		List<PowerType> powerTypes = new LinkedList<>(PowerHolderComponent.getPowers(target.getBukkitEntity()));
		List<MultiplePower> multiples = PowerHolderComponent.getPowers(target.getBukkitEntity(), MultiplePower.class);
		if (!includeSubpowers) {
			powerTypes.removeAll(Util.collapseList(multiples.stream().map(MultiplePower::getSubPowers).toList()));
		}

		for (PowerType power : powerTypes) {

			List<Component> sourcesTooltip = new LinkedList<>();
			PowerHolderComponent.getSources(power, target.getBukkitEntity()).forEach(id -> sourcesTooltip.add(Component.nullToEmpty(id.toString())));

			HoverEvent sourceHoverEvent = new HoverEvent(
				HoverEvent.Action.SHOW_TEXT,
				LangFile.translatable("commands.apoli.list.sources", ComponentUtils.formatList(sourcesTooltip, Component.nullToEmpty(",")))
			);

			Component powerTooltip = Component.literal(power.getId().toString())
				.setStyle(Style.EMPTY.withHoverEvent(sourceHoverEvent));

			powersTooltip.add(powerTooltip);
			powers++;

		}

		if (powers == 0) {
			source.sendFailure(LangFile.translatable("commands.apoli.list.fail", target.getName()));
		} else {
			int finalPowers = powers;
			source.sendSuccess(() -> LangFile.translatable("commands.apoli.list.pass", target.getName(), finalPowers, ComponentUtils.formatList(powersTooltip, Component.nullToEmpty(", "))), true);
		}

		return powers;

	}

	private static int hasPower(@NotNull CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

		net.minecraft.commands.CommandSourceStack source = (net.minecraft.commands.CommandSourceStack) context.getSource();

		List<LivingEntity> targets = PowerHolderArgumentType.getHolders(context, "targets");
		List<LivingEntity> processedTargets = new LinkedList<>();

		PowerType power = PowerArgumentType.getPower(context, "power");

		for (LivingEntity target : targets) {
			if (PowerHolderComponent.hasPower(target.getBukkitEntity(), power)) {
				processedTargets.add(target);
			}
		}

		int targetsSize = targets.size();
		int processedTargetsSize = processedTargets.size();

		if (processedTargetsSize == 0) {
			if (targetsSize == 1) {
				source.sendFailure(LangFile.translatable("commands.execute.conditional.fail"));
			} else {
				source.sendFailure(LangFile.translatable("commands.execute.conditional.fail_count", targetsSize));
			}
		} else {
			if (processedTargetsSize == 1) {
				source.sendSuccess(() -> LangFile.translatable("commands.execute.conditional.pass"), true);
			} else {
				source.sendSuccess(() -> LangFile.translatable("commands.execute.conditional.pass_count", processedTargetsSize), true);
			}
		}

		return processedTargets.size();

	}

	private static int getSourcesFromPower(@NotNull CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

		net.minecraft.commands.CommandSourceStack source = (net.minecraft.commands.CommandSourceStack) context.getSource();

		LivingEntity target = PowerHolderArgumentType.getHolder(context, "target");
		PowerType power = PowerArgumentType.getPower(context, "power");

		StringBuilder powerSources = new StringBuilder();
		int powers = 0;

		String separator = "";
		for (ResourceLocation powerSource : PowerHolderComponent.getSources(power, target.getBukkitEntity())) {

			powerSources.append(separator).append(powerSource.toString());
			powers++;

			separator = ", ";

		}

		if (powers == 0) {
			source.sendFailure(LangFile.translatable("commands.apoli.sources.fail", target.getName(), power.name()));
		} else {
			int finalPowers = powers;
			source.sendSuccess(() -> LangFile.translatable("commands.apoli.sources.pass", target.getName(), finalPowers, power.name(), powerSources.toString()), true);
		}

		return powers;

	}

	private static int removePower(@NotNull CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

		net.minecraft.commands.CommandSourceStack source = (net.minecraft.commands.CommandSourceStack) context.getSource();

		List<LivingEntity> targets = PowerHolderArgumentType.getHolders(context, "targets");
		List<LivingEntity> processedTargets = new LinkedList<>();

		PowerType power = PowerArgumentType.getPower(context, "power");
		for (LivingEntity target : targets) {
			if (!(target instanceof ServerPlayer player)) continue;

			AtomicInteger revokedPowers = new AtomicInteger();
			PowerHolderComponent.getSources(power, target.getBukkitEntity()).forEach(location -> {
				OriginLayer layer = OriginsPaper.getLayer(location);
				PowerUtils.removePower(source.getBukkitSender(), power, player.getBukkitEntity(), layer, true);

				revokedPowers.getAndIncrement();
			});

			if (revokedPowers.get() > 0) {
				processedTargets.add(target);
			}

		}

		String targetName = targets.getFirst().getName().getString();
		String powerTypeName = PlainTextComponentSerializer.plainText().serialize(power.name());

		int targetsSize = targets.size();
		int processedTargetsSize = processedTargets.size();

		if (processedTargetsSize == 0) {
			if (targetsSize == 1) {
				source.sendFailure(LangFile.translatable("commands.apoli.remove.fail.single", targetName, powerTypeName));
			} else {
				source.sendFailure(LangFile.translatable("commands.apoli.remove.fail.multiple", powerTypeName));
			}
		} else {
			String processedTargetName = processedTargets.getFirst().getName().getString();
			if (processedTargetsSize == 1) {
				source.sendSuccess(() -> LangFile.translatable("commands.apoli.remove.success.single", processedTargetName, powerTypeName), true);
			} else {
				source.sendSuccess(() -> LangFile.translatable("commands.apoli.remove.success.multiple", processedTargetsSize, powerTypeName), true);
			}
		}

		return processedTargetsSize;

	}

	private static int clearAllPowers(@NotNull CommandContext<CommandSourceStack> context, boolean onlyTargetSelf) throws CommandSyntaxException {

		net.minecraft.commands.CommandSourceStack source = (net.minecraft.commands.CommandSourceStack) context.getSource();

		List<LivingEntity> targets = new LinkedList<>();
		List<LivingEntity> processedTargets = new LinkedList<>();

		if (!onlyTargetSelf) {
			targets.addAll(PowerHolderArgumentType.getHolders(context, "targets"));
		} else {

			Entity self = source.getEntityOrException();
			if (!(self instanceof LivingEntity livingSelf)) {
				throw PowerHolderArgumentType.HOLDER_NOT_FOUND.create(self.getName());
			}

			targets.add(livingSelf);

		}

		int clearedPowers = 0;
		for (LivingEntity target : targets) {
			if (!(target instanceof ServerPlayer player)) continue;

			List<PowerType> powers = PowerHolderComponent.getPowers(target.getBukkitEntity());

			if (powers.isEmpty()) continue;

			for (PowerType power : powers) {
				PowerUtils.removePower(source.getBukkitSender(), power, player.getBukkitEntity(), null, true);
			}

			clearedPowers += powers.size();
			processedTargets.add(target);

		}

		String targetName = targets.getFirst().getName().getString();

		int targetsSize = targets.size();
		int processedTargetsSize = processedTargets.size();

		if (processedTargetsSize == 0) {
			if (targetsSize == 1) {
				source.sendFailure(LangFile.translatable("commands.apoli.clear.fail.single", targetName));
			} else {
				source.sendFailure(LangFile.translatable("commands.apoli.clear.fail.multiple"));
			}
		} else {

			String processedTargetName = processedTargets.getFirst().getName().getString();
			int finalClearedPowers = clearedPowers;

			if (processedTargetsSize == 1) {
				source.sendSuccess(() -> LangFile.translatable("commands.apoli.clear.success.single", processedTargetName, finalClearedPowers), true);
			} else {
				source.sendSuccess(() -> LangFile.translatable("commands.apoli.clear.success.multiple", processedTargetsSize, finalClearedPowers), true);
			}

		}

		return clearedPowers;

	}

	private static int dumpPowerJson(@NotNull CommandContext<CommandSourceStack> context, boolean indentSpecified) {

		net.minecraft.commands.CommandSourceStack source = (net.minecraft.commands.CommandSourceStack) context.getSource();
		PowerType power = PowerArgumentType.getPower(context, "power");

		String indent = Strings.repeat(' ', indentSpecified ? IntegerArgumentType.getInteger(context, "indent") : 4);
		source.sendSuccess(() -> {
			String append = ((net.minecraft.commands.CommandSourceStack) context.getSource()).isPlayer() ? "" : "\n";
			return Component.literal(append).append(new JsonTextFormatter(indent).apply(power.sourceObject));
		}, false);
		return 1;

	}
}
