package com.xuzhong.mp4togif.util;

import com.madgag.gif.fmsware.AnimatedGifEncoder;

import com.xuzhong.mp4togif.bean.FileResponse;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
public class GifUtil {

    /**
     * 默认每截取一次跳过多少帧（默认：2）
     */
    private static final Integer DEFAULT_MARGIN = 2;
    /**
     * 默认帧频率（默认：10）
     */
    private static final Integer DEFAULT_FRAME_RATE = 10;

    /**
     * 截取视频指定帧生成gif,存储路径同级下
     *
     * @param filePath
     *            视频文件路径
     * @param startFrame
     *            开始帧
     * @param frameCount
     *            截取帧数
     * @param frameRate
     *            帧频率(默认：3)
     * @param margin
     *            每截取一次跳过多少帧(默认：3)
     * @throws IOException
     * @author 许中
     * @date 2020年10月22日
     * @version 1.0.1
     */
    public static FileResponse buildGif(String filePath, int startFrame, int frameCount, Integer frameRate,
                                        Integer margin) throws IOException {
        FileResponse file = new FileResponse();
        if (margin == null) {
            margin = DEFAULT_MARGIN;
        }
        if (frameRate == null) {
            frameRate = DEFAULT_FRAME_RATE;
        }
        // gif存储路径
        String gifPath = filePath.substring(0, filePath.lastIndexOf(".")) + ".gif";
        // 输出文件流
        FileOutputStream targetFile = new FileOutputStream(gifPath);
        // 读取文件
        FFmpegFrameGrabber ff = new FFmpegFrameGrabber(filePath);
        Java2DFrameConverter converter = new Java2DFrameConverter();
        ff.start();
        try {
            Integer videoLength = ff.getLengthInFrames();
            log.info("视频帧长度:[{}]", videoLength);
            // 如果用户上传视频极短,不符合自己定义的帧数取值区间,那么获取从1/5处开始至1/2处结束生成gif
            if (startFrame > videoLength || (startFrame + frameCount * margin) > videoLength) {
                startFrame = videoLength / 5;
                frameCount = (videoLength - startFrame - 5) / margin;
            }
            ff.setFrameNumber(startFrame);
            log.info("起始位置[{}]帧数:[{}]跳步:[{}]", startFrame, frameCount, margin);
            AnimatedGifEncoder en = new AnimatedGifEncoder();
            en.setFrameRate(frameRate);
            log.info("帧频率设置为[{}]", frameRate);
            // 无限期的循环下去、注意，此参数设置必须在下面for循环之前，即在添加第一帧数据之前
            en.setRepeat(0);
            en.start(targetFile);
            // 预览图、当前未生成
            boolean poster = false;
            for (int i = 0; i < frameCount; i++) {
                // BufferedImage image = (BufferedImage)
                // converter.convert(ff.grabFrame()).getScaledInstance(300, 400,
                // Image.SCALE_DEFAULT);
                // log.info("图片质量压缩");
                // 截取一帧,确保截取的当前帧存在图片！
                if (!poster) {
                    Frame f = ff.grabFrame();
                    if (f != null) {
                        // 图片宽高即为视频宽高
                        file.setHeight(f.imageHeight);
                        file.setWidth(f.imageWidth);
                        File filePicture = new File(
                                filePath.substring(0, filePath.lastIndexOf(".")) + ".jpg");
                        log.info("vedio参数为[{}]", file);
                        log.info("文件参数:[{}]", filePicture);
                        // 获取图片信息
                        BufferedImage image = (BufferedImage) converter.getBufferedImage(f);
                        BufferedImage bi = new BufferedImage(file.getWidth(), file.getHeight(),
                                BufferedImage.TYPE_3BYTE_BGR);
                        bi.getGraphics().drawImage(
                                image.getScaledInstance(file.getWidth(), file.getHeight(), Image.SCALE_DEFAULT), 0, 0,
                                null);
                        // 生成视频预览图
                        ImageIO.write(image, "jpg", filePicture);
                        poster = true;
                        file.setPosterUrl(filePicture.getPath());
                    }
                }
                en.addFrame(converter.convert(ff.grabFrame()));
                // log.info("取帧位置[{}],参数[{}]", frameCount, ff.grabFrame());
                ff.setFrameNumber(ff.getFrameNumber() + margin);
                // log.info("设置下一帧位置:[{}]", ff.getFrameNumber());
            }
            en.finish();
        } finally {
            ff.stop();
            ff.close();
        }
        log.info("上传gif图片到oss文件存储,返回gif文件存储路径");
        file.setGifUrl(gifPath);
        return file;
    }



    public static void main(String[] args) {
        try {
            System.out.println(buildGif("D:/123.mp4", 5, 50, 10, 2));
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
    }
}
