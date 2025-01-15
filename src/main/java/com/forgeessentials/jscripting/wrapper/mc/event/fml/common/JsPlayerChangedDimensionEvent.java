package com.forgeessentials.jscripting.wrapper.mc.event.fml.common;




public class JsPlayerChangedDimensionEvent extends JsPlayerEvent<PlayerEvent.PlayerChangedDimensionEvent>{
    @SubscribeEvent
    public final void _handle(PlayerEvent.PlayerChangedDimensionEvent event) {
        _callEvent(event);
    }
}
