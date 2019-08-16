package io.renren.modules.spider.controller;

import io.renren.modules.spider.service.IImport2CloudService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 *  将数据导出并上传到 云 的控制器
 * @author wgshb
 *
 */
@RequestMapping("/news")
@Controller
public class Import2CloudController {

	private Logger LOG = LoggerFactory.getLogger(Import2CloudController.class);
	
	@Autowired
	private IImport2CloudService import2CloudService;
	
	@ResponseBody
	@RequestMapping("/import2Cloud")
	public String import2Cloud(@RequestParam Map<String, Object> params) {
		String[] tables = {"news_cn", "news_en", "news_wx"};
		Date crawlerDate = null;
		String date = (String) params.get("date");
		try {
			crawlerDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
		} catch (ParseException e) {
				// 出错, 设置日期为当天
				crawlerDate = new Date();
				LOG.error("The date you input is bad format, Please make sure date likes 'yyyy-MM-dd'");
		}
		
		try {
			import2CloudService.importByDate(crawlerDate, tables);
		} catch (FileNotFoundException e) {
			LOG.error("Cant not find the zip from the given path!!");
			e.printStackTrace();
			return "2";
		}
		return "1";
	}
	
}
