package me.dueris.originspaper.factory.actions.types;

public class ItemActions {

	public void register() {
		/*register(new ActionFactory(OriginsPaper.apoliIdentifier("consume"), (data, itemStack) -> {
			Util.consumeItem(itemStack.getSecond());
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("damage"), (data, itemStack) -> {
			net.minecraft.world.item.ItemStack stack = CraftItemStack.unwrap(itemStack.getSecond());
			int damageAmount = data.getNumberOrDefault("amount", 1).getInt();

			if (data.getBooleanOrDefault("ignore_unbreaking", false)) {

				if (damageAmount >= stack.getMaxDamage()) {
					stack.shrink(1);
				} else {
					stack.setDamageValue(stack.getDamageValue() + damageAmount);
				}

			} else {
				stack.hurtAndBreak(damageAmount, itemStack.getFirst(), null, item -> {
				});
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("merge_custom_data"), (data, itemStack) -> {
			net.minecraft.world.item.ItemStack stack = CraftItemStack.unwrap(itemStack.getSecond());
			CompoundTag newNbt = data.transformWithCalio("nbt", CalioDataTypes::compoundTag);

			CustomData.update(DataComponents.CUSTOM_DATA, stack, oldNbt -> oldNbt.merge(newNbt));
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("remove_enchantment"), (data, itemStack) -> {
			net.minecraft.world.item.ItemStack stack = CraftItemStack.unwrap(itemStack.getSecond());
			ServerLevel world = itemStack.getFirst();

			if (!stack.isEnchanted()) {
				return;
			}

			List<ResourceKey<Enchantment>> enchantmentKeys = new LinkedList<>();
			List<ResourceLocation> locations = new ArrayList<>();

			if (data.isPresent("enchantment")) {
				locations.add(data.getResourceLocation("enchantment"));
			}
			if (data.isPresent("enchantments")) {
				locations.addAll(data.getJsonArray("enchantments").asList.stream().map(FactoryElement::getString).map(ResourceLocation::parse).toList());
			}

			for (ResourceLocation location : locations) {
				enchantmentKeys.add(ResourceKey.create(net.minecraft.core.registries.Registries.ENCHANTMENT, location));
			}

			ItemEnchantments component = stack.getEnchantments();
			ItemEnchantments.Mutable componentBuilder = new ItemEnchantments.Mutable(component);

			Integer levels = data.isPresent("levels")
				? data.getNumber("levels").getInt()
				: null;

			if (!enchantmentKeys.isEmpty()) {
				for (ResourceKey<Enchantment> enchantmentKey : enchantmentKeys) {

					Holder<Enchantment> enchantmentEntry = world.registryAccess()
						.registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
						.getHolder(enchantmentKey)
						.orElseThrow();

					if (component.keySet().contains(enchantmentEntry)) {
						componentBuilder.set(enchantmentEntry, levels != null ? component.getLevel(enchantmentEntry) - levels : 0);
					}

				}

			} else {

				for (Holder<Enchantment> enchantment : component.keySet()) {
					componentBuilder.set(enchantment, levels != null ? component.getLevel(enchantment) - levels : 0);
				}

			}

			stack.set(DataComponents.ENCHANTMENTS, componentBuilder.toImmutable());
			if (data.getBoolean("reset_repair_cost") && !stack.isEnchanted()) {
				stack.set(DataComponents.REPAIR_COST, 0);
			}
		}));*/
	}

//	public void register(ItemActions.ActionFactory factory) {
//		OriginsPaper.getPlugin().registry.retrieve(Registries.ITEM_ACTION).register(factory);
//	}

}
