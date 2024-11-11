package io.github.dueris.originspaper.command.argument.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.command.argument.PowerArgumentType;
import io.github.dueris.originspaper.command.argument.PowerHolderArgumentType;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerManager;
import io.github.dueris.originspaper.util.PowerUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings("UnstableApiUsage")
public record PowerSuggestionProvider(Function<CommandContext<CommandSourceStack>, Collection<Entity>> getter,
									  PowerArgumentType.PowerTarget targetType) implements SuggestionProvider<CommandSourceStack> {

	private static @NotNull PowerSuggestionProvider entity(String entityArgumentName, PowerArgumentType.PowerTarget targetType) {
		return new PowerSuggestionProvider(context -> {

			try {
				return List.of(PowerHolderArgumentType.getHolder(context, entityArgumentName));
			} catch (IllegalArgumentException iae) {
				OriginsPaper.LOGGER.warn("Something went wrong trying to get an entity from argument \"{}\": ", entityArgumentName, iae);
				throw iae;
			} catch (CommandSyntaxException e) {
				throw new IllegalStateException(e);
			}

		}, targetType);
	}

	private static @NotNull PowerSuggestionProvider entities(String entitiesArgumentName, PowerArgumentType.PowerTarget targetType) {
		return new PowerSuggestionProvider(context -> {

			try {
				return PowerHolderArgumentType.getHolders(context, entitiesArgumentName)
					.stream()
					.map(Entity.class::cast)
					.toList();
			} catch (IllegalArgumentException iae) {
				OriginsPaper.LOGGER.warn("Something went wrong trying to get entities from argument \"{}\": ", entitiesArgumentName, iae);
				throw iae;
			} catch (CommandSyntaxException e) {
				throw new IllegalStateException(e);
			}

		}, targetType);
	}

	public static @NotNull PowerSuggestionProvider powersFromEntity(String entityArgumentName) {
		return entity(entityArgumentName, PowerArgumentType.PowerTarget.GENERAL);
	}

	public static @NotNull PowerSuggestionProvider powersFromEntities(String entitiesArgumentName) {
		return entities(entitiesArgumentName, PowerArgumentType.PowerTarget.GENERAL);
	}

	public static @NotNull PowerSuggestionProvider resourcesFromEntity(String entityArgumentName) {
		return entity(entityArgumentName, PowerArgumentType.PowerTarget.RESOURCE);
	}

	public static @NotNull PowerSuggestionProvider resourcesFromEntities(String entitiesArgumentName) {
		return entities(entitiesArgumentName, PowerArgumentType.PowerTarget.RESOURCE);
	}

	@Override
	public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {

		try {

			Stream.Builder<ResourceLocation> powerIds = Stream.builder();
			Collection<Entity> entities = getter().apply(context);

			for (Entity entity : entities) {

				PowerHolderComponent powerComponent = PowerHolderComponent.KEY.get(entity);
				for (Map.Entry<ResourceLocation, Power> powerEntry : PowerManager.entrySet()) {

					ResourceLocation id = powerEntry.getKey();
					Power power = powerEntry.getValue();

					try {

						if (powerComponent.hasPower(power) && isAllowed(power)) {
							powerIds.add(id);
						}

					} catch (Exception e) {
						OriginsPaper.LOGGER.error("Error trying to put power \"{}\" in the suggestion provider (skipping): {}", id, e);
					}

				}

			}

			return SharedSuggestionProvider.suggestResource(powerIds.build(), builder);

		} catch (Exception e) {
			return Suggestions.empty();
		}

	}

	private boolean isAllowed(Power power) {
		return targetType() != PowerArgumentType.PowerTarget.RESOURCE
			|| PowerUtil.validateResource(power.getPowerType()).isSuccess();
	}

}
