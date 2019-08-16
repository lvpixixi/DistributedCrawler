package yaycrawler.dao.domain;

import com.alibaba.fastjson.JSON;
import yaycrawler.common.model.CrawlerRequest;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

/**
 * mysql中的任务队列表
 * Created by  yuananyun on 2017/3/24.
 */
@Entity
@Table(name = "crawler_task")
public class CrawlerTask {
    /**
     * 任务的标识
     */
    @Id
    private String code;

    /**
     * 请求链接
     */
    @Column(columnDefinition = "varchar(300)")
    private String url;

    /**
     * 请求方法：get、post等
     */
    @Column(columnDefinition = "varchar(10)")
    private String method;

    /**
     * 请求的数据（JSON字符串）
     */
    @Column(columnDefinition = "text")
    private String data;

    /**
     * 其他额外的数据
     */
    @Column(name = "extend_data", columnDefinition = "text")
    private String extendData;

    @Column(name = "created_time", columnDefinition = " datetime default current_timestamp()")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;

    @Column(name = "started_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startedTime;

    @Column(name = "completed_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date completedTime;

    /**
     * 状态，未开始：-2；运行中：-1；失败：0；成功：1；超时：2
     */
    @Column(name = "status", columnDefinition = "tinyint default -2")
    private Integer status;

    /**
     * 表示任务在哪个worker上执行
     */
    @Column(name = "worker_id", columnDefinition = "varchar(100)")
    private String workerId;

    /**
     * 任务的执行信息
     */
    @Column(columnDefinition = "varchar(200)")
    private String message;


    public CrawlerTask() {

    }

    public CrawlerTask(String code, String url, String method, Map data, Map extendData) {
        this.code = code;
        this.url = url;
        this.method = method;
        if (data != null)
            this.data = JSON.toJSONString(data);
        if (extendData != null)
            this.extendData = JSON.toJSONString(extendData);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Map getDataMap() {
        if (this.data != null) {
            try {
                return JSON.parseObject(data, Map.class);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public void setData(Map data) {
        if (data != null)
            setData(JSON.toJSONString(data));
    }

    public Map getExtendDataMap() {
        if (this.extendData != null) {
            try {
                return JSON.parseObject(extendData, Map.class);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return null;
    }


    public CrawlerRequest convertToCrawlerRequest() {
        CrawlerRequest request = new CrawlerRequest(this.url, method, getDataMap());
        request.setHashCode(this.code);
        if (this.extendData != null)
            request.setExtendMap(getExtendDataMap());
        return request;
    }

    public static CrawlerTask convertFromCrawlerRequest(CrawlerRequest request) {
        return new CrawlerTask(request.getHashCode(), request.getUrl(), request.getMethod(),
                request.getData(), request.getExtendMap());
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(Date startedTime) {
        this.startedTime = startedTime;
    }

    public Date getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(Date completedTime) {
        this.completedTime = completedTime;
    }
}
