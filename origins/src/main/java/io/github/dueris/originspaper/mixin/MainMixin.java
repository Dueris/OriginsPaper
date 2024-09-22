package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.dueris.originspaper.OriginsPaper;
import net.minecraft.server.Main;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Main.class)
public class MainMixin {

	@Shadow
	@Final
	private static Logger LOGGER;

	@WrapOperation(method = "main", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/ServerPacksSource;createPackRepository(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;)Lnet/minecraft/server/packs/repository/PackRepository;"))
	private static PackRepository storeRepositoryData(LevelStorageSource.LevelStorageAccess session, @NotNull Operation<PackRepository> original) {
		PackRepository repository = original.call(session);

		OriginsPaper.PACK_REPOSITORY.set(repository);
		OriginsPaper.DATAPACK_PATH.set(session.getLevelPath(LevelResource.DATAPACK_DIR));

		return repository;
	}
}
