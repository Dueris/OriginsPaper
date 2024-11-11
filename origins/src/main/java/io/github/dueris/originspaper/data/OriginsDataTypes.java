package io.github.dueris.originspaper.data;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.origin.Impact;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.origin.OriginUpgrade;

import java.util.List;
import java.util.Optional;

public final class OriginsDataTypes {

	public static final SerializableDataType<Impact> IMPACT = SerializableDataType.enumValue(Impact.class);

	@Deprecated(forRemoval = true)
	public static final SerializableDataType<OriginUpgrade> UPGRADE = OriginUpgrade.DATA_TYPE;

	@Deprecated(forRemoval = true)
	public static final SerializableDataType<List<OriginUpgrade>> UPGRADES = UPGRADE.list();

	public static final SerializableDataType<OriginLayer.ConditionedOrigin> CONDITIONED_ORIGIN = OriginLayer.ConditionedOrigin.DATA_TYPE;

	public static final SerializableDataType<List<OriginLayer.ConditionedOrigin>> CONDITIONED_ORIGINS = CONDITIONED_ORIGIN.list();

	public static final SerializableDataType<OriginLayer.ConditionedOrigin> ORIGIN_OR_CONDITIONED_ORIGIN = SerializableDataType.of(
		new Codec<>() {

			@Override
			public <T> DataResult<Pair<OriginLayer.ConditionedOrigin, T>> decode(DynamicOps<T> ops, T input) {

				if (ops.getStringValue(input).isSuccess()) {
					return SerializableDataTypes.IDENTIFIER.codec().decode(ops, input)
						.map(idAndInput -> idAndInput
							.mapFirst(id -> new OriginLayer.ConditionedOrigin(Optional.empty(), Lists.newArrayList(id))));
				} else {
					return CONDITIONED_ORIGIN.codec().decode(ops, input);
				}

			}

			@Override
			public <T> DataResult<T> encode(OriginLayer.ConditionedOrigin input, DynamicOps<T> ops, T prefix) {

				if (input.condition() == null && input.origins().size() == 1) {
					return SerializableDataTypes.IDENTIFIER.write(ops, input.origins().getFirst());
				} else {
					return CONDITIONED_ORIGIN.write(ops, input);
				}

			}

		}
	);

	public static final SerializableDataType<List<OriginLayer.ConditionedOrigin>> ORIGINS_OR_CONDITIONED_ORIGINS = ORIGIN_OR_CONDITIONED_ORIGIN.list();

	public static final SerializableDataType<OriginLayer.GuiTitle> GUI_TITLE = OriginLayer.GuiTitle.DATA_TYPE;

}
