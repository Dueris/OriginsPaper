package io.github.dueris.originspaper.mixin;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.server.commands.AdvancementCommands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(AdvancementCommands.class)
public interface AdvancementCommandsAccessor {

	@Invoker
	static void callAddChildren(AdvancementNode parent, List<AdvancementHolder> children) {
		throw new AssertionError();
	}

}
