package com.forgeessentials.perftools;

import java.util.TimerTask;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.server.ServerLifecycleHooks;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.logger.LoggingHandler;

/**
 * Warns those with permission when the memory usage passes a certain percentage threshold
 */
public class MemoryWatchdog extends TimerTask
{
    public MemoryWatchdog()
    {
    }

    @Override
    public void run()
    {
        long max = Runtime.getRuntime().maxMemory();
        long total = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        long percentage = total * 100L / max;

        if (percentage >= PerfToolsModule.percentageWarn)
        {

            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            try
            {
                if (FMLEnvironment.dist.isClient())
                {
                    LoggingHandler.felog.info("High memory use detected. " + percentage + "% of memory in use.");
                }
                else
                {
                    ChatOutputHandler.sendMessage(server.createCommandSourceStack(),
                            "[ForgeEssentials] High memory use detected. " + percentage + "% of memory in use.");
                }
                for (ServerPlayer player : ServerUtil.getPlayerList())
                    if (APIRegistry.perms.checkPermission(player, PerfToolsModule.PERM_WARN))
                        ChatOutputHandler.chatNotification(player.createCommandSourceStack(),
                                "[ForgeEssentials] High memory use detected. " + percentage + "% of memory in use.");
            }
            catch (Exception ignored)
            {
            }
        }
    }
}
