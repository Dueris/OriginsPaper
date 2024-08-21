package io.github.dueris.originspaper.integration;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.dueris.originspaper.event.OriginChangeEvent;
import io.github.dueris.originspaper.screen.OriginPage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CraftPehuki implements Listener {
	public static void onLoad() {
		register(((CraftServer) Bukkit.getServer()).getServer().getCommands().getDispatcher());
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
			(Commands.literal("scale").requires(source -> source.hasPermission(3)))
				.then(
					(Commands.literal("set")
						.then(
							Commands.argument("scale", FloatArgumentType.floatArg())
								.executes(context -> execute(context, Attribute.GENERIC_SCALE))
						))
						.then(buildNew("motion", Attribute.GENERIC_MOVEMENT_SPEED))
						.then(buildNew("pehkui:motion", Attribute.GENERIC_MOVEMENT_SPEED))
						.then(buildNew("entity_reach", Attribute.PLAYER_ENTITY_INTERACTION_RANGE))
						.then(buildNew("pehkui:entity_reach", Attribute.PLAYER_ENTITY_INTERACTION_RANGE))
						.then(buildNew("block_reach", Attribute.PLAYER_BLOCK_INTERACTION_RANGE))
						.then(buildNew("pehkui:block_reach", Attribute.PLAYER_BLOCK_INTERACTION_RANGE))
						.then(Commands.literal("flight").then(flight()))
						.then(Commands.literal("pehkui:flight").then(flight()))
						.then(buildNew("knockback", Attribute.GENERIC_ATTACK_KNOCKBACK))
						.then(buildNew("scale", Attribute.GENERIC_SCALE))
						.then(buildNew("height", Attribute.GENERIC_SCALE))
						.then(buildNew("step-height", Attribute.GENERIC_STEP_HEIGHT))
						.then(buildNew("visibility", Attribute.GENERIC_FOLLOW_RANGE))
						.then(buildNew("pehkui:knockback", Attribute.GENERIC_ATTACK_KNOCKBACK))
						.then(buildNew("pehkui:scale", Attribute.GENERIC_SCALE))
						.then(buildNew("pehkui:height", Attribute.GENERIC_SCALE))
						.then(buildNew("pehkui:step-height", Attribute.GENERIC_STEP_HEIGHT))
						.then(buildNew("pehkui:visibility", Attribute.GENERIC_FOLLOW_RANGE))
						.then(ignored("width"))
						.then(ignored("pehkui:width"))
				)
				.then(Commands.literal("reset").executes(context -> {
					if (!context.getSource().isPlayer()) {
						return 0;
					} else {
						OriginPage.setAttributesToDefault(context.getSource().getPlayer());
						context.getSource().getPlayer().getBukkitEntity().getAttribute(Attribute.GENERIC_SCALE).setBaseValue(1.0);
						return 1;
					}
				}))
		);
	}

	private static RequiredArgumentBuilder<CommandSourceStack, Float> flight() {
		return Commands.argument("scale", FloatArgumentType.floatArg()).executes(context -> {
			try {
				if (context.getSource().isPlayer()) {
					ServerPlayer player = context.getSource().getPlayer();
					player.getBukkitEntity().setFlySpeed(FloatArgumentType.getFloat(context, "scale"));
				} else {
					context.getSource().sendFailure(Component.literal("This must be executed by a player!"));
				}
			} catch (Exception var2) {
				var2.printStackTrace();
			}

			return 1;
		});
	}

	private static LiteralArgumentBuilder<CommandSourceStack> buildNew(String e, Attribute attribute) {
		return Commands.literal(e)
			.then(Commands.argument("scale", FloatArgumentType.floatArg()).executes(context -> execute(context, attribute)));
	}

	private static LiteralArgumentBuilder<CommandSourceStack> ignored(String e) {
		return Commands.literal(e)
			.then(Commands.argument("scale", FloatArgumentType.floatArg()).executes(context -> 1));
	}

	private static int execute(CommandContext<CommandSourceStack> context, Attribute attribute) {
		try {
			if (context.getSource().isPlayer()) {
				ServerPlayer player = context.getSource().getPlayer();
				player.getBukkitEntity().getAttribute(attribute).setBaseValue(FloatArgumentType.getFloat(context, "scale"));
			} else {
				context.getSource().sendFailure(Component.literal("This must be executed by a player!"));
			}
		} catch (Exception var3) {
			var3.printStackTrace();
		}

		return 1;
	}

	@EventHandler
	public void resetScale(OriginChangeEvent e) {
		e.getPlayer().getAttribute(Attribute.GENERIC_SCALE).setBaseValue(1.0);
	}
}
