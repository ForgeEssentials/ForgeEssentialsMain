package com.ForgeEssentials.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

import com.ForgeEssentials.api.FEChatFormatCodes;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.google.common.collect.HashMultimap;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class CommandChunkLoaderList extends FEcmdModuleCommands
{
	@Override
	public String getCommandName()
	{
		return "chunkloaderlist";
	}
	
	@Override
	public String[] getDefaultAliases()
    {
        return new String[] {"cll"};
    }

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
	    String key = "*";
	    if (args.length != 0)
	    {
	        String target = "";
	        for (String s : args)
	            target = target + " " + s;
	        target = target.substring(1).trim();
	        
	        List<String> allUsernames = Arrays.asList(MinecraftServer.getServer().getConfigurationManager().getAvailablePlayerDat());
	        for (String username : allUsernames)
	        {
	            if(username.equalsIgnoreCase(target))
	            {
	                key = "p:" + username;
	                break;
	            }
	        }
	        
	        for (ModContainer mod : Loader.instance().getActiveModList())
	        {
	            if (mod.getName().equalsIgnoreCase(target)) key = "m:" + mod.getModId();
	            else if (mod.getModId().equalsIgnoreCase(target)) key = "m:" + mod.getModId();
	        }
	    }
	    list(sender, key);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
	    list(sender, "*");
	}

	private void list(ICommandSender sender, String key)
    {
	    for (int i : DimensionManager.getIDs())
	        list(sender, i, key);
    }
	
	private void list(ICommandSender sender, int dim, String key)
	{
	    WorldServer world = DimensionManager.getWorld(dim);
	    
	    HashMultimap<String, Ticket> modTickets = HashMultimap.create();
	    HashMultimap<String, Ticket> playerTickets = HashMultimap.create();
	    
	    for (Ticket ticket : ForgeChunkManager.getPersistentChunksFor(world).values())
	        if (ticket.isPlayerTicket()) playerTickets.put(ticket.getPlayerName(), ticket);
	        else modTickets.put(ticket.getModId(), ticket);
	    
	    if (modTickets.isEmpty() && playerTickets.isEmpty()) return;
        
	    if (!key.equals("*")) sender.sendChatToPlayer(FEChatFormatCodes.UNDERLINE + "ChunkLoaders for " + key.split(":", 2)[1] + ":");
	    
        sender.sendChatToPlayer(FEChatFormatCodes.AQUA + "Dim " + world.provider.getDimensionName() + ":");
	    
	    if (key.startsWith("p:") || key.equals("*"))
	    {
	        for (String username : playerTickets.keySet())
	        {
	            if (key.replace("p:", "").equalsIgnoreCase(username) || key.equals("*"))
	            {
	                if(key.equals("*")) sender.sendChatToPlayer(FEChatFormatCodes.AQUA + username);
	                
	                HashSet<ChunkCoordIntPair> chunks = new HashSet<ChunkCoordIntPair>();
	                
	                for (Ticket ticket : playerTickets.get(username))
	                    for (Object obj : ticket.getChunkList())
	                        chunks.add((ChunkCoordIntPair) obj);
	                
	                for(ChunkCoordIntPair coords : chunks)
	                    sender.sendChatToPlayer(coords.getCenterXPos() + " : " + coords.getCenterZPosition());
	            }
	        }    
	    }
	    
	    if (key.startsWith("m:") || key.equals("*"))
	    {
	        for (String modID : modTickets.keySet())
	        {
	            if(key.equals("*")) sender.sendChatToPlayer(FEChatFormatCodes.AQUA + modID);
	            HashSet<ChunkCoordIntPair> chunks = new HashSet<ChunkCoordIntPair>();
                
                for (Ticket ticket : playerTickets.get(modID))
                    for (Object obj : ticket.getChunkList())
                        chunks.add((ChunkCoordIntPair) obj);
                    
                for(ChunkCoordIntPair coords : chunks)
                    sender.sendChatToPlayer(coords.getCenterXPos() + " : " + coords.getCenterZPosition());
	        }
	    }
    }

    @Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
	    if (args.length == 1)
	    {
	        ArrayList<String> options = new ArrayList<String>();
	        for(String s : MinecraftServer.getServer().getConfigurationManager().getAvailablePlayerDat())
	            options.add(s);
	        for (ModContainer mod : Loader.instance().getActiveModList())
            {
	            options.add(mod.getName());
            }
	        
	        return getListOfStringsFromIterableMatchingLastWord(args, options);
	    }
		return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.OWNERS;
	}
}
