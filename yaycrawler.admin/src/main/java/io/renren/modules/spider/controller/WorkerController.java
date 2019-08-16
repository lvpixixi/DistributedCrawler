package io.renren.modules.spider.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.common.utils.R;
import io.renren.modules.spider.communication.MasterActor;
import io.renren.modules.spider.entity.SpiderProjectEntity;
import io.renren.modules.spider.utils.Sublist;
import io.renren.modules.sys.controller.AbstractController;


/**
 * 工作节点监控控制器
 * @author wangdawei
 * @email dawei.happy@gmail.com
 * @date 2018-09-11 20:24:12
 */
@RestController
@RequestMapping("/spider/worker")
public class WorkerController extends AbstractController {
    @Autowired
    private MasterActor masterActor;
    
    /**
	 * worker列表
	 */
	@RequestMapping("/select")
	public R select(){
		//查询列表数据
        Object data = masterActor.retrievedWorkerRegistrations();
		return R.ok().put("list", data);
		
		
	}
	

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);
        Object data = masterActor.retrievedWorkerRegistrations();
        PageUtils pageUtil = null;
        if(data instanceof List){
        	List list = (List)data;
        	int total = list.size();
        	List pageList = Sublist.getPage(list, query.getPage(), query.getLimit());
    		pageUtil = new PageUtils(pageList, total, query.getLimit(), query.getPage());
        }else{
        	pageUtil = new PageUtils(new ArrayList(), 0, query.getLimit(), query.getPage());
        }
		
		
		return R.ok().put("page", pageUtil);
	}
}
