package com.denek023.event;

import net.minecraft.world.phys.Vec3;

public class ReverseTimeState {
    public final Vec3 playerPos;
    public final float playerHealth;

    public ReverseTimeState(Vec3 pos, float health) {
        this.playerPos = pos;
        this.playerHealth = health;
    }
}
