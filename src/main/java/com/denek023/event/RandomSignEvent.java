package com.denek023.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

@Mod.EventBusSubscriber
public class RandomSignEvent {
    private static final String[] SIGN_LINES = {
        "Why Me?...",
        "Is it me?",
        "I trusted him...",
        "He betrayed me...",
        "I want to die...",
        "I want to kill everyone...",
        "I want to be free...",
        "Why am I still alive?",
        "I hate you all",
        "No escape",
        "He is watching",
        "Don't look back",
        "Help me",
        "It's too late",
        "You can't run",
        "He is near"
    };
    private static final HashMap<UUID, Integer> signCooldowns = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.level().isClientSide) return;
        ServerPlayer player = (ServerPlayer) event.player;
        UUID id = player.getUUID();
        int cooldown = signCooldowns.getOrDefault(id, 0);
        if (cooldown > 0) {
            signCooldowns.put(id, cooldown - 1);
            return;
        }
        Level level = player.level();

        if (new Random().nextDouble() < 0.001) {
            signCooldowns.put(id, 800);
            Random rand = new Random();

            double angle = rand.nextDouble() * 2 * Math.PI;
            int dx = (int) Math.round(Math.cos(angle) * 5);
            int dz = (int) Math.round(Math.sin(angle) * 5);
            int x = (int) player.getX() + dx;
            int y = (int) player.getY();
            int z = (int) player.getZ() + dz;
            BlockPos pos = new BlockPos(x, y, z);

            if (level.isEmptyBlock(pos) && !level.isEmptyBlock(pos.below())) {
                BlockState signState = Blocks.OAK_SIGN.defaultBlockState();
                level.setBlock(pos, signState, 3);

                if (level.getBlockEntity(pos) instanceof SignBlockEntity sign) {
                    String line = SIGN_LINES[rand.nextInt(SIGN_LINES.length)];
                    int maxLen = 15;
                    String[] splitLines = new String[4];
                    for (int i = 0; i < 4; i++) splitLines[i] = "";

                    String[] words = line.split(" ");
                    int idx = 0;
                    for (String word : words) {
                        if (splitLines[idx].length() + word.length() + 1 > maxLen) {
                            idx++;
                            if (idx >= 4) break;
                        }
                        if (!splitLines[idx].isEmpty()) splitLines[idx] += " ";
                        splitLines[idx] += word;
                    }

                    net.minecraft.nbt.CompoundTag tag = sign.saveWithFullMetadata();
                    net.minecraft.nbt.ListTag messages = new net.minecraft.nbt.ListTag();
                    for (int i = 0; i < 4; i++) {
                        messages.add(net.minecraft.nbt.StringTag.valueOf(
                            net.minecraft.network.chat.Component.Serializer.toJson(
                                net.minecraft.network.chat.Component.literal(splitLines[i])
                            )
                        ));
                    }
                    net.minecraft.nbt.CompoundTag frontText = tag.getCompound("front_text");
                    frontText.put("messages", messages);
                    tag.put("front_text", frontText);

                    sign.load(tag);
                    sign.setChanged();
                    level.sendBlockUpdated(pos, sign.getBlockState(), sign.getBlockState(), 3);
                }
            }
        }
    }
}