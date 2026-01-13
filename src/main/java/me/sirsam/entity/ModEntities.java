package me.sirsam.entity;

import me.sirsam.Forklift;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.jetbrains.annotations.NotNull;

public final class ModEntities {
    public static final EntityType<@NotNull ForkliftEntity> FORKLIFT= register("forklift", EntityType.Builder.of(ForkliftEntity::new, MobCategory.MISC).sized(2f, 2f));

    private static <T extends Entity> EntityType<@NotNull T> register(String name, EntityType.Builder<@NotNull T> builder) {
        ResourceKey<@NotNull EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Forklift.MOD_ID, name));
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
    }


    public static void init() {
        FabricDefaultAttributeRegistry.register(FORKLIFT, ForkliftEntity.createCubeAttributes());
        Forklift.LOGGER.info("Registering mod entities...");
    }
}
