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

import java.util.concurrent.ConcurrentHashMap;
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
    
    private static final int COOLDOWN_TICKS = 3000;
    private static final int MAX_LINE_LENGTH = 15;
    private static final int MAX_DISTANCE = 2;
    private static final double SPAWN_CHANCE = 0.001;
    
    private static final Random RANDOM = new Random();
    private static final ConcurrentHashMap<UUID, Integer> signCooldowns = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.level().isClientSide) return;
        
        try {
            ServerPlayer player = (ServerPlayer) event.player;
            UUID id = player.getUUID();
            int cooldown = signCooldowns.getOrDefault(id, 0);
            
            if (cooldown > 0) {
                signCooldowns.put(id, cooldown - 1);
                return;
            }
            
            Level level = player.level();
            
            if (RANDOM.nextDouble() < SPAWN_CHANCE) {
                signCooldowns.put(id, COOLDOWN_TICKS);

                double angle = RANDOM.nextDouble() * 2 * Math.PI;
                int dx = (int) Math.round(Math.cos(angle) * MAX_DISTANCE);
                int dz = (int) Math.round(Math.sin(angle) * MAX_DISTANCE);
                int x = (int) player.getX() + dx;
                int y = (int) player.getY();
                int z = (int) player.getZ() + dz;
                BlockPos pos = new BlockPos(x, y, z);

                if (level.isEmptyBlock(pos) && !level.isEmptyBlock(pos.below())) {
                    BlockState signState = Blocks.OAK_SIGN.defaultBlockState();
                    level.setBlock(pos, signState, 3);

                    if (level.getBlockEntity(pos) instanceof SignBlockEntity sign) {
                        String line = SIGN_LINES[RANDOM.nextInt(SIGN_LINES.length)];
                        String[] splitLines = splitTextIntoLines(line, MAX_LINE_LENGTH);

                        net.minecraft.nbt.CompoundTag tag = sign.saveWithFullMetadata();
                        net.minecraft.nbt.ListTag messages = new net.minecraft.nbt.ListTag();
                        for (String text : splitLines) {
                            messages.add(net.minecraft.nbt.StringTag.valueOf(
                                net.minecraft.network.chat.Component.Serializer.toJson(
                                    net.minecraft.network.chat.Component.literal(text)
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
        } catch (Exception e) {
            System.err.println("Error in RandomSignEvent: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String[] splitTextIntoLines(String text, int maxLength) {
        String[] splitLines = new String[4];
        for (int i = 0; i < 4; i++) splitLines[i] = "";

        String[] words = text.split(" ");
        int idx = 0;
        
        for (String word : words) {
            if (splitLines[idx].length() + word.length() + 1 > maxLength) {
                idx++;
                if (idx >= 4) break;
            }
            if (!splitLines[idx].isEmpty()) splitLines[idx] += " ";
            splitLines[idx] += word;
        }
        
        return splitLines;
    }
}