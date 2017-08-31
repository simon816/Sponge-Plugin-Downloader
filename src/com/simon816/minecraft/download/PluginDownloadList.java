package com.simon816.minecraft.download;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.simon816.minecraft.download.PluginDownloadTask.State;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraftforge.fml.client.config.HoverChecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PluginDownloadList extends GuiListExtended {

    private final ArrayList<PluginEntry> plugins;
    private final GuiDownloadPlugins hostGui;

    public PluginDownloadList(GuiDownloadPlugins hostGui, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
        super(hostGui.mc, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.plugins = Lists.newArrayList();
        this.hostGui = hostGui;
    }

    @Override
    public IGuiListEntry getListEntry(int index) {
        return this.plugins.get(index);
    }

    @Override
    protected int getSize() {
        return this.plugins.size();
    }

    @Override
    protected int getScrollBarX() {
        return super.getScrollBarX() + 30;
    }

    @Override
    public int getListWidth() {
        return super.getListWidth() + 85;
    }

    private class PluginEntry implements IGuiListEntry, FutureCallback<Void> {

        private final PluginDownloadTask task;
        private final ArrayList<GuiButton> buttons;
        private final HoverChecker openWebpageHoverChecker;
        private final List<String> urlHoverText;

        public PluginEntry(PluginDownloadTask plugin) {
            this.task = plugin;
            this.buttons = Lists.newArrayList();
            this.buttons.add(new GuiButton(0, 0, 0, 100, 20, "Download"));
            this.buttons.add(new GuiButton(1, 0, 0, 100, 20, "Open webpage"));
            this.openWebpageHoverChecker = new HoverChecker(this.buttons.get(1), 0);
            this.urlHoverText = Arrays.asList(this.task.info.name);
        }

        @Override
        public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
        }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
            FontRenderer render = PluginDownloadList.this.mc.fontRendererObj;
            int bw = render.getCharWidth('|');
            int n = (listWidth / bw) * this.task.progress / 100;

            render.drawString(this.task.info.toString(), x, y, 0xFFFFFF);
            StringBuilder barBuilder = new StringBuilder();
            for (int i = 0; i < n; i++) {
                barBuilder.append('|');
            }
            int c = 0x00FF30;
            if (this.task.state == State.WAITING || this.task.state == State.FETCH_HEADER) {
                c = 0x0000FF;
            }
            render.drawString(barBuilder.toString(), x, y + render.FONT_HEIGHT, c);
            StringBuilder remainderBuilder = new StringBuilder();
            for (int i = listWidth / bw - n; i > 0; i--) {
                remainderBuilder.append('|');
            }
            render.drawString(remainderBuilder.toString(), x + bw * n, y + render.FONT_HEIGHT, 0x505050);

            int buttonOffset = 0;
            for (GuiButton button : this.buttons) {
                button.xPosition = x + buttonOffset;
                button.yPosition = y + render.FONT_HEIGHT * 2;
                button.drawButton(PluginDownloadList.this.mc, mouseX, mouseY);
                buttonOffset += button.width + 5;
            }
            if (this.openWebpageHoverChecker.checkHover(mouseX, mouseY)) {
                PluginDownloadList.this.hostGui.drawHover(this.urlHoverText, mouseX, mouseY);
            }
        }

        @Override
        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
            System.out.println("PluginDownloadList.PluginEntry.mousePressed()");
            for (GuiButton button : this.buttons) {
                if (button.mousePressed(PluginDownloadList.this.mc, mouseX, mouseY)) {
                    this.handleButtonPress(button);
                    return true;
                }
            }
            return false;
        }

        private void handleButtonPress(GuiButton button) {
            if (button.id == 0) {
                if (!button.enabled) {
                    // Downloaded, nothing more to do
                    return;
                }
                if (this.task.isRunning()) {
                    button.displayString = "Download";
                    this.task.cancel();
                } else {
                    button.displayString = "Cancel";
                    this.task.begin(this);
                }
            } else if (button.id == 1) {

            }
        }

        @Override
        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
        }

        @Override
        public void onSuccess(Void result) {
            this.buttons.get(0).enabled = false;
        }

        @Override
        public void onFailure(Throwable t) {
        }
    }

    public void populateList(Collection<PluginDownloadTask> plugins) {
        if (plugins == null) {
            return;
        }
        for (PluginDownloadTask plugin : plugins) {
            this.plugins.add(new PluginEntry(plugin));
        }
    }

    public void downloadAll() {
        for (PluginEntry plugin : this.plugins) {
            // 'click' the download button
            plugin.handleButtonPress(plugin.buttons.get(0));
        }
    }
}
