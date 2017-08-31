package com.simon816.minecraft.download;

import org.spongepowered.api.Game;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;

import java.util.Collection;

public class EventListener {

    private final PluginDataMessage pluginDataMessage;
    private final Game game;

    private final LiteralText kickMessage;

    public EventListener(Game game) {
        this.game = game;
        Collection<PluginInfo> plugins = SpongePlugin.getClientPlugins();
        this.pluginDataMessage = new PluginDataMessage(plugins);

        LiteralText.Builder kickMessageBuilder =
                Text.builder("You are missing the following plugins required to play on this server:\n\n");
        for (PluginInfo plugin : plugins) {
            kickMessageBuilder.append(Text.of(plugin + "\n"));
        }
        kickMessageBuilder.append(Text.of("\nFor easy downloading in the future, use the Plugin Downloader\n(http://www.example.com)"));
        this.kickMessage = kickMessageBuilder.build();
    }

//    @Subscribe
//    public void onClientConnect(ClientConnectEvent event) {
//        NetHandlerLoginServer handler = (NetHandlerLoginServer) event.getConnection();
//        Boolean fmlMarker = handler.networkManager.channel().attr(NetworkRegistry.FML_MARKER).get();
//        boolean isFml = fmlMarker != null ? fmlMarker.booleanValue() : false;
//        if (!isFml) {
//            event.setCancelled(true);
//            event.setDisconnectMessage(this.kickMessage);
//            return;
//        }
//        // handler.networkManager.channel().pipeline().addBefore("packet_handler",
//        // "plugin_dld", new PHandler());
//    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        SpongePlugin.getInstance().getChannel().sendTo(event.getTargetEntity(), this.pluginDataMessage);
    }

}
