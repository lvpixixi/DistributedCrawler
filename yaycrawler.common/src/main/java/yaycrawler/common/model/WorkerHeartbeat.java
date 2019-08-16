package yaycrawler.common.model;

import java.util.List;

/**
 * Created by ucs_yuananyun on 2016/5/12.
 */
public class WorkerHeartbeat {
	//工作节点ID
    private String workerId;
    //工作节点路径
    private String workerContextPath;
    //工作节点心跳间隔
    private Long heartbeatInteval;
    //等待执行任务数
    private int waitTaskCount;
    /**
     * 已经完成的任务的列表
     */
    private List<CrawlerResult> completedCrawlerResultList;


    public WorkerHeartbeat(String workerId) {
        this.workerId = workerId;
    }

    public WorkerHeartbeat() {

    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public int getWaitTaskCount() {
        return waitTaskCount;
    }

    public void setWaitTaskCount(int waitTaskCount) {
        this.waitTaskCount = waitTaskCount;
    }

    public String getWorkerContextPath() {
        return workerContextPath;
    }

    public void setWorkerContextPath(String workerContextPath) {
        this.workerContextPath = workerContextPath;
    }

    public Long getHeartbeatInteval() {
        return heartbeatInteval;
    }

    public void setHeartbeatInteval(Long heartbeatInteval) {
        this.heartbeatInteval = heartbeatInteval;
    }


    public List<CrawlerResult> getCompletedCrawlerResultList() {
        return completedCrawlerResultList;
    }

    public void setCompletedCrawlerResultList(List<CrawlerResult> completedCrawlerResultList) {
        this.completedCrawlerResultList = completedCrawlerResultList;
    }
}
