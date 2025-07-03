package com.denek023.event;

import com.denek023.denek023.Denek023;
import com.denek023.denek023.entity.Denek023BehindYouEntity;
import com.denek023.denek023.init.ModEntityTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = Denek023.MODID)
public class Denek023BehindYouHandler {

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        if (event.getEntity().level().isClientSide) return;

        ServerPlayer player = (ServerPlayer) event.getEntity();
        AbstractContainerMenu menu = event.getContainer();

        if (menu instanceof InventoryMenu || menu instanceof FurnaceMenu || menu instanceof CraftingMenu || menu instanceof ChestMenu) {
            if (new Random().nextInt(10) == 0) {
                double checkRadius = 32.0;
                List<Denek023BehindYouEntity> behinds = player.level().getEntitiesOfClass(
                    Denek023BehindYouEntity.class,
                    player.getBoundingBox().inflate(checkRadius)
                );
                if (!behinds.isEmpty()) return;

                double distance = 3.0;
                double angle = Math.toRadians(player.getYRot() + 180);
                double dx = -Math.sin(angle) * distance;
                double dz = Math.cos(angle) * distance;
                double spawnX = player.getX() + dx;
                double spawnZ = player.getZ() + dz;
                double spawnY = player.getY();

                Denek023BehindYouEntity behind = ModEntityTypes.DENEK023_BEHIND_YOU.get().create(player.level());
                if (behind != null) {
                    behind.setPos(spawnX, spawnY, spawnZ);
                    player.level().addFreshEntity(behind);

                    System.out.println("Denek023BehindYouEntity spawned at: " + spawnX + ", " + spawnY + ", " + spawnZ);

                    Random rand = new Random();
                    int soundChance = rand.nextInt(4);

                    if (soundChance == 0) {
                        player.level().playSound(null, player.blockPosition(),
                            SoundEvent.createVariableRangeEvent(Denek023.id("sshhh")),
                            SoundSource.HOSTILE, 4.0f, 1.0f);
                    } else if (soundChance == 1) {
                        player.level().playSound(null, player.blockPosition(),
                            SoundEvent.createVariableRangeEvent(Denek023.id("behindyou")),
                            SoundSource.HOSTILE, 4.0f, 1.0f);
                    }
                }
            }
        }
    }
}
