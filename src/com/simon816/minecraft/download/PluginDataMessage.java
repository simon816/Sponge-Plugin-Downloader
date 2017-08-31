package com.simon816.minecraft.download;

import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.Message;

import java.util.Collection;

public class PluginDataMessage implements Message {

    private Collection<PluginInfo> plugins;

    public PluginDataMessage() {
    }

    public PluginDataMessage(Collection<PluginInfo> plugins) {
        this.plugins = plugins;
    }

    @Override
    public void readFrom(ChannelBuf buf) {
        // TODO Auto-generated method stub

    }

    @Override
    public void writeTo(ChannelBuf buf) {
        buf.writeInteger(this.plugins.size());
        for (PluginInfo plugin : this.plugins) {
            plugin.writeTo(buf);
        }
    }

}
