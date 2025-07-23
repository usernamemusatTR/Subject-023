package com.denek023.denek023.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;

public class DoorHelper {
    public static boolean tryOpenDoor(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof DoorBlock) {
            if (state.hasProperty(DoorBlock.HALF) && state.getValue(DoorBlock.HALF) == net.minecraft.world.level.block.state.properties.DoubleBlockHalf.LOWER) {
                boolean open = state.getValue(DoorBlock.OPEN);
                if (!open) {
                    level.setBlock(pos, state.setValue(DoorBlock.OPEN, true), 10);
                    return true;
                }
            }
        }
        return false;
    }
}
