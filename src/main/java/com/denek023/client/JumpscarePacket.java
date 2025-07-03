package com.denek023.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class JumpscarePacket {
    public JumpscarePacket() {}

    public static void encode(JumpscarePacket msg, FriendlyByteBuf buf) {
    }

    public static JumpscarePacket decode(FriendlyByteBuf buf) {
        return new JumpscarePacket();
    }

    public static void handle(JumpscarePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            JumpscareOverlay.showJumpscare = true;
            JumpscareOverlay.jumpscareStartTime = System.currentTimeMillis();
        });
        ctx.get().setPacketHandled(true);
    }
}