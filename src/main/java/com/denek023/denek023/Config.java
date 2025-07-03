package com.denek023.denek023;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Denek023.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue logDirtBlock;
    public static final ForgeConfigSpec.IntValue magicNumber;
    public static final ForgeConfigSpec.ConfigValue<String> magicNumberIntroduction;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        logDirtBlock = builder.comment("Whether to log the dirt block on common setup")
                .define("logDirtBlock", true);
        magicNumber = builder.comment("A magic number")
                .defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);
        magicNumberIntroduction = builder.comment("What you want the introduction message to be for the magic number")
                .define("magicNumberIntroduction", "The magic number is... ");
        SPEC = builder.build();
    }
}
