package com.forgeessentials.jscripting.wrapper.mc.event.fml.common;




public class JsItemSmeltedEvent extends JsPlayerEvent<PlayerEvent.ItemSmeltedEvent>{
    @SubscribeEvent
    public final void _handle(PlayerEvent.ItemSmeltedEvent event) {
        _callEvent(event);
    }
}
