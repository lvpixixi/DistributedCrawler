package io.renren.modules.spider.communication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import yaycrawler.common.model.CommunicationAPIs;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.model.QueueQueryParam;
import yaycrawler.common.model.RestFulResult;
import yaycrawler.common.utils.HttpUtils;



/**
 * Created by ucs_yuananyun on 2016/5/13.
 */
@Component
@PropertySource(value= "classpath:master.properties")
public class MasterActor {

    private static Logger logger = LoggerFactory.getLogger(MasterActor.class);
    @Value("${master.server.address}")
    private String masterServerAddress;
    @Value("${signature.token}")
    private String secret;

    /**
     * Admin向Master发送任务
     *
     * @return
     */
    public boolean publishTasks(CrawlerRequest... crawlerRequests) {
        String targetUrl = CommunicationAPIs.getFullRemoteUrl(masterServerAddress, CommunicationAPIs.ADMIN_POST_MASTER_TASK_REGEDIT);
        RestFulResult result = HttpUtils.doSignedHttpExecute(secret, targetUrl, HttpMethod.POST, crawlerRequests);
        if(result!=null && result.hasError()) logger.error(result.getMessage());
        return result != null && !result.hasError();
    }

    /**
     * 查询worker的注册信息
     * @return
     */
    public Object retrievedWorkerRegistrations() {
        String targetUrl = CommunicationAPIs.getFullRemoteUrl(masterServerAddress, CommunicationAPIs.ADMIN_POST_MASTER_RETRIVED_WORKERS);
        RestFulResult result = HttpUtils.doSignedHttpExecute(secret, targetUrl, HttpMethod.POST, null);
        return result.getData();
    }
    /**
     * 获取等待队列信息
     * @param queueQueryParam
     * @return
     */
    public Object retrievedWaitingQueueRegistrations(QueueQueryParam queueQueryParam) {
        String targetUrl = CommunicationAPIs.getFullRemoteUrl(masterServerAddress, CommunicationAPIs.ADMIN_POST_MASTER_RETRIVED_ITEM_QUEUES);
        RestFulResult result = HttpUtils.doSignedHttpExecute(secret, targetUrl, HttpMethod.POST, queueQueryParam);
        return result.getData();
    }

    /**
     * 获取成功队列信息
     * @param queueQueryParam
     * @return
     */
    public Object retrievedSuccessQueueRegistrations(QueueQueryParam queueQueryParam) {
        String targetUrl = CommunicationAPIs.getFullRemoteUrl(masterServerAddress, CommunicationAPIs.ADMIN_POST_MASTER_RETRIVED_SUCCESS_QUEUES);
        RestFulResult result = HttpUtils.doSignedHttpExecute(secret, targetUrl, HttpMethod.POST, queueQueryParam);
        return result.getData();
    }
    /**
     * 获取失败队列信息
     * @param queueQueryParam
     * @return
     */
    public Object retrievedFailQueueRegistrations(QueueQueryParam queueQueryParam) {
        String targetUrl = CommunicationAPIs.getFullRemoteUrl(masterServerAddress, CommunicationAPIs.ADMIN_POST_MASTER_RETRIVED_FAIL_QUEUES);
        RestFulResult result = HttpUtils.doSignedHttpExecute(secret, targetUrl, HttpMethod.POST, queueQueryParam);
        return result.getData();
    }

    /**
     * 获取运行中队列信息
     * @param queueQueryParam
     * @return
     */
    public Object retrievedRunningQueueRegistrations(QueueQueryParam queueQueryParam) {
        String targetUrl = CommunicationAPIs.getFullRemoteUrl(masterServerAddress, CommunicationAPIs.ADMIN_POST_MASTER_RETRIVED_RUNNING_QUEUES);
        RestFulResult result = HttpUtils.doSignedHttpExecute(secret, targetUrl, HttpMethod.POST, queueQueryParam);
        return result.getData();
    }
}
