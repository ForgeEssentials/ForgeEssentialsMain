package com.forgeessentials.jscripting.wrapper.mc.event.entity.player;

import net.minecraftforge.event.entity.player.ArrowLooseEvent;



public class JsArrowLooseEvent extends JsPlayerEvent<ArrowLooseEvent>
{

    @SubscribeEvent
    public final void _handle(ArrowLooseEvent event)
    {
        _callEvent(event);
    }

}
