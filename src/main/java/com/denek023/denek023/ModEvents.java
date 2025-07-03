package com.denek023.denek023;

import com.denek023.denek023.entity.Denek023AttackerEntity;
import com.denek023.denek023.entity.Denek023WatcherEntity;
import com.denek023.denek023.entity.Denek023BehindYouEntity;
import com.denek023.denek023.init.ModEntityTypes;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Denek023.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void onEntityAttributeCreate(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.DENEK023_ATTACKER.get(), Denek023AttackerEntity.createAttributes().build());
        event.put(ModEntityTypes.DENEK023_WATCHER.get(), Denek023WatcherEntity.createAttributes().build());
        event.put(ModEntityTypes.DENEK023_BEHIND_YOU.get(), Denek023BehindYouEntity.createAttributes().build());
    }
}