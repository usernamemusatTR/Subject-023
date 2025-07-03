package com.denek023.denek023.client;

import com.denek023.denek023.ModSounds;
import com.denek023.denek023.event.ChaseMusicEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.util.RandomSource;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Denek023ClientEvents {
    private static SimpleSoundInstance chaseInstance = null;

    @SubscribeEvent
    public static void onChaseMusicEvent(ChaseMusicEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        if (event.play) {
            // Eğer instance null ise veya ses yöneticisi bu sesi çalmıyorsa, yeni bir tane başlat.
            // isActive metodu, sesin o anda çalınıp çalınmadığını kontrol eder.
            if (chaseInstance == null || !mc.getSoundManager().isActive(chaseInstance)) {
                chaseInstance = new SimpleSoundInstance(
                    ModSounds.CHASE.get().getLocation(),
                    net.minecraft.sounds.SoundSource.AMBIENT,
                    4.0f, 1.0f,
                    RandomSource.create(),
                    true, 0,
                    Attenuation.NONE,
                    0, 0, 0,
                    true
                );
                mc.getSoundManager().play(chaseInstance);
            }
        } else {
            if (chaseInstance != null && mc.getSoundManager().isActive(chaseInstance)) {
                mc.getSoundManager().stop(chaseInstance);
                chaseInstance = null;
            }
        }
    }
}