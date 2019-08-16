package bscrawler.spider.listener;

import java.util.List;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;

public class RunTestSpiderListener implements SpiderListener {
	
	private Spider spider;
	private List<String> errorMsgs;
	private int maxRecordNum;
	private int currentCount = 0;
	private int reqCount = 0;
	
	public RunTestSpiderListener(Spider spider,int maxRecordNum,List<String> errorMsgs) {
		this.spider = spider;
		this.maxRecordNum = maxRecordNum;
		this.errorMsgs = errorMsgs;
	}

	@Override
	public void onSuccess(Request request) {
		++reqCount;
		if(reqCount>=this.maxRecordNum*10-1) {
			this.spider.stop();
		}
		
	}

	@Override
	public void onError(Request request) {
		errorMsgs.add(request.getUrl());
	}

	@Override
	public void onFinish(String spiderId) {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public void onGetOneRecord(String spiderId, int count) {
		++currentCount;
		if(currentCount>=this.maxRecordNum-1) {
			this.spider.stop();
		}
		
	}

	
}
