package com.forgeessentials.playermarket;

import static com.forgeessentials.playermarket.ModulePlayerMarket.PERM_CMD;
import static com.forgeessentials.playermarket.ModulePlayerMarket.PERM_CMD_BUY_BASE;
import static com.forgeessentials.playermarket.ModulePlayerMarket.PERM_CMD_REMOVE;
import static com.forgeessentials.playermarket.ModulePlayerMarket.PERM_CMD_SELL_BASE;
import static com.forgeessentials.playermarket.ModulePlayerMarket.PERM_CMD_SERVER;
import static com.forgeessentials.util.ServerUtil.getItemPermission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import org.jetbrains.annotations.NotNull;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.chat.Mailer;
import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.core.BasicInteraction;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.playermarket.PlayerMarketData.AuctionStack;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.output.ChatOutputHandler;

public class PlayerMarketCommand extends ParserCommandBase
{
    @Override public String getUsage(ICommandSender sender)
    {
        return "market [sell]? [price]?";
    }

    @Override public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override public String getPermissionNode()
    {
        return PERM_CMD;
    }

    @Override public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @NotNull @Override protected String getPrimaryAlias()
    {
        return "market";
    }

    @Override public List<String> getAliases()
    {
        return Arrays.asList("pshop","playershop", "auctionhouse", "ah");
    }

    public void initItems(IInventory source, int offset, int amount,  List<AuctionStack> _itemsListed, CommandParserArgs args) {
        int size = offset + amount;
        int invSize = source.getSizeInventory();
        for (int i = offset; i < size; i++) {
            if (i >= _itemsListed.size()) {
                if (i-offset < invSize)
                {
                    source.setInventorySlotContents(i - offset, ItemStack.EMPTY);
                }
                continue;
            }

            AuctionStack auctionStack = _itemsListed.get(i);
            ItemStack stack = auctionStack.stack.copy();

            NBTTagCompound tag = stack.getTagCompound();
            if (tag == null) {
                tag = new NBTTagCompound();
            }

            NBTTagCompound display = tag.getCompoundTag("display");
            NBTTagList lore = display.getTagList("Lore", 8);

            String prefix = ChatOutputHandler.COLOR_FORMAT_CHARACTER + "6";
            lore.appendTag(new NBTTagString(prefix + "Click to " + (args.senderPlayer.getUniqueID().equals(auctionStack.sellerId) ? "remove" : "buy")));
            lore.appendTag(new NBTTagString(prefix + "Price: " + APIRegistry.economy.toString(auctionStack.price)));
            lore.appendTag(new NBTTagString(prefix + "Seller: " + auctionStack.sellerName));
            display.setTag("Lore", lore);
            tag.setTag("display", display);
            stack.setTagCompound(tag);

            source.setInventorySlotContents(i-offset, stack);
        }
        source.markDirty();
    }
    protected void ShowPlayerMarket(CommandParserArgs args, boolean remove) {
        if (args.isTabCompletion) {
            return;
        }
        EntityPlayerMP player = args.senderPlayer;
        //Take a local copy of itemsListed for basic concurrency.
        List<AuctionStack> _itemsListed = new ArrayList<>(ModulePlayerMarket.instance().data.itemsListed);
        final boolean multiPage = _itemsListed.size() > 54;
        final int[] currentPage = new int[1];
        InventoryBasic source = new InventoryBasic("Chest", false, multiPage ? 54 : _itemsListed.size());

        if (multiPage) {
            initItems(source, 0, 45, _itemsListed, args);
            //Init Menu
            source.setInventorySlotContents(48, new ItemStack(Items.ARROW));
            source.setInventorySlotContents(50, new ItemStack(Items.TIPPED_ARROW));
        } else {
            initItems(source, 0, 54, _itemsListed, args);
        }
        BasicInteraction menuChest = new BasicInteraction("Player Market", true, source)
        {

            @Override public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
            {
                return new PlayerMarketContainer(playerInventory, this, playerIn) {

                    @Override public synchronized ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player)
                    {
                        if (inventorySlots.get(slotId).inventory.equals(getLowerChestInventory()))
                        {
                            if (multiPage && slotId >= 45) {
                                if (slotId == 48) {
                                    if (currentPage[0] > 0) {
                                        currentPage[0]--;
                                    }
                                    initItems(getLowerChestInventory(), currentPage[0] * 45, 45, _itemsListed, args);
                                } else if (slotId == 50) {
                                    if (currentPage[0] < (_itemsListed.size()-1) / 45) {
                                        currentPage[0]++;
                                    }
                                    initItems(getLowerChestInventory(), currentPage[0] * 45, 45, _itemsListed, args);
                                }
                                return super.slotClick(slotId, dragType, clickTypeIn, player);
                            }
                            AuctionStack stack = _itemsListed.get(slotId);

                            //Add Item to inventory here
                            if (!ModulePlayerMarket.instance().data.itemsListed.contains(stack))
                            {
                                args.error("%s already sold!", stack.stack.getDisplayName());
                                _itemsListed.remove(stack);
                                initItems(getLowerChestInventory(), currentPage[0] * 45, 45, _itemsListed, args);
                                return super.slotClick(slotId, dragType, clickTypeIn, player);
                            }


                            if (!args.hasPermission(PERM_CMD_BUY_BASE + "." + getItemPermission(stack.stack))) {
                                args.error("You don't have permission to buy %s", stack.stack.getDisplayName());
                                return super.slotClick(slotId, dragType, clickTypeIn, player);
                            }

                            if (!remove && !stack.sellerId.equals(player.getUniqueID()))
                            {
                                Wallet purchaseWallet = APIRegistry.economy.getWallet(UserIdent.get(player));
                                Wallet sellerWallet = APIRegistry.economy.getWallet(UserIdent.get(stack.sellerId));
                                if (!purchaseWallet.covers(stack.price))
                                {
                                    TextComponentString s1 = new TextComponentString(Translator.format("Not enough %s to buy ", APIRegistry.economy.currency(2)));
                                    s1.getStyle().setColor(ChatOutputHandler.chatErrorColor);
                                    s1.appendSibling(stack.stack.getTextComponent());
                                    player.sendMessage(s1);
                                    return super.slotClick(slotId, dragType, clickTypeIn, player);
                                }

                                purchaseWallet.withdraw(stack.price);
                                sellerWallet.add(stack.price);

                                TextComponentString s1 = new TextComponentString("");
                                s1.getStyle().setColor(ChatOutputHandler.chatConfirmationColor);
                                s1.appendSibling(stack.stack.getTextComponent());
                                s1.appendText(Translator.format(" purchased for %s", APIRegistry.economy.toString(stack.price)));
                                player.sendMessage(s1);
                            }
                            else
                            {
                                TextComponentString s1 = new TextComponentString("");
                                s1.getStyle().setColor(ChatOutputHandler.chatConfirmationColor);
                                s1.appendSibling(stack.stack.getTextComponent());
                                s1.appendText(Translator.translate(" removed from market"));
                                player.sendMessage(s1);
                            }
                            ModulePlayerMarket.instance().data.itemsListed.remove(stack);
                            if (!remove)
                            {
                                player.inventory.addItemStackToInventory(stack.stack);
                            } else {
                                UserIdent removedUser = UserIdent.get(stack.sellerId);

                                String[] msg = Translator.format("Your Item | was removed by an Admin!").split("\\|");

                                TextComponentString s1 = new TextComponentString(msg[0]);
                                s1.getStyle().setColor(TextFormatting.GREEN);
                                s1.appendSibling(stack.stack.getTextComponent());
                                s1.appendText(msg[1]);
                                if (removedUser.hasPlayer()) {
                                    removedUser.getPlayer().sendMessage(s1);
                                } else if (ModuleChat.instance != null){
                                    Mailer.sendMail(APIRegistry.IDENT_SERVER, removedUser, s1.getFormattedText());
                                }
                            }
                            _itemsListed.remove(stack);
                            initItems(getLowerChestInventory(), currentPage[0] * 45, 45, _itemsListed, args);
                        }
                        return super.slotClick(slotId, dragType, clickTypeIn, player);
                    }
                };
            }

            @Override public String getGuiID()
            {
                return "minecraft:chest";
            }
        };

