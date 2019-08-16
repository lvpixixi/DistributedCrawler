package yaycrawler.common.model;

import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2016/5/13.
 */
public class CrawlerResult {

	public static final int RESULT_CODE_NEW= 0;
	public static final int RESULT_CODE_UPDATECOUNT= 1;
	public static final int RESULT_CODE_FINISH= 2;
	public static final int RESULT_CODE_FAIL= 3;
	
	private String key;
    //private boolean isSuccess;
    private int status;
    private List<CrawlerRequest> crawlerRequestList;
    private String message;
    private Map<String,Object> data;

    public CrawlerResult() {
    }

    public CrawlerResult(int status, String key, List<CrawlerRequest> crawlerRequestList,String message) {
        //this.isSuccess = isSuccess;
        this.key = key;
        this.crawlerRequestList = crawlerRequestList;
        this.message = message;
        this.status = status;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<CrawlerRequest> getCrawlerRequestList() {
        return crawlerRequestList;
    }

    public void setCrawlerRequestList(List<CrawlerRequest> crawlerRequestList) {
        this.crawlerRequestList = crawlerRequestList;
    }

   /* public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }*/

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
    public String toString() {
        return "CrawlerResult{" +
                "key='" + key + '\'' +
                ", status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
	
	

}
