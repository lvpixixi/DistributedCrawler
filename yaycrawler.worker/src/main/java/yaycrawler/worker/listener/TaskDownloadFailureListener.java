package yaycrawler.worker.listener;

import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.SpiderListener;
import yaycrawler.common.model.CrawlerResult;
import yaycrawler.worker.communication.MasterActor;

/**
 * Created by ucs_yuananyun on 2016/5/16.
 */
@Component
public class TaskDownloadFailureListener implements SpiderListener {

    @Autowired
    private MasterActor masterActor;

    @Override
    public void onSuccess(Request request) {

    }

    @Override
    public void onError(Request request) {
        masterActor.notifyTaskFailure(new CrawlerResult(CrawlerResult.RESULT_CODE_FAIL, DigestUtils.sha1Hex(request.getUrl()), null,"页面下载失败！"));
    }

	@Override
	public void onFinish(String spiderId) {
		masterActor.notifyTaskFinish(new CrawlerResult(CrawlerResult.RESULT_CODE_FINISH, spiderId, null,"任务执行完成"));
	}

	@Override
	public void onGetOneRecord(String spiderId,int count) {
		Map<String,Object> data = Maps.newHashMap();
		data.put("count", count);
		CrawlerResult cr = new CrawlerResult(CrawlerResult.RESULT_CODE_UPDATECOUNT, spiderId, null,"爬取记录更新");
		cr.setData(data);
		masterActor.notifyTaskSuccess(cr);
		
	}
}
