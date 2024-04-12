package com.forgeessentials.util.events;

import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerLifecycleEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.Event;

public class FEModuleEvent extends Event
{

    protected ServerLifecycleEvent event;

    public ServerLifecycleEvent getServerLifecycleEvent()
    {
        return event;
    }

    public static class FEModuleServerAboutToStartEvent extends FEModuleEvent
    {
        public FEModuleServerAboutToStartEvent(ServerAboutToStartEvent event)
        {
            this.event = event;
        }
    }

    public static class FEModuleServerStartingEvent extends FEModuleEvent
    {
        public FEModuleServerStartingEvent(ServerStartingEvent event)
        {
            this.event = event;
        }
    }

    public static class FEModuleServerStartedEvent extends FEModuleEvent
    {
        public FEModuleServerStartedEvent(ServerStartedEvent event)
        {
            this.event = event;
        }
    }

    public static class FEModuleServerStoppingEvent extends FEModuleEvent
    {
        public FEModuleServerStoppingEvent(ServerStoppingEvent event)
        {
            this.event = event;
        }
    }

    public static class FEModuleServerStoppedEvent extends FEModuleEvent
    {
        public FEModuleServerStoppedEvent(ServerStoppedEvent event)
        {
            this.event = event;
        }
    }

}
