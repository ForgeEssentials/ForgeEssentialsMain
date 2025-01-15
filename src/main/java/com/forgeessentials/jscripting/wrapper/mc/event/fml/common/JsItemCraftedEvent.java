package com.forgeessentials.jscripting.wrapper.mc.event.fml.common;




public class JsItemCraftedEvent extends JsPlayerEvent<PlayerEvent.ItemCraftedEvent>{
    @SubscribeEvent
    public final void _handle(PlayerEvent.ItemCraftedEvent event) {
        _callEvent(event);
    }
}
