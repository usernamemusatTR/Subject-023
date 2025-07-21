package com.denek023.event;

import net.minecraftforge.eventbus.api.Event;

public class ChaseMusicEvent extends Event {
    public final boolean play;

    public ChaseMusicEvent(boolean play) {
        this.play = play;
    }
}