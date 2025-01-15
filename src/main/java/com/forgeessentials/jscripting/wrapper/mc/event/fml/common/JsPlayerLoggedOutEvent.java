package com.forgeessentials.jscripting.wrapper.mc.event.fml.common;




public class JsPlayerLoggedOutEvent extends JsPlayerEvent<PlayerEvent.PlayerLoggedOutEvent>{
    @SubscribeEvent
    public final void _handle(PlayerEvent.PlayerLoggedOutEvent event) {
        _callEvent(event);
    }
}
