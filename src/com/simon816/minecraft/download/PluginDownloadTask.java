package com.simon816.minecraft.download;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.IProgressUpdate;

import java.io.File;
import java.util.Random;

public class PluginDownloadTask {

    public static enum State {
        NOT_STARTED, FETCH_HEADER, WAITING, DOWNLOADING, FINISHED
    }

    public int progress;
    public final PluginInfo info;
    private ListenableFuture<Void> activeDownload;
    private final File targetFile;
    private final String url;
    public State state = State.NOT_STARTED;

    public PluginDownloadTask(PluginInfo info) {
        this.info = info;
        String fileName = this.info.asFileName();
        this.targetFile = new File(ForgeMod.getModDir(), fileName + (new Random()).nextLong());
        this.url = ForgeMod.repoUrl.toString() + "/" + this.info.name + "/" + fileName;
    }

    @Override
    public String toString() {
        return this.info.toString();
    }

    @SuppressWarnings("unchecked")
    public void begin(FutureCallback<Void> callback) {
        this.state = State.FETCH_HEADER;
        // TODO validate ID
        this.activeDownload =
                (ListenableFuture) HttpUtil.downloadResourcePack(this.targetFile, this.url, ResourcePackRepository.func_190115_a(), 0,
                        new UpdateGui(),
                        Minecraft.getMinecraft().getProxy());
        Futures.addCallback(this.activeDownload, callback);
        Futures.addCallback(this.activeDownload, new FutureCallback<Void>() {

            @Override
            public void onSuccess(Void result) {
                System.out.println("onSuccess(" + result + ")");
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("onFailure(" + t + ")");
                PluginDownloadTask.this.progress = 0;
            }
        });
    }

    public void cancel() {
        if (this.activeDownload != null) {
            System.out.println(this.activeDownload.cancel(true));
        }
    }

    public boolean isRunning() {
        return this.activeDownload != null && !this.activeDownload.isDone();
    }

    private void done() {
        // TODO Auto-generated method stub

    }

    private class UpdateGui implements IProgressUpdate {

        @Override
        public void displaySavingString(String message) {
        }

        @Override
        public void resetProgressAndMessage(String message) {
        }

        @Override
        public void displayLoadingString(String message) {
        }

        @Override
        public void setLoadingProgress(int progress) {
            if (progress == 100 && PluginDownloadTask.this.state == State.FETCH_HEADER) {
                PluginDownloadTask.this.state = State.WAITING;
            } else if (PluginDownloadTask.this.state == State.WAITING) {
                PluginDownloadTask.this.state = State.DOWNLOADING;
            }
            PluginDownloadTask.this.progress = progress;
        }

        @Override
        public void setDoneWorking() {
            PluginDownloadTask.this.state = State.FINISHED;
            PluginDownloadTask.this.done();
        }

    }

}
