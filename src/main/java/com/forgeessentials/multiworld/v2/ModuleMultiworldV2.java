package com.forgeessentials.multiworld.v2;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.config.ConfigLoaderBase;
import com.forgeessentials.core.misc.commandTools.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.multiworld.v2.command.CommandMultiworld;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

/**
 * 
 * @author MaximustheMiner
 */
@FEModule(name = "MultiworldV2", parentMod = ForgeEssentials.class, canDisable = true)
public class ModuleMultiworldV2 extends ConfigLoaderBase
{
    private static ForgeConfigSpec MULTIWORLD_CONFIG;
    private static final ConfigData data = new ConfigData("MultiworldV2", MULTIWORLD_CONFIG, new ForgeConfigSpec.Builder());

	public static final String PERM_BASE = "fe.multiworld";
    public static final String PERM_MANAGE = PERM_BASE + ".manage";
    public static final String PERM_DELETE = PERM_BASE + ".delete";
    public static final String PERM_LIST = PERM_BASE + ".list";
    public static final String PERM_TELEPORT = PERM_BASE + ".teleport";

    public boolean testValue;

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event)
    {
        FECommandManager.registerCommand(new CommandMultiworld(true), event.getDispatcher());
    }

	@SubscribeEvent
	public void serverStarting(FEModuleServerStartingEvent e) {

		APIRegistry.perms.registerNode(PERM_MANAGE, DefaultPermissionLevel.OP,
				"Manage multiworlds");
		APIRegistry.perms.registerNode(PERM_DELETE, DefaultPermissionLevel.OP,
				"Delete multiworlds");
		APIRegistry.perms.registerNode(PERM_LIST, DefaultPermissionLevel.ALL,
				"List multiworlds on the server");
	}

	static ForgeConfigSpec.BooleanValue FEtestValue;
	
	@Override
	public void load(Builder BUILDER, boolean isReload) {
		BUILDER.comment("AuthModule configuration").push("General");
		FEtestValue = BUILDER.comment("test value").define("test", false);
		BUILDER.pop();
		
	}

	@Override
	public void bakeConfig(boolean reload) {
		testValue = FEtestValue.get();
	}

	@Override
	public ConfigData returnData() {
		return data;
	}
}
