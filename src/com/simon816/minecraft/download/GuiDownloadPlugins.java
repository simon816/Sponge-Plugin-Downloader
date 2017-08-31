package com.simon816.minecraft.download;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class GuiDownloadPlugins extends GuiScreen {

    private GuiScreen parentScreen;
    private Collection<PluginDownloadTask> plugins;

    public GuiDownloadPlugins(GuiScreen parentScreen, Collection<PluginDownloadTask> plugins) {
        this.parentScreen = parentScreen;
        this.plugins = plugins;
    }

    private static final int BUTTON_BACK = 0;
    private static final int BUTTON_DLD_ALL = 1;
    private boolean initialized;
    private PluginDownloadList pluginsList;

    @Override
    public void initGui() {
        this.buttonList.clear();

        if (!this.initialized) {
            this.initialized = true;

            this.pluginsList = new PluginDownloadList(this, this.width, this.height, 32, this.height - 64, 50);
            this.pluginsList.populateList(this.plugins);
        } else {
            this.pluginsList.setDimensions(this.width, this.height, 32, this.height - 64);
        }

        this.createButtons();
    }

    @SuppressWarnings("unchecked")
    public void createButtons() {
        this.buttonList.add(new GuiButton(BUTTON_BACK, (this.width / 2) + 80, this.height - 28, 75, 20, I18n.format("gui.cancel")));
        this.buttonList.add(new GuiButton(BUTTON_DLD_ALL, this.width / 2 - 154, this.height - 28, 72, 20, "Download All"));
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.pluginsList.handleMouseInput();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.pluginsList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, "Plugins To Download", this.width / 2, 20, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.pluginsList.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Called when a mouse button is released. Args : mouseX, mouseY,
     * releaseButton
     */
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.pluginsList.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (!button.enabled) {
            return;
        }
        if (button.id == BUTTON_BACK) {
            this.mc.displayGuiScreen(this.parentScreen);
        } else if (button.id == BUTTON_DLD_ALL) {
            this.pluginsList.downloadAll();
        }
    }

    public void drawHover(List<String> textLines, int x, int y) {
        this.drawHoveringText(textLines, x, y);
    }

}
