package com.denek023.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = "denek023")
public class MobStareEvent {
    private static final int STARE_RADIUS = 50;
    private static final int STARE_DURATION_TICKS = 20 * 15;
    private static final double TRIGGER_CHANCE = 0.005;
    private static final Map<UUID, Integer> staringTicks = new HashMap<>();
    private static final Map<Mob, LookAtPlayerGoal> activeGoals = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!(event.player instanceof ServerPlayer player) || event.phase != TickEvent.Phase.END) return;
        UUID uuid = player.getUUID();
        ServerLevel level = (ServerLevel) player.level();

        if (staringTicks.containsKey(uuid)) {
            int ticks = staringTicks.get(uuid) - 1;
            for (Mob mob : activeGoals.keySet()) {
                if (mob.level() == level && mob.isAlive() && mob.distanceTo(player) <= STARE_RADIUS) {
                    if (mob instanceof PathfinderMob pathMob) {
                        pathMob.getNavigation().stop();
                    }
                    mob.setDeltaMovement(0, 0, 0);
                    mob.setYRot(mob.getYRot());
                }
            }
            if (ticks <= 0) {
                staringTicks.remove(uuid);
                stopAllMobsStaring(player, level);
            } else {
                staringTicks.put(uuid, ticks);
            }
            return;
        }

        if (player.tickCount % 20 == 0 && level.random.nextDouble() < TRIGGER_CHANCE) {
            staringTicks.put(uuid, STARE_DURATION_TICKS);
            makeAllMobsStare(player, level);
        }
    }

    private static void makeAllMobsStare(Player player, ServerLevel level) {
        AABB box = player.getBoundingBox().inflate(STARE_RADIUS);
        List<PathfinderMob> mobs = level.getEntitiesOfClass(PathfinderMob.class, box, mob -> mob.isAlive());
        for (PathfinderMob mob : mobs) {
            stopMobStaring(mob);
            LookAtPlayerGoal stareGoal = new LookAtPlayerGoal(mob, Player.class, 64.0F, 1.0F, false) {
                @Override
                public boolean canContinueToUse() {
                    return true;
                }
            };
            mob.goalSelector.addGoal(0, stareGoal);
            activeGoals.put(mob, stareGoal);
        }
    }

    private static void stopAllMobsStaring(Player player, ServerLevel level) {
        Iterator<Map.Entry<Mob, LookAtPlayerGoal>> it = activeGoals.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Mob, LookAtPlayerGoal> entry = it.next();
            Mob mob = entry.getKey();
            LookAtPlayerGoal goal = entry.getValue();
            mob.goalSelector.removeGoal(goal);
            if (mob instanceof PathfinderMob pathMob) {
                pathMob.setNoAi(false);
            }
            it.remove();
        }
    }

    private static void stopMobStaring(Mob mob) {
        LookAtPlayerGoal oldGoal = activeGoals.remove(mob);
        if (oldGoal != null) {
            mob.goalSelector.removeGoal(oldGoal);
        }
    }

    // Helper for MobStareEventSoundSilence
    public static boolean isMobStaring(Mob mob) {
        return activeGoals.containsKey(mob);
    }
}
