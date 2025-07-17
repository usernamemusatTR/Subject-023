package com.denek023.denek023.entity;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class Denek023Entity extends Mob {
    public Denek023Entity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
    }
}