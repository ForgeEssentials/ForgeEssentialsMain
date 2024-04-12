package com.forgeessentials.client.mixin;

import java.util.function.Supplier;

import net.minecraftforge.network.HandshakeHandler;
import net.minecraftforge.network.HandshakeMessages;
import net.minecraftforge.network.NetworkEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.forgeessentials.client.ForgeEssentialsClient;

@Mixin(HandshakeHandler.class)
public class ServerModListMixin
{
    /**
     * @author Maximuslotro
     * @reason get modlist from server connect
     */
    @Inject(at = @At("HEAD"),
            method = "handleServerModListOnClient",
            remap = false)
    public void getmodlist(HandshakeMessages.S2CModList serverModList, Supplier<NetworkEvent.Context> c, CallbackInfo info)
    {
        ForgeEssentialsClient.getServerMods(serverModList.getModList());
    }
}
