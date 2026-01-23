package me.sirsam.item;

import me.sirsam.Forklift;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class ModItemGroup {
    public static final ResourceKey<@NotNull CreativeModeTab> MOD_ITEM_GROUP_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(Forklift.MOD_ID, "forklift_item_group"));
    public static final CreativeModeTab MOD_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.FUEL_CANISTER))
            .title(Component.translatable("itemGroup.forklift"))
            .build();

    public static void init() {
        Forklift.LOGGER.info("Registering mod item group...");

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, MOD_ITEM_GROUP_KEY, MOD_ITEM_GROUP);
    }

    public static void addItem(Item item) {
        ItemGroupEvents.modifyEntriesEvent(MOD_ITEM_GROUP_KEY)
                .register((itemGroup) -> itemGroup.accept(item));
    }
}
