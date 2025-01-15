package com.forgeessentials.jscripting.wrapper.mc.event.entity.player;

import net.minecraftforge.event.entity.player.UseHoeEvent;



public class JsUseHoeEvent extends JsPlayerEvent<UseHoeEvent>
{

    @SubscribeEvent
    public final void _handle(UseHoeEvent event)
    {
        _callEvent(event);
    }

}
