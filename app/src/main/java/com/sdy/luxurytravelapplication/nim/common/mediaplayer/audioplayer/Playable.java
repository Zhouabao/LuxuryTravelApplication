package com.sdy.luxurytravelapplication.nim.common.mediaplayer.audioplayer;

public interface Playable {
    long getDuration();

    String getPath();

    boolean isAudioEqual(Playable audio);
}