package com.simon816.minecraft.download;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

@Mod(modid = "pluginDownload:Client", clientSideOnly = true, version = "0.0.1", name = "Plugin Download")
public class ForgeMod {

    public static final URL repoUrl;
    private static File modDir;
    private SimpleNetworkWrapper channel;
    private NetworkManager netMan;

    static {
        try {
            repoUrl = new URL("http://www.simon816.com/minecraft/sponge/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        this.channel = NetworkRegistry.INSTANCE.newSimpleChannel(Constants.CHANNEL_NAME);
        this.channel.registerMessage(new PluginDataMessageForge.Handler(this), PluginDataMessageForge.class, 0, Side.SERVER);
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onConnected(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        System.out.println("Connected to server");
        this.netMan = event.getManager();
//        final PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
//        buffer.writeInt(Constants.MSG.ACK.ordinal());
//        event.manager.sendPacket(new C17PacketCustomPayload(Constants.CHANNEL_NAME, buffer));

//        NetworkDispatcher nd = event.manager.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
//        System.out.println(nd);
    }

    @SubscribeEvent
    public void onChangeGui(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiMultiplayer) {
            Collection<PluginDownloadTask> plugins = Lists.newArrayList();
            plugins.add(new PluginDownloadTask(new PluginInfo("test", "1.0.0")));
            plugins.add(new PluginDownloadTask(new PluginInfo("test", "1.0.0")));
            plugins.add(new PluginDownloadTask(new PluginInfo("test", "1.0.0")));
            plugins.add(new PluginDownloadTask(new PluginInfo("test", "1.0.0")));
            plugins.add(new PluginDownloadTask(new PluginInfo("test", "1.0.0")));
            plugins.add(new PluginDownloadTask(new PluginInfo("test", "1.0.0")));
            event.setGui(new GuiDownloadPlugins(Minecraft.getMinecraft().currentScreen, plugins));
        }
    }

    public static File getModDir() {
        if (modDir == null) {
            return modDir = new File(Minecraft.getMinecraft().mcDataDir, "mods");
        }
        return modDir;
    }

    public void recievePluginRequest(ArrayList<PluginDownloadTask> plugins) {
        System.out.println("Recieve payload from channel");
        System.out.println(plugins);
        this.netMan.closeChannel(new TextComponentString("Quitting"));
        // Minecraft.getMinecraft().loadWorld((WorldClient) null);
        Minecraft.getMinecraft()
                .displayGuiScreen(new GuiDownloadPlugins(Minecraft.getMinecraft().currentScreen, plugins));
    }

}
