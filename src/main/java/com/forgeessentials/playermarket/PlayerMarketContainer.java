package com.forgeessentials.playermarket;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class PlayerMarketContainer extends ContainerChest
{
    private IInventory chestInventory;
    public PlayerMarketContainer(IInventory playerInventory, IInventory chestInventory,
            EntityPlayer player)
    {
        super(playerInventory, chestInventory, player);
    }

    @Override public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player)
    {
        if (clickTypeIn.equals(ClickType.QUICK_MOVE)) {
            return inventorySlots.get(slotId).getStack();
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);
    }

    @Override public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        return ItemStack.EMPTY;
    }

    @Override public boolean canMergeSlot(ItemStack p_canMergeSlot_1_, Slot p_canMergeSlot_2_)
    {
        return false;
    }

    @Override public boolean canDragIntoSlot(Slot p_canDragIntoSlot_1_)
    {
        return false;
    }
}
