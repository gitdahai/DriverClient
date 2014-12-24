package cn.hollo.www.upyun;

/**
 * Created by orson on 14-12-24.
 */
public interface OnDownloadFinishListener {
    public void onDownloadFinish(int responseCode, boolean isSuccess, String saveFileName, Object attach);
}
