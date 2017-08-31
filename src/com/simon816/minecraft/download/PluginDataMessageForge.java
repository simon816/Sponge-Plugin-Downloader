package com.simon816.minecraft.download;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;

public class PluginDataMessageForge implements IMessage {

    public static class Handler implements IMessageHandler<PluginDataMessageForge, IMessage> {

        private final ForgeMod mod;

        public Handler(ForgeMod mod) {
            this.mod = mod;
        }

        @Override
        public IMessage onMessage(PluginDataMessageForge message, MessageContext ctx) {
            this.mod.recievePluginRequest(message.plugins);
            return null;
        }

    }

    private ArrayList<PluginDownloadTask> plugins;

    @Override
    public void fromBytes(ByteBuf buf) {
        int len = buf.readInt();
        this.plugins = Lists.newArrayListWithCapacity(len);
        for (int i = 0; i < len; i++) {
            PluginInfo info = PluginInfo.readFrom(buf);
            this.plugins.add(new PluginDownloadTask(info));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        // TODO Auto-generated method stub

    }

}
