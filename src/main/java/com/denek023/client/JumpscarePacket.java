package com.denek023.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraft.client.Minecraft;
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
            Minecraft mc = Minecraft.getInstance();
            mc.setScreen(new com.denek023.client.JumpscareScreen());
        });
        ctx.get().setPacketHandled(true);
    }
}