package com.andrewtse.testdemo.sound.pcm;

/**
 * @author xk
 * @date 2019/1/6
 * 录制监听
 */
public interface OnRecording {

    /**
     * 录制中监听
     *
     * @param data 数据
     * @param len  长度
     */
    void onRecording(byte[] data, int len);

    /**
     * 错误监听
     */
    void onError(Exception e);
}
