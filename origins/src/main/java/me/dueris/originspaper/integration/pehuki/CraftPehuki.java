package me.dueris.originspaper.integration.pehuki;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.dueris.originspaper.event.OriginChangeEvent;
import me.dueris.originspaper.screen.OriginPage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class CraftPehuki implements Listener {
	public static void onLoad() {
		register(((CraftServer) Bukkit.getServer()).getServer().getCommands().getDispatcher());
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
			literal("scale").requires(source -> source.hasPermission(3))
				.then(literal("set")
					.then(argument("scale", FloatArgumentType.floatArg()).executes(context -> execute(context, Attribute.GENERIC_SCALE)))
					.then(buildNew("motion", Attribute.GENERIC_MOVEMENT_SPEED))
					.then(buildNew("pehkui:motion", Attribute.GENERIC_MOVEMENT_SPEED)) // Support namespaced
					.then(buildNew("entity_reach", Attribute.PLAYER_ENTITY_INTERACTION_RANGE))
					.then(buildNew("pehkui:entity_reach", Attribute.PLAYER_ENTITY_INTERACTION_RANGE)) // Support namespaced
					.then(buildNew("block_reach", Attribute.PLAYER_BLOCK_INTERACTION_RANGE))
					.then(buildNew("pehkui:block_reach", Attribute.PLAYER_BLOCK_INTERACTION_RANGE)) // Support namespaced
					.then(literal("flight").then(flight()))
					.then(literal("pehkui:flight").then(flight()))
					.then(buildNew("knockback", Attribute.GENERIC_ATTACK_KNOCKBACK))
					.then(buildNew("scale", Attribute.GENERIC_SCALE))
					.then(buildNew("height", Attribute.GENERIC_SCALE))
					.then(buildNew("step-height", Attribute.GENERIC_STEP_HEIGHT))
					.then(buildNew("visibility", Attribute.GENERIC_FOLLOW_RANGE))
					.then(buildNew("pehkui:knockback", Attribute.GENERIC_ATTACK_KNOCKBACK)) // Support namespaced
					.then(buildNew("pehkui:scale", Attribute.GENERIC_SCALE)) // Support namespaced
					.then(buildNew("pehkui:height", Attribute.GENERIC_SCALE)) // Support namespaced
					.then(buildNew("pehkui:step-height", Attribute.GENERIC_STEP_HEIGHT)) // Support namespaced
					.then(buildNew("pehkui:visibility", Attribute.GENERIC_FOLLOW_RANGE)) // Support namespaced
				).then(literal("reset").executes(context -> {
					if (!context.getSource().isPlayer()) return 0; // Only players please
					OriginPage.setAttributesToDefault(context.getSource().getPlayer());
					context.getSource().getPlayer().getBukkitEntity().getAttribute(Attribute.GENERIC_SCALE).setBaseValue(1);
					return SINGLE_SUCCESS;
				}))
		);
	}

	private static RequiredArgumentBuilder<CommandSourceStack, Float> flight() {
		return argument("scale", FloatArgumentType.floatArg())
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
			});
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

	@EventHandler
	public void resetScale(OriginChangeEvent e) {
		e.getPlayer().getAttribute(Attribute.GENERIC_SCALE).setBaseValue(1);
	}
}
