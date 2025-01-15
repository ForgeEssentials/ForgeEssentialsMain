package com.forgeessentials.jscripting.wrapper.mc.event.fml.common;




public class JsPlayerRespawnEvent extends JsPlayerEvent<PlayerEvent.PlayerRespawnEvent>{
    @SubscribeEvent
    public final void _handle(PlayerEvent.PlayerRespawnEvent event) {
        _callEvent(event);
    }
}
