package com.example.split_video;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.NullOutput;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@org.springframework.stereotype.Service
public class Service {

    public void split(List<Video> videos) {
        Path pathToFFmpeg = FileSystems.getDefault().getPath("./bin");
        String videoDir = "./video_path/";
        String filename = "small_video";
        String pathToFile = videoDir + filename + ".mp4";
        long inputDuration;

        // Get input duration
        final AtomicLong durationMillis = new AtomicLong();

        FFmpeg.atPath(pathToFFmpeg)
                .addInput(UrlInput.fromUrl(pathToFile))
                .addOutput(new NullOutput())
                .setProgressListener(
                        progress -> durationMillis.set(progress.getTimeMillis())
                )
                .execute();

        inputDuration = durationMillis.get();

        System.out.println(filename + " duration: " + inputDuration + " milliseconds");
        Long temp = null;
        long count = 1;
        for (Video video : videos) {

            // Check time
            if (temp != null) {
                if (temp > video.getPointStart()) {
                    throw new IllegalArgumentException("Time start have better time before");
                }
            }
            temp = video.getPointStart() + video.getDuration();

            long remaining = inputDuration - video.getPointStart();
            long currOutputDuration = Math.min(remaining, video.getDuration());
            FFmpeg.atPath(pathToFFmpeg)
                    .addInput(
                            UrlInput.fromUrl(videoDir + filename + ".mp4")
                                    .setPosition(video.getPointStart(), TimeUnit.MILLISECONDS)
                                    .setDuration(currOutputDuration, TimeUnit.MILLISECONDS)
                    )
                    .addOutput(
                            UrlOutput.toPath(FileSystems.getDefault().getPath(videoDir + filename + "_part_" + count + ".mp4"))
                                    .setPosition(0, TimeUnit.MILLISECONDS)
                    )
                    .setOverwriteOutput(true)
                    .execute();
            count++;
        }
    }
}
