package com.simon816.minecraft.download;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameLoadCompleteEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.network.ChannelBinding.IndexedMessageChannel;
import org.spongepowered.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;

@Plugin(id = "pluginDownload:Server", name = "Plugin Download")
public class SpongePlugin {

//
//    public static interface ClientModData extends DataManipulator<ClientModData> {
//
//    }
    @Inject private Game game;

    private static final Collection<PluginInfo> clientPlugins = new ArrayList<PluginInfo>();
    private boolean acceptRegistrations = true;
    private boolean isServer;

    private IndexedMessageChannel channel;

    private static SpongePlugin instance;

    public SpongePlugin() {
        SpongePlugin.instance = this;
    }

    public final static SpongePlugin getInstance() {
        return instance;
    }

    public ChannelBinding.IndexedMessageChannel getChannel() {
        checkState(this.channel != null, "Channel not created");
        return this.channel;
    }

    @Listener
    public void onInitalization(GamePreInitializationEvent event) {
        this.isServer = this.game.getPlatform().getType().isServer();
        if (!this.isServer) {
            return;
        }
        for (int i = 1; i < 11; i++) {
            this.addClientPlugin(new PluginInfo("Test Plugin " + i, "1.0." + i * 2));
        }
    }

//
//    @Subscribe
//    public void onPluginEvent(PluginMessageEvent event) {
//        event.reply(new MemoryDataContainer());
//        event.getMessageData();
//        PluginMessage.sendTo("SomePlugin", new MemoryDataContainer()).addListener(new Runnable() {
//
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//
//            }
//        }, Executors.newSingleThreadExecutor());
//        PluginMessage.sendOnlyInState(GameState.INITIALIZATION, "", new MemoryDataContainer());
//    }

    public void addClientPlugin(PluginInfo id) {
        checkState(this.acceptRegistrations, "Not accepting registrations. Should have done it earlier!");
        checkState(this.isServer, "Cannot accept registrations as we're on the client!");
        clientPlugins.add(id);
    }

    public static Collection<PluginInfo> getClientPlugins() {
        return ImmutableList.copyOf(clientPlugins);
    }

    @Listener
    public void onComplete(GameLoadCompleteEvent event) {
        this.acceptRegistrations = false;
        if (clientPlugins.size() > 0) {
            EventListener eventListener = new EventListener(this.game);
            this.channel = this.game.getChannelRegistrar().createChannel(this, Constants.CHANNEL_NAME);
            this.channel.registerMessage(PluginDataMessage.class, 0);
            this.game.getEventManager().registerListeners(this, eventListener);
        }
    }
}
