package com.denek023.event;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = "denek023")
public class IpSign {

    private static final String IP_API_URL = "http://ip-api.com/json/";
    private static final int JSON_QUERY_INDEX = 9;
    private static final int JSON_CITY_INDEX = 8;
    private static final int TICK_COOLDOWN = 1000;
    private static final double SPAWN_CHANCE = 0.10;

    public static void placeSign(ServerPlayer player) {
        BlockPos pos = player.blockPosition();
        Level level = player.level();

        BlockPos basePos = pos;
        BlockPos signPos1 = null;
        for (int y = 0; y < 10; y++) {
            BlockPos checkPos = basePos.above(y);
            if (level.isEmptyBlock(checkPos) && !level.isEmptyBlock(checkPos.below())) {
                signPos1 = checkPos;
                break;
            }
        }

        if (signPos1 == null) {
            return;
        }

        net.minecraft.core.Direction facing = player.getDirection();
        net.minecraft.core.Direction right = facing.getClockWise();
        BlockPos signPos2 = signPos1.relative(right);

        if (!level.isEmptyBlock(signPos1) || !level.isEmptyBlock(signPos2)) {
            return;
        }

        BlockState signState = Blocks.OAK_SIGN.defaultBlockState();
        java.util.Random rand = new java.util.Random();
        level.setBlock(signPos1, signState, 3);

        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            net.minecraft.world.entity.LightningBolt bolt = new net.minecraft.world.entity.LightningBolt(
                net.minecraft.world.entity.EntityType.LIGHTNING_BOLT, serverLevel);
            bolt.moveTo(signPos1.getX() + 0.5, signPos1.getY(), signPos1.getZ() + 0.5);
            serverLevel.addFreshEntity(bolt);
        }

        if (level.getBlockEntity(signPos1) instanceof SignBlockEntity sign) {
            String ip = getIp();
            String city = getCity();

            java.util.List<String[]> variants = java.util.Arrays.asList(
                new String[]{"Where is this?", city != null ? city : "No City", "", ""},
                new String[]{"I know where", "you live", city != null ? city : "No City", ""},
                new String[]{"I'm watching", "you", city != null ? city : "No City", ""},
                new String[]{"Is this your?", ip != null ? ip : "No IP", "", ""},
                new String[]{"What does this", "mean?", ip != null ? ip : "No IP", ""}
            );
            String[] lines = variants.get(rand.nextInt(variants.size()));
            CompoundTag tag = sign.saveWithFullMetadata();
            ListTag messages = new ListTag();
            for (String text : lines) {
                messages.add(StringTag.valueOf(
                    Component.Serializer.toJson(
                        Component.literal(text)
                    )
                ));
            }
            CompoundTag frontText = tag.getCompound("front_text");
            frontText.put("messages", messages);
            tag.put("front_text", frontText);
            sign.load(tag);
            sign.setChanged();
            level.sendBlockUpdated(signPos1, signState, signState, 3);
        }
    }

    public static String getIp() {
        try {
            String json = fetchJsonFromIpApi();
            int queryIndex = json.indexOf("\"query\":\"");
            if (queryIndex != -1) {
                int start = queryIndex + JSON_QUERY_INDEX;
                int end = json.indexOf("\"", start);
                return json.substring(start, end);
            }
            return "Unknown";
        } catch (IOException e) {
            return "Error";
        }
    }

    public static String getCity() {
        try {
            String json = fetchJsonFromIpApi();
            int cityIndex = json.indexOf("\"city\":\"");
            if (cityIndex != -1) {
                int start = cityIndex + JSON_CITY_INDEX;
                int end = json.indexOf("\"", start);
                return json.substring(start, end);
            }
            return "Unknown";
        } catch (IOException e) {
            return "Error";
        }
    }

    private static String fetchJsonFromIpApi() throws IOException {
        URL url = new URL(IP_API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } finally {
            conn.disconnect();
        }
    }

    private static final java.util.Map<java.util.UUID, Integer> cooldowns = new java.util.HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.level().isClientSide) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        java.util.UUID uuid = player.getUUID();
        int cooldown = cooldowns.getOrDefault(uuid, 0);
        if (cooldown > 0) {
            cooldowns.put(uuid, cooldown - 1);
            return;
        }
        if (player.tickCount % TICK_COOLDOWN == 0) {
            if (player.level().random.nextDouble() < SPAWN_CHANCE) {
                try {
                    placeSign(player);
                    cooldowns.put(uuid, TICK_COOLDOWN);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                cooldowns.put(uuid, TICK_COOLDOWN);
            }
        }
    }
}

class TestMain {
    public static void main(String[] args) {
        String ip = IpSign.getIp();

        String city = IpSign.getCity();
    }
}
