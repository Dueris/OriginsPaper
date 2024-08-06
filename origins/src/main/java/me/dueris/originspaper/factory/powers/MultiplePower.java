package me.dueris.originspaper.factory.powers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.dueris.calio.data.AccessorKey;
import io.github.dueris.calio.parser.CalioParser;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.ParsingStrategy;
import io.github.dueris.calio.util.ReflectionUtils;
import io.github.dueris.calio.util.holder.ObjectProvider;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.condition.ConditionFactory;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.registry.registries.PowerType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MultiplePower extends PowerType {
	private final List<PowerType> subPowers = new ArrayList<>();

	public MultiplePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority) {
		super(key, type, name, description, hidden, condition, loadingPriority);
	}

	public static InstanceDefiner buildDefiner() {
		return PowerType.buildDefiner().typedRegistry(OriginsPaper.apoliIdentifier("multiple"));
	}

	@Override
	public void onBootstrap() {
		for (String key : sourceObject.keySet()) {
			JsonElement element = sourceObject.get(key);
			if (!element.isJsonObject()) continue;

			JsonObject jo = sourceObject.getAsJsonObject(key);
			AccessorKey<PowerType> accessorKey = new AccessorKey<>(List.of("apoli", "origins"), "power", PowerType.class, 0, ParsingStrategy.TYPED, Registries.CRAFT_POWER);

			ConcurrentLinkedQueue<Tuple<InstanceDefiner, Class<? extends PowerType>>> typedTempInstance = new ConcurrentLinkedQueue<>();
			final Class<? extends PowerType>[] defaultType = new Class[]{null};
			Class<PowerType> clz = accessorKey.strategy().equals(ParsingStrategy.DEFAULT) ? accessorKey.toBuild() :
				((ObjectProvider<Class<PowerType>>) () -> {
					try {
						ConcurrentLinkedQueue<Class<? extends PowerType>> instanceTypes = (ConcurrentLinkedQueue<Class<? extends PowerType>>) ReflectionUtils.getStaticFieldValue(accessorKey.toBuild(), "INSTANCE_TYPES");
						for (Class<? extends PowerType> instanceType : instanceTypes) {
							if (ReflectionUtils.hasMethod(instanceType, "buildDefiner", true)) {
								typedTempInstance.add(new Tuple<>(ReflectionUtils.invokeStaticMethod(instanceType, "buildDefiner"), instanceType));
							}
						}
						if (ReflectionUtils.hasField(accessorKey.toBuild(), "DEFAULT_TYPE", true)) {
							defaultType[0] = (Class<? extends PowerType>) ReflectionUtils.getStaticFieldValue(accessorKey.toBuild(), "DEFAULT_TYPE");
						}
					} catch (Throwable throwable) {
						throw new RuntimeException("Unable to parse INSTANCE_TYPES field for class '" + accessorKey.toBuild().getSimpleName() + "'");
					}
					return (Class<PowerType>) accessorKey.toBuild();
				}).get();
			Tuple<ResourceLocation, String> pathAndSource = new Tuple<>(
				ResourceLocation.fromNamespaceAndPath(key().getNamespace(), key().getPath() + "_" + key.toLowerCase(Locale.getDefault())), jo.toString());
			PowerType type = CalioParser.parseFile(pathAndSource, clz, accessorKey, defaultType, typedTempInstance);
			if (type != null) {
				subPowers.add(type);
			}
		}
	}

	public List<PowerType> getSubPowers() {
		return subPowers;
	}

}