        player.displayGUIChest(menuChest);
    }
    @Override public void parse(CommandParserArgs args) throws CommandException
    {
        if (args.senderPlayer == null) {
            args.error("Must be a player to use command!");
            return;
        }
        if (args.isEmpty())
        {
            ShowPlayerMarket(args, false);
            return;
        }

        args.tabComplete("buy", "remove", "server", "sell");
        String arg = args.remove();
        AuctionStack newStack = new AuctionStack();
        switch (arg) {
        case "buy":
            ShowPlayerMarket(args, false);
            break;
        case "remove":
            if (!args.hasPermission(PERM_CMD_REMOVE))
            {
                args.error("Not allowed to use subcommand!");
                break;
            }
            ShowPlayerMarket(args, true);
            break;
        case "server":
            if (!args.hasPermission(PERM_CMD_SERVER))
            {
                args.error("Not allowed to use subcommand!");
                break;
            }
            newStack.sellerId = APIRegistry.IDENT_SERVER.getUuid();
            newStack.sellerName = APIRegistry.IDENT_SERVER.getUsername();
        case "sell":
            newStack.stack = args.senderPlayer.inventory.getCurrentItem();
            Long price = ModuleEconomy.getItemPrice(newStack.stack, UserIdent.get(args.senderPlayer));
            if (newStack.stack == ItemStack.EMPTY)
            {
                args.error("Can't sell your hand!");
                break;
            }

            if (!args.hasPermission(PERM_CMD_SELL_BASE + "." + getItemPermission(newStack.stack))) {
                args.error("You don't have permission to sell %s", newStack.stack.getDisplayName());
                break;
            }
            if (!args.isEmpty())
            {
                newStack.price = args.parseInt();
                if (newStack.price < 0) {
                    args.error("Price can not be negative!");
                    break;
                }
                if (price != null && newStack.price < price) {
                    args.warn("Price for %s is lower than server price of %s.  If this is an error, you will need to remove your item!", newStack.stack.getDisplayName(), price);
                }
            } else {
                if (price != null) {
                    newStack.price = price;
                } else {
                    args.error("No default price set for %s!", newStack.stack.getDisplayName());
                    break;
                }
            }
            if (arg.equals("sell"))
            {
                newStack.sellerId = args.senderPlayer.getUniqueID();
                newStack.sellerName = args.senderPlayer.getName();
                args.senderPlayer.inventory.removeStackFromSlot(args.senderPlayer.inventory.currentItem);

            }

            newStack.stack = newStack.stack.copy();
            ModulePlayerMarket.instance().data.itemsListed.add(newStack);
            TextComponentString s1 = new TextComponentString("");
            s1.getStyle().setColor(ChatOutputHandler.chatConfirmationColor);
            s1.appendSibling(newStack.stack.getTextComponent());
            s1.appendText(Translator.format(" sold for %s", APIRegistry.economy.toString(newStack.price)));
            args.senderPlayer.sendMessage(s1);
            break;
        }
    }
}
