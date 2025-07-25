package com.denek023.denek023;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "denek023");

    public static final RegistryObject<SoundEvent> RUN =
        SOUND_EVENTS.register("run", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("denek023", "run")));
    public static final RegistryObject<SoundEvent> CHASE =
        SOUND_EVENTS.register("chase", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("denek023", "chase")));
    public static final RegistryObject<SoundEvent> JUMPSCARE =
        SOUND_EVENTS.register("jumpscare", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("denek023", "jumpscare")));
    public static final RegistryObject<SoundEvent> DISCARDED =
        SOUND_EVENTS.register("discarded", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("denek023", "discarded")));
    public static final RegistryObject<SoundEvent> SCREAM =
        SOUND_EVENTS.register("scream", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("denek023", "scream")));
    public static final RegistryObject<SoundEvent> CAVE_AMBIENT =
        SOUND_EVENTS.register("cave_ambient", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("denek023", "cave_ambient")));
    public static final RegistryObject<SoundEvent> BREATHLEFT =
        SOUND_EVENTS.register("breathleft", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("denek023", "breathleft")));
    public static final RegistryObject<SoundEvent> BREATHRIGHT =
        SOUND_EVENTS.register("breathright", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("denek023", "breathright")));
    public static final RegistryObject<SoundEvent> FAKEWALK =
            SOUND_EVENTS.register("fakewalk", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("denek023", "fakewalk")));
    public static final RegistryObject<SoundEvent> CREEPERTSS =
            SOUND_EVENTS.register("creepertss", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("denek023", "creepertss")));
    public static final RegistryObject<SoundEvent> HEARTBEAT =
            SOUND_EVENTS.register("heartbeat", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("denek023", "heartbeat")));
    public static final RegistryObject<SoundEvent> REVERSEMUSICBOX =
            SOUND_EVENTS.register("reversemusicbox", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("denek023", "reversemusicbox")));
    public static final RegistryObject<SoundEvent> CREEPY_WHISPER =
            SOUND_EVENTS.register("creepy_whsiper", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("denek023", "creepy_whisper")));
    public static final RegistryObject<SoundEvent> WHISTLE =
            SOUND_EVENTS.register("whistle", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("denek023", "whistle")));
    public static final RegistryObject<SoundEvent> SCREAM2 =
            SOUND_EVENTS.register("scream2", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("denek023", "scream2")));
    public static final RegistryObject<SoundEvent> JUMPSCARE2 =
            SOUND_EVENTS.register("jumpscare2", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("denek023", "jumpscare2")));
    public static final RegistryObject<SoundEvent> COMECLOSER =
            SOUND_EVENTS.register("comecloser", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("denek023", "comecloser")));
            
    public static void register(IEventBus modEventBus) {
        SOUND_EVENTS.register(modEventBus);
    }
}