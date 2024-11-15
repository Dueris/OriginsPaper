package io.github.dueris.originspaper.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import io.github.dueris.originspaper.mixin.SlotRangesAccessor;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.inventory.SlotRange;
import net.minecraft.world.inventory.SlotRanges;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class SlotRangesUtil {

	public static final StreamCodec<ByteBuf, SlotRange> PACKET_CODEC = ByteBufCodecs.STRING_UTF8.map(SlotRanges::nameToIds, StringRepresentable::getSerializedName);
	private static final Codec<SlotRange> SINGLE_BY_INDEX_CODEC = Codec.INT.flatXmap(
		id -> {

			List<SlotRange> slotRanges = SlotRangesAccessor.getSlotRanges();

			for (SlotRange slotRange : slotRanges) {

				IntList slotIds = slotRange.slots();

				if (slotIds.size() == 1 && Objects.equals(slotIds.getFirst(), id)) {
					return DataResult.success(slotRange);
				}

			}

			return DataResult.error(() -> "Single slot range with ID  \"" + id + "\" is undefined!");

		},
		slotRange -> {
			int index = SlotRangesAccessor.getSlotRanges().indexOf(slotRange);
			return index == -1
				? DataResult.error(() -> "Unknown slot range \"" + slotRange.getSerializedName() + "\"!")
				: DataResult.success(index);
		}
	);
	public static final Codec<SlotRange> SINGLE_INDEX_OR_STRING_CODEC = new Codec<>() {

		@Override
		public <T> DataResult<Pair<SlotRange, T>> decode(@NotNull DynamicOps<T> ops, T input) {

			if (ops.getNumberValue(input).isSuccess()) {
				return SINGLE_BY_INDEX_CODEC.decode(ops, input);
			} else {
				return SlotRanges.CODEC.parse(ops, input)
					.flatMap(SlotRangesUtil::validateSingleSlot)
					.map(slotRange -> Pair.of(slotRange, input));
			}

		}

		@Override
		public <T> DataResult<T> encode(SlotRange input, DynamicOps<T> ops, T prefix) {
			return SlotRanges.CODEC.encode(input, ops, prefix);
		}

	};
	private static final Codec<SlotRange> BY_INDEX_CODEC = Codec.INT.flatXmap(
		id -> {

			List<SlotRange> slotRanges = SlotRangesAccessor.getSlotRanges();

			for (SlotRange slotRange : slotRanges) {

				IntList slotIds = slotRange.slots();

				for (int slotId : slotIds) {

					if (slotId == id) {
						return DataResult.success(slotRange);
					}

				}

			}

			return DataResult.error(() -> "Slot range with ID " + id + " is undefined!");

		},
		slotRange -> {
			int index = SlotRangesAccessor.getSlotRanges().indexOf(slotRange);
			return index == -1
				? DataResult.error(() -> "Unknown slot range \"" + slotRange.getSerializedName() + "\"!")
				: DataResult.success(index);
		}
	);
	public static final Codec<SlotRange> INDEX_OR_STRING_CODEC = new Codec<>() {

		@Override
		public <T> DataResult<Pair<SlotRange, T>> decode(@NotNull DynamicOps<T> ops, T input) {

			if (ops.getNumberValue(input).isSuccess()) {
				return BY_INDEX_CODEC.decode(ops, input);
			} else {
				return SlotRanges.CODEC.decode(ops, input);
			}

		}

		@Override
		public <T> DataResult<T> encode(SlotRange input, DynamicOps<T> ops, T prefix) {
			return SlotRanges.CODEC.encode(input, ops, prefix);
		}

	};

	public static DataResult<SlotRange> validateSingleSlot(@NotNull SlotRange slotRange) {
		return slotRange.size() == 1
			? DataResult.success(slotRange)
			: DataResult.error(() -> "Slot range \"" + slotRange + "\" has multiple slot IDs, which is not allowed!");
	}

}
