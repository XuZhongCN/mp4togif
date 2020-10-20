package com.xuzhong.mp4togif.util;


import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.VideoAttributes;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class FileEncodeUtil {

    /**
     * 文件转码
     *
     * @param sourcePath
     *            源文件路径
     * @param targetPath
     *            目标文件路径
     * @return 转换是否成功
     * @author 许中
     * @date 2020年10月22日
     * @version 1.0.1
     */
    public static boolean encoder(String sourcePath, String targetPath) {
        File source = new File(sourcePath);
        File target = new File(targetPath);
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("libmp3lame");
        audio.setBitRate(new Integer(56000));
        audio.setChannels(new Integer(1));
        audio.setSamplingRate(new Integer(22050));
        VideoAttributes video = new VideoAttributes();
        video.setCodec("mpeg4");
        // video.setSize(new VideoSize(400, 300));
        video.setBitRate(new Integer(800000));
        video.setFrameRate(new Integer(15));
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("mp4");
        attrs.setAudioAttributes(audio);
        attrs.setVideoAttributes(video);
        Encoder encoder = new Encoder();
        try {
            encoder.encode(source, target, attrs);
            return true;
        } catch (Exception e) {
            log.info("视频转码失败");
            e.printStackTrace();
            return false;
        }
    }
}

