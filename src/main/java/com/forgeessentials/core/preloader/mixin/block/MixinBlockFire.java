package com.forgeessentials.core.preloader.mixin.block;

import net.minecraft.block.BlockFire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fe.event.world.FireEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Mixin(BlockFire.class)
public class MixinBlockFire
{

    @Inject(
        method = "tryCatchFire(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Ljava/util/Random;ILnet/minecraft/util/EnumFacing;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z"
        ),
        cancellable = true
    )
    public void handleTryCatchFire(World world, BlockPos pos, int chance, Random random, int age, EnumFacing face, CallbackInfo ci)
    {
        //System.out.println("Mixin : Fire destroyed block and spread to below block");
        if (MinecraftForge.EVENT_BUS.post(new FireEvent.Destroy(world, pos)))
        {
            ci.cancel();
        }
        else
        {
            int sourceX = x + face.offsetX;
            int sourceY = y + face.offsetY;
            int sourceZ = z + face.offsetZ;
            if (MinecraftForge.EVENT_BUS.post(new FireEvent.Spread(world, x, y, z, sourceX, sourceY, sourceZ)))
            {
                //System.out.println("Injector: Fire destroyed but could not spread to block below");
                world.setBlockToAir(x, y, z);
                ci.cancel();
            }
        }
    }

    @Inject(
    		method = "tryCatchFire(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Ljava/util/Random;ILnet/minecraft/util/EnumFacing;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;setBlockToAir(Lnet/minecraft/util/BlockPos;)Z"
        ),
        cancellable = true
    )
    public void handleTryCatchFireAir(World world, BlockPos pos, int chance, Random random, int age, EnumFacing face, CallbackInfo ci)
    {
        //System.out.println("Mixin : Fire destroyed block");
        if (MinecraftForge.EVENT_BUS.post(new FireEvent.Destroy(world, pos)))
        {
            ci.cancel();
        }
    }

    @Inject(
        method = "Lnet/minecraft/block/BlockFire;updateTick(Lnet/minecraft/world/World;net/minecraft/block/state/IBlockState;Lnet/minecraft/util/BlockPos;Ljava/util/Random;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z"
        ),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand, CallbackInfo ci, boolean isFireSource, int blockMeta,
                           boolean isHighHumidity, byte b0, int x, int z, int y)
    {
        //System.out.println(String.format("Mixin : Fire spreading to other block from [%d,%d,%d] to [%d,%d,%d]", sourceX, sourceY, sourceZ, x, y, z));
        //if (MinecraftForge.EVENT_BUS.post(new FireEvent.Spread(worldIn, pos, sourceX, sourceY, sourceZ)))
        //{
        //    ci.cancel();
        //}
    }

}
