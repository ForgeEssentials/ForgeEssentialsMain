package com.forgeessentials.jscripting.wrapper.mc.event.fml.common;




public class JsItemPickupEvent extends JsPlayerEvent<PlayerEvent.ItemPickupEvent>{
    @SubscribeEvent
    public final void _handle(PlayerEvent.ItemPickupEvent event) {
        _callEvent(event);
    }
}
