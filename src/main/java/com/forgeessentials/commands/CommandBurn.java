package com.forgeessentials.commands;

import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.CommandUsageException;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandBurn extends FEcmdModuleCommands {
	
    @Override
    public String getCommandName()
    {
        return "burn";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
    	OptionParser parser = new OptionParser();
    	parser.accepts("p").withRequiredArg();
    	parser.accepts("t").withRequiredArg().ofType(Integer.class);
    	parser.accepts("?");
    	
    	OptionSet options = parser.parse(args);

    	EntityPlayerMP target = sender;
    	int time = 15;
    	
    	if (options.has("?")) {
    		OutputHandler.chatNotification(sender, "Options: " + options.toString());
    		return;
    	}
    	
    	if (options.has("p") && !options.valueOf("p").toString().equalsIgnoreCase("me")) {
    		if (PermissionsManager.checkPermission(sender, getPermissionNode() + ".others")) {
    			target = UserIdent.getPlayerByUsername(options.valueOf("p").toString());
    			if (target == null) {
    				throw new CommandException(String.format("Player %s does not exist, or is not online.", options.valueOf("p").toString()));
    			}
    		}
    		else {
    			throw new CommandException("You lack the permission to burn other players.");
    		}
    	}
    	
    	if (options.has("t"))
        {
            time = parseIntWithMin(sender, options.valueOf("t").toString(), 0);
        }
    	
    	OutputHandler.chatConfirmation(sender, "You should feel bad about doing that.");
    	target.setFire(time);
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
    	OptionParser parser = new OptionParser();
    	parser.accepts("p").withRequiredArg();
    	parser.accepts("t").withRequiredArg().ofType(Integer.class);
    	parser.accepts("?");
    	
    	OptionSet options = parser.parse(args);

    	EntityPlayerMP target;
    	int time = 15;
    	
    	if (options.has("?")) {
    		OutputHandler.chatNotification(sender, "Options: " + options.toString());
    		return;
    	}
    	
    	if (options.has("p")) {
    		target = UserIdent.getPlayerByUsername(options.valueOf("p").toString());
    		if (target == null) {
    			throw new CommandException(String.format("Player %s does not exist, or is not online.", options.valueOf("p").toString()));
    		}
    	}
    	else {
    		throw new WrongUsageException(getCommandUsage(sender));
    	}
    	
    	if (options.has("t"))
        {
            time = parseIntWithMin(sender, options.valueOf("t").toString(), 0);
        }
    	
    	OutputHandler.chatConfirmation(sender, "You should feel bad about doing that.");
    	target.setFire(time);
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(getPermissionNode() + ".others", RegisteredPermValue.OP);
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
        }
        else
        {
            return null;
        }
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
    	if (sender instanceof EntityPlayer)
        {
    		return "/burn [-p <me|player>] [-t <time>] Set a player on fire.";
        }
        else
        {
        	return "/burn <-p player> [-t <time>] Set a player on fire.";
        }
    }
}
