package com.forgeessentials.util;

import net.minecraft.command.CommandResultStats.Type;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;

public class DoAsCommandSender implements ICommandSender
{

    protected ICommandSender sender;

    protected UserIdent ident;

    protected boolean hideChatMessages;

    public DoAsCommandSender()
    {
        this.ident = APIRegistry.IDENT_SERVER;
        this.sender = MinecraftServer.getServer();
    }

    public DoAsCommandSender(UserIdent ident)
    {
        this.ident = ident;
        this.sender = MinecraftServer.getServer();
    }

    public DoAsCommandSender(UserIdent ident, ICommandSender sender)
    {
        this.ident = ident;
        this.sender = sender;
    }

    public ICommandSender getOriginalSender()
    {
        return sender;
    }

    public UserIdent getUserIdent()
    {
        return ident;
    }

    @Override
    public String getName()
    {
        return sender.getName();
    }

    @Override
    public IChatComponent func_145748_c_()
    {
        return sender.getName();
    }

    @Override
    public void addChatMessage(IChatComponent message)
    {
        if (!hideChatMessages)
            sender.addChatMessage(message);
    }

    @Override
    public boolean canCommandSenderUseCommand(int level, String command)
    {
        return true;
    }

    @Override
    public ChunkCoordinates getPlayerCoordinates()
    {
        return sender.getPlayerCoordinates();
    }

    @Override
    public World getEntityWorld()
    {
        return sender.getEntityWorld();
    }

    public UserIdent getIdent()
    {
        return ident;
    }

    public void setIdent(UserIdent ident)
    {
        this.ident = ident;
    }

    public void setHideChatMessages(boolean hideChatMessages)
    {
        this.hideChatMessages = hideChatMessages;
    }

    public boolean isHideChatMessages()
    {
        return hideChatMessages;
    }

	@Override
	public IChatComponent getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BlockPos getPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vec3 getPositionVector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Entity getCommandSenderEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean sendCommandFeedback() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCommandStat(Type type, int amount) {
		// TODO Auto-generated method stub
		
	}

}