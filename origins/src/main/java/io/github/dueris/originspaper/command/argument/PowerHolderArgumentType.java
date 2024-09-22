package io.github.dueris.originspaper.command.argument;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class PowerHolderArgumentType extends EntityArgument {

	public static final SimpleCommandExceptionType HOLDERS_NOT_FOUND = new SimpleCommandExceptionType(
		Component.literal("No living entities were found")
	);
	public static final DynamicCommandExceptionType HOLDER_NOT_FOUND = new DynamicCommandExceptionType(
		o -> Component.literal("Entity is not a living entity")
	);

	protected PowerHolderArgumentType(boolean singleTarget, boolean playersOnly) {
		super(singleTarget, playersOnly);
	}

	public static LivingEntity getHolder(CommandContext<io.papermc.paper.command.brigadier.CommandSourceStack> context, String name) throws CommandSyntaxException {

		Entity entity = context.getArgument(name, EntitySelector.class).findSingleEntity((net.minecraft.commands.CommandSourceStack) context.getSource());
		if (!(entity instanceof LivingEntity livingEntity)) {
			throw HOLDER_NOT_FOUND.create(entity.getName());
		}

		return livingEntity;

	}

	public static @NotNull List<LivingEntity> getHolders(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {

		List<? extends Entity> entities = new LinkedList<>(entities(context, name));
		List<LivingEntity> holders = entities.stream()
			.filter(e -> e instanceof LivingEntity)
			.map(e -> (LivingEntity) e)
			.toList();

		if (holders.isEmpty()) {

			if (entities.size() == 1) {
				throw HOLDER_NOT_FOUND.create(entities.getFirst().getName());
			} else {
				throw HOLDERS_NOT_FOUND.create();
			}

		}

		return holders;

	}

	public static @NotNull Collection<? extends Entity> entities(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
		Collection<? extends Entity> collection = optionalEntities(context, name);

		if (collection.isEmpty()) {
			throw EntityArgument.NO_ENTITIES_FOUND.create();
		} else {
			return collection;
		}
	}

	public static @NotNull Collection<? extends Entity> optionalEntities(@NotNull CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
		return context.getArgument(name, EntitySelector.class).findEntities((net.minecraft.commands.CommandSourceStack) context.getSource());
	}

}
