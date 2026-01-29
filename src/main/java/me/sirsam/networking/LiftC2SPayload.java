package me.sirsam.networking;

import me.sirsam.Forklift;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record LiftC2SPayload(LiftAction action) implements CustomPacketPayload {

    public enum LiftAction { UP, DOWN }

    public static final Identifier LIFT_ID = Identifier.fromNamespaceAndPath(Forklift.MOD_ID, "lift_action");
    public static final CustomPacketPayload.Type<@NotNull LiftC2SPayload> ID = new CustomPacketPayload.Type<>(LIFT_ID);

    public static final StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull LiftC2SPayload> CODEC = new StreamCodec<>() {
        @Override
        public LiftC2SPayload decode(RegistryFriendlyByteBuf buf) {
            return new LiftC2SPayload(LiftAction.values()[buf.readVarInt()]);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, LiftC2SPayload payload) {
            buf.writeVarInt(payload.action().ordinal());
        }
    };

    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return ID;
    }
}