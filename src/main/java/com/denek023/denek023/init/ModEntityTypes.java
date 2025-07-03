package com.denek023.denek023.init;

import com.denek023.denek023.entity.Denek023AttackerEntity;
import com.denek023.denek023.entity.Denek023BehindYouEntity;
import com.denek023.denek023.entity.Denek023WatcherEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, "denek023");

    public static final RegistryObject<EntityType<Denek023AttackerEntity>> DENEK023_ATTACKER =
        ENTITY_TYPES.register("denek023_attacker",
            () -> EntityType.Builder.of(Denek023AttackerEntity::new, MobCategory.MONSTER)
                .sized(0.6F, 1.95F)
                .build("denek023:denek023_attacker"));

    public static final RegistryObject<EntityType<Denek023WatcherEntity>> DENEK023_WATCHER =
        ENTITY_TYPES.register("denek023_watcher",
            () -> EntityType.Builder.of(Denek023WatcherEntity::new, MobCategory.MONSTER)
                .sized(0.6F, 1.95F)
                .build("denek023:denek023_watcher"));

    public static final RegistryObject<EntityType<Denek023BehindYouEntity>> DENEK023_BEHIND_YOU =
        ENTITY_TYPES.register("denek023_behind_you",
            () -> EntityType.Builder.of(Denek023BehindYouEntity::new, MobCategory.MONSTER)
                .sized(0.6F, 1.95F)
                .build("denek023:denek023_behind_you"));
}