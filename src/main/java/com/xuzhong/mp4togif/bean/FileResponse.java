package com.xuzhong.mp4togif.bean;


import lombok.Data;

@Data
public class FileResponse {
    /**
     * 是否转码成功,默认成功
     */
    private boolean encode = true;
    /**
     * gif创建是否成功
     */
    private boolean gif = true;
    /**
     * 本地gif文件路径
     */
    private String gifUrl;
    /**
     * 本地视频路径
     */
    private String url;
    /**
     * 预览图本地存储路径
     */
    private String posterUrl;
    /**
     * 视频高
     */
    private Integer height = 400;
    /**
     * 视频宽
     */
    private Integer width = 300;

    public FileResponse() {
        super();
    }
}

