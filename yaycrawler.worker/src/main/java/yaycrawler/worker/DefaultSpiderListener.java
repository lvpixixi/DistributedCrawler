package yaycrawler.worker;

import java.util.Map;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.SpiderListener;

public class DefaultSpiderListener implements SpiderListener {

	@Override
	public void onSuccess(Request request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(Request request) {
		System.out.println("onError"+request.getUrl());
	}

	@Override
	public void onFinish(String spiderId) {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public void onGetOneRecord(String spiderId, int count) {
		// TODO Auto-generated method stub
		
	}

	
}
