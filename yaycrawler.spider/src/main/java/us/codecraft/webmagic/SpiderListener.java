package us.codecraft.webmagic;

import java.util.Map;

/**
 * Listener of Spider on page processing. Used for monitor and such on.
 *
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public interface SpiderListener {

	/**
	 * 成功采集一个Url
	 * @param request
	 */
    public void onSuccess(Request request);
    
    /**
     * 成功处理一条记录
     * @param spiderId
     */
    public void onGetOneRecord(String spiderId,int count);

    /**
     * 失败是处理
     * @param request
     */
    public void onError(Request request);
    
    /**
     * 完成时处理
     * @param spiderId
     */
    public void onFinish(String spiderId);
}