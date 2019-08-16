package io.renren.modules.spider.controller;

import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.modules.spider.entity.SpiderProjectEntity;
import io.renren.modules.spider.service.IUnpackRecordService;
import io.renren.modules.spider.service.SpiderProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/public")
public class Public2AppController {

	private Logger LOG = LoggerFactory.getLogger(Public2AppController.class);
	
	@Autowired
	private SpiderProjectService spiderProjectService;
	
	@Autowired
	private IUnpackRecordService unpackRecordService;
	
	@RequestMapping("/getProjects")
	@ResponseBody
	public R getProjects() {
		List<SpiderProjectEntity> list = spiderProjectService.getProjects();
		return R.ok().put("list", list);
	}
	
	@RequestMapping("/searchByQuery")
    @ResponseBody
    public R searchByQuery(@RequestParam Map<String, Object> params){
    	PageUtils pageUtil = unpackRecordService.searchByQuery(params);
    	return R.ok().put("page", pageUtil);
    }
	
	@RequestMapping("/openpath")
	public R openpath(@RequestBody String path) {
		try {
			String decode = URLDecoder.decode(path,"UTF-8");
			if(decode.contains("/")) {
				decode = decode.replace("/", File.separator);
			}
			Runtime.getRuntime().exec("explorer " + decode);
		} catch (IOException e) {
			e.printStackTrace();
			return R.error();
		}
		return R.ok();
	}
	
	@RequestMapping("/send")
    @ResponseBody
    public R send(@RequestBody String params){
    	try {
			unpackRecordService.send(params);
		} catch (Exception e) {
			e.printStackTrace();
			return R.error();
		}
    	return R.ok();
    }
	
}
