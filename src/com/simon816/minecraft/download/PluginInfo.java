package com.simon816.minecraft.download;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import org.spongepowered.api.network.ChannelBuf;

public final class PluginInfo {

    public final String name;
    public final String version;

    public PluginInfo(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public void writeTo(ChannelBuf buffer) {
        buffer.writeString(this.name);
        buffer.writeString(this.version);
    }

    @Override
    public String toString() {
        return this.name + "@" + this.version;
    }

    public static PluginInfo readFrom(ByteBuf buf) {
        return new PluginInfo(ByteBufUtils.readUTF8String(buf), ByteBufUtils.readUTF8String(buf));
    }

    public String asFileName() {
        return this.name + "-" + this.version + ".jar.php";
    }
}
