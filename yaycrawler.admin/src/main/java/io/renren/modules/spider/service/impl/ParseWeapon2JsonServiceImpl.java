package io.renren.modules.spider.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.mss.crawler.custom.weapon.Weapon;
import com.mss.crawler.custom.weapon.WeaponBean;

import bscrawler.spider.util.HtmlFormatter;
import io.renren.modules.spider.dao.WeaponMapper;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

@Service 
public class ParseWeapon2JsonServiceImpl {

	private static final String path_label = "${RootPath}";
	
	@Autowired
	private WeaponMapper mapper; 
	
	public List<String> parseContent2Json() {
		List<Weapon> contents = mapper.getAllContent();
		
		List<String> jsons = new ArrayList<>();
		
		for(Weapon weapon : contents) {
			String json = processDetail(weapon);
			jsons.add(json);
		}
		
		return jsons;
	}
	
	private String processDetail(Weapon weapon){
		String contStr = weapon.getContent();
		// 去除科学技术的数字之间的 , 
		contStr = contStr.replaceAll("(?<=(\\d)*),(?=\\d{3})", "");
		// 将本地的文本放入到 构造器列表中
		Html html = new Html(contStr);
		WeaponBean entity = new WeaponBean();
		
		String contentXpath = "//div[@class='detail clearfix']";
		Selectable content = html.xpath(contentXpath);
		entity.setSynonymTitle(weapon.getSynonymTitle());
		entity.setClassify(weapon.getClassify());
		entity.setId(weapon.getId());
		entity.setBigclassify(weapon.getBigclassify());
		entity.setClassifyCode(weapon.getClassifyCode());
		entity.setName(content.xpath("//div[@class='detail clearfix']/div[1]/div[1]/span[1]/text() | //div[@class='detail clearfix']/div[1]/div[1]/div[1]/div[1]/span[1]/text()").get());
		// //div[@class='maxPic']/img/@src | //div[@class='dataInfo']/img/@src
		entity.setCover(path_label + content.xpath("//div[@class='detail clearfix']/div[1]/div[1]/img/@src | //div[@class='detail clearfix']/div[2]/div[1]/img/@src").get());
		//span[@class='country']/b/a/text()
		entity.setCountry(content.xpath("//div[@class='detail clearfix']/div[1]/div[1]/span[2]/b/a/text() | //div[@class='detail clearfix']/div[1]/div[1]/div[1]/div[1]/span[2]/b/a/text()").get());
		String summary = content.xpath("//div[@class='detail clearfix']/div[1]/div[2]/div[1]/p/text() | "
				+ "//div[@class='detail clearfix']/div[1]/div[1]/div[1]/p/text() | "
				+ "//div[@class='detail clearfix']/div[1]/div[1]/div[1]/span/text() | "
				+ "//div[@class='detail clearfix']/div[1]/div[1]/div[1]/text() | "
				+ "//div[@class='detail clearfix']/div[1]/div[2]/div[1]/text() | "
				+ "//div[@class='detail clearfix']/div[1]/div[2]/div[1]/p/span/text() |"
				+ "//div[@class='detail clearfix']/div[1]/div[2]/div[1]/div/text() |"
				+ "//div[@class='detail clearfix']/div[1]/div[1]/div[1]/div[2]/div/text()").get();
		// 若 摘要为空, 不进行处理
		if(summary.trim().length() < 2) {
			return "";
		}
		entity.setSummary(summary);
		entity.setContentHtml(content.xpath("//div[@class='detail clearfix']/div/div[3]/div | //div[@class='detail clearfix']/div/div[2]/div[1]").get());
		entity.setDatainfoHtml(content.xpath("//div[@class='detail clearfix']/div[2]/div").get());
		entity.setUrl(weapon.getUrl());
		Map<String,String> mainContent = new LinkedHashMap<>();
		
		List<Selectable> contentNodes = content.xpath("//div[@class='detail clearfix']/div[1]/div[3]/div/div | //div[@class='detail clearfix']/div[1]/div[2]/div/div").nodes();
		for(Selectable div:contentNodes){		
			String key = div.xpath("//h3/text()").get();
			String divContent = div.xpath("//div/div/div/text() | //div/div/ul/li/text() | //div/div/span/text() | //div/div/p/text() | //div/div/text() | //div/div/p/span/text() //div/div/p/span/span/text() | //div/div/ul/li/text()").get();
			if(!StringUtils.isEmpty(divContent)){
				mainContent.put(key, HtmlFormatter.html2text(divContent));
			}
		}
		
		Map<String,Object> infos = new LinkedHashMap<>();
		
		List<Selectable> infoNodes = content.xpath("//div[@class='detail clearfix']/div[2]/div[1]").nodes();
		
		try {
			infos.putAll(formatDataList(infoNodes.get(0)));
			
			String key = "";
			for(int i=1;i<infoNodes.size();i++){	
				if(i%2!=0){
					key = HtmlFormatter.html2text(infoNodes.get(i).get());
				}else{
					Map<String,String> value = formatDataList(infoNodes.get(i));
					System.out.println(value);
					infos.put(key, value);
					key="";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("=====================" + weapon);
		}
		
		entity.setContent(content.xpath("//div[@class='detail clearfix']/div/div[3]/div | //div[@class='detail clearfix']/div/div[2]/div[1]").get());
		entity.setInfos(content.xpath("//div[@class='detail clearfix']/div[2]/div").get());
		
		entity.setContentHtml(content.xpath("//div[@class='detail clearfix']/div/div[3]/div | //div[@class='detail clearfix']/div/div[2]/div[1]").get());
		entity.setDatainfoHtml(content.xpath("//div[@class='detail clearfix']/div[2]/div").get());
		
		String jsonStr = JSONObject.toJSONString(entity) ;
		return jsonStr;
	}
	
	private Map<String,String> formatDataList(Selectable list){
		//System.out.println("formatDataList"+"---------------------------------------------------------------");
		Map<String,String> result = new HashMap<>();
		for(Selectable node:list.xpath("//ul/li").nodes()){	
			String li_Str = HtmlFormatter.html2text(node.get());
			//System.out.println(li_Str);
			/*if(li_Str.indexOf("：")!=-1){
				String[] splits = StringUtils.split(li_Str,"：");
				result.put(splits[0], splits[1]);
				System.out.println(result);
			}*/
			result.putAll(str2Map(li_Str));
			
		}
		return result;
	}
	
	private Map<String,String> str2Map(String str){
		
		Map<String,String> result = new HashMap<>();
		if(str.indexOf("；")!=-1){
			String[] list = StringUtils.split(str,"；");
			for(String line :list){
				if(line.indexOf("：")!=-1){
					String[] splits = StringUtils.split(line,"：");
					result.put(HtmlFormatter.removeNum(splits[0]), splits[1]);
				}
			}
		}else{
			if(str.indexOf("：")!=-1){
				String[] splits = StringUtils.split(str,"：");
				result.put(splits[0], splits[1]);
			}
			
		}
		
		
		return result;
	}
	
	public static void main(String[] args) {
		String str = "<div class=\"detail clearfix\">  <div >   <div >    <div >     <div >      <span ><i ></i>D-21</span>      <span ><b><a href=\"/weaponmaps/list_182\" target=\"_blank\" title=\"查看美国军力信息\">美国</a></b></span>     </div>     <p> 　　D-21无人侦察机（Lockheed D-21）是美国空军所使用的一款三倍音速长程战略侦察机，取代原先的A-12侦察机。D-21无人侦察机从1962年10月开始研发，原本称为洛克希德Q-12设计案。 </p>    </div>   </div>   <div >    <div >     <div >      <h3 >使用情况</h3>      <div >       <div>        D-21高空高速无人机比A-12/SR-71“黑鸟”高空高速侦察机更加神秘，与后者不同，D-21仅仅实战出击了四次，就退出了历史舞台。据资料记载，这四次出击全部针对中国罗布泊核试验场。D-21从1969年11月到1971年3月，共进行了四次实战出击，全部针对中国罗布泊试验场，但四次出击全部遭到失败。其中前三次成功拍摄到情报照片，但全部回收失败，而第四次则在进入中国领空后失踪。       </div>      </div>     </div>    </div>   </div>    </div>  <div >   <div >    <img src=\"/weapon.huanqiu.com/image/771081ef00dace9524590f5317d877ea.jpg\" alt=\"图片名称\" >    <ul>     <li><span>名称：</span>D-21侦察机</li>     <li><span>首飞时间：</span>1964年</li>     <li><span>服役时间：</span>1969年</li>     <li><span>退役时间：</span>1971年</li>     <li><span>研发单位：</span>洛克希德公司</li>     <li><span>气动布局：</span>三角面</li>     <li><span>发动机数量：</span>双发</li>     <li><span>飞行速度：</span>超音速</li>    </ul>    <h4>技术数据</h4>    <ul >     <li><span>机长：</span><b>12.8米<a href=\"/Rank/aircraft_9_841_43824\" title=\"查看排名\" target=\"_blank\"></a></b></li>     <li><span>翼展：</span><b>5.79米<a href=\"/Rank/aircraft_9_842_43824\" title=\"查看排名\" target=\"_blank\"></a></b></li>     <li><span>机高：</span><b>2.14米<a href=\"/Rank/aircraft_9_844_43824\" title=\"查看排名\" target=\"_blank\"></a></b></li>     <li><span>发动机：</span>RJ43-MA-20S4冲压发动机</li>     <li><span>最大起飞重量：</span><b>5,000千克<a href=\"/Rank/aircraft_9_846_43824\" title=\"查看排名\" target=\"_blank\"></a></b></li>    </ul>    <h4>性能数据</h4>    <ul >     <li><span>最大飞行速度：</span><b>3,560千米每小时<a href=\"/Rank/aircraft_9_858_43824\" title=\"查看排名\" target=\"_blank\"></a></b></li>     <li><span>最大航程：</span><b>5,550千米<a href=\"/Rank/aircraft_9_859_43824\" title=\"查看排名\" target=\"_blank\"></a></b></li>    </ul>   </div>  </div> </div>";
		Weapon weapon = new Weapon();
		weapon.setContent(str);
		ParseWeapon2JsonServiceImpl impl = new ParseWeapon2JsonServiceImpl();
		String string = impl.processDetail(weapon);
		System.out.println(string);
	}
	
}
