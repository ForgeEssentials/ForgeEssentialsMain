package com.forgeessentials.jscripting.wrapper.mc.event.fml.common;




public class JsPlayerLoggedInEvent extends JsPlayerEvent<PlayerEvent.PlayerLoggedInEvent>{
    @SubscribeEvent
    public final void _handle(PlayerEvent.PlayerLoggedInEvent event) {
        _callEvent(event);
    }
}
