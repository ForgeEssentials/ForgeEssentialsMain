package net.minecraftforge.fe.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;


@Cancelable
public class EntityPortalEvent extends EntityEvent
{

    public final World world;

    public final BlockPos pos;

    public final int targetDimension;

    public final int targetX;

    public final int targetY;

    public final int targetZ;

    public EntityPortalEvent(Entity entity, World world, BlockPos pos, int targetDimension, int targetX, int targetY, int targetZ)
    {
        super(entity);
        this.world = world;
        this.pos = pos;
        this.targetDimension = targetDimension;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
    }
}
