package me.sirsam.item;

import me.sirsam.Forklift;
import me.sirsam.entity.ModEntities;
import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public final class ModItems {
    public static final Item FUEL_CANISTER = register("fuel_canister", Item::new, new Item.Properties());
    public static final Item FORKLIFT_SPAWN_EGG = register("forklift_spawn_egg", SpawnEggItem::new, new Item.Properties().spawnEgg(ModEntities.FORKLIFT));
    public static final Item SUSPICIOUS_SUBSTANCE = register("suspicious_substance", Item::new, new Item.Properties());

    public static <GenericItem extends Item> GenericItem register(String name, Function<Item.Properties, GenericItem> itemFactory, Item.Properties settings) {
        ResourceKey<@NotNull Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Forklift.MOD_ID, name));

        GenericItem item = itemFactory.apply(settings.setId(key));

        Registry.register(BuiltInRegistries.ITEM, key, item);

        ModItemGroup.addItem(item);

        return item;
    }

    public static void init() {
        Forklift.LOGGER.info("Registering mod items...");

        FuelRegistryEvents.BUILD.register((builder, context) -> {
            builder.add(FUEL_CANISTER, 30*20);
        });
    }
}