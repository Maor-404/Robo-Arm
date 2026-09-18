package com.maor.roboticarm.network;

import com.maor.roboticarm.RoboticArm;
import com.maor.roboticarm.blockentity.RoboticArmBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ArmControlPayload(BlockPos pos, int action) implements CustomPacketPayload {
    public static final Type<ArmControlPayload> TYPE = new Type<>(RoboticArm.id("arm_control"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ArmControlPayload> STREAM_CODEC =
            StreamCodec.composite(BlockPos.STREAM_CODEC, ArmControlPayload::pos, ByteBufCodecs.VAR_INT, ArmControlPayload::action, ArmControlPayload::new);
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(RoboticArm.MODID).versioned("1");
        registrar.playToServer(TYPE, STREAM_CODEC, ArmControlPayload::handle);
    }
    private static void handle(ArmControlPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (player.level().isLoaded(payload.pos()) && player.distanceToSqr(payload.pos().getX()+.5, payload.pos().getY()+.5, payload.pos().getZ()+.5) < 64
                    && player.level().getBlockEntity(payload.pos()) instanceof RoboticArmBlockEntity arm) {
                if (payload.action() == 0) arm.setRunning(true);
                else if (payload.action() == 1) arm.setRunning(false);
                else arm.setTask(RoboticArmBlockEntity.Task.values()[(arm.getTask().ordinal()+1)%3]);
            }
        });
    }
}
