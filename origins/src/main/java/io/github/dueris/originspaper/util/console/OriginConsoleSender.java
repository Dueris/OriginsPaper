package io.github.dueris.originspaper.util.console;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.craftbukkit.conversations.ConversationTracker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class OriginConsoleSender extends OriginServerCommandSender implements ConsoleCommandSender {
	protected final ConversationTracker conversationTracker = new ConversationTracker();

	public OriginConsoleSender() {
		super();
	}

	@Override
	public void sendMessage(String message) {
	}

	public void sendRawMessage(String message) {
	}

	public void sendRawMessage(UUID sender, String message) {
	}

	@Override
	public void sendMessage(String... messages) {
	}

	@Override
	public @NotNull String getName() {
		return "ORIGINS";
	}

	@Override
	public net.kyori.adventure.text.Component name() {
		return net.kyori.adventure.text.Component.text(this.getName());
	}

	@Override
	public boolean isOp() {
		return true;
	}

	@Override
	public void setOp(boolean value) {
	}

	@Override
	public boolean beginConversation(Conversation conversation) {
		return this.conversationTracker.beginConversation(conversation);
	}

	@Override
	public void abandonConversation(Conversation conversation) {
//        this.conversationTracker.abandonConversation(conversation, new ConversationAbandonedEvent(conversation, new ManuallyAbandonedConversationCanceller()));
	}

	@Override
	public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {
//        this.conversationTracker.abandonConversation(conversation, details);
	}

	@Override
	public void acceptConversationInput(String input) {
		this.conversationTracker.acceptConversationInput(input);
	}

	@Override
	public boolean isConversing() {
		return this.conversationTracker.isConversing();
	}

	@Override
	public boolean hasPermission(String name) {
		return true;
	}

	@Override
	public boolean hasPermission(org.bukkit.permissions.Permission perm) {
		return true;
	}

	public static class NMSSender {

		public static void executeNMSCommand(@Nullable Entity entity, Vec3 hitPosition, String command) {
			if (command != null) {
				MinecraftServer server = entity.getServer();
				if (server != null) {
					CommandSourceStack source = new CommandSourceStack(
						entity == null ? CommandSource.NULL : entity,
						hitPosition,
						entity.getRotationVector(),
						entity.level() instanceof ServerLevel ? (ServerLevel) entity.level() : null,
						4,
						entity.getName().getString(),
						entity.getName(),
						entity.getServer(),
						entity
					)
						.withSuppressedOutput();

					try {
						server.getCommands().performPrefixedCommand(source, command);
					} catch (Exception var8) {
						try {
							OriginConsoleSender serverCommandSender = new OriginConsoleSender();
							Bukkit.dispatchCommand(serverCommandSender, command);
						} catch (Exception var7) {
							var7.printStackTrace();
						}
					}
				}
			}
		}
	}
}
