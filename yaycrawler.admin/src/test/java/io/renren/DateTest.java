package io.renren;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.StringUtils;

import com.mss.crawler.custom.weapon.KeywordsThesaurus;
import com.mss.crawler.custom.weapon.Weapon;

import io.renren.modules.spider.dao.KeywordsThesaurusMapper;
import io.renren.modules.spider.dao.NewsDao;
import io.renren.modules.spider.dao.WeaponMapper;
import io.renren.modules.spider.entity.News;
import io.renren.modules.spider.service.impl.IImport2CloudServiceImpl;
import io.renren.modules.spider.service.impl.ParseWeapon2JsonServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DateTest {

	private static final String prefix = "\\$\\{RootPath\\}";
	
	@Autowired
	private IImport2CloudServiceImpl import2CloudService;
	
	@Autowired
	private ParseWeapon2JsonServiceImpl impl;
	
	@Autowired
	private NewsDao dao;
	
	@Autowired
	private WeaponMapper mapper; 
	
	@Autowired
	private KeywordsThesaurusMapper thesaurusMapper;

	/**
	 * 获取所有的武器库的名称
	 */
	@Test
	public void testGatherWeaponNames() throws IOException {
		List<String> list = dao.getWeaponNames();
		System.out.println("================" + list.size());
		BufferedWriter writer = new BufferedWriter(new FileWriter("names.txt"));
		for(String str : list) {
			if(str.length() > 0) {
				writer.write(str);
				writer.newLine();
				writer.flush();
			}
		}
		writer.close();
    }

	/**
	 * 更新摘要字段
	 */
	@Test
	public void testUpdateNewSummary() {
		List<News> list = dao.selectNews2update();
		for(News news : list) {
			String summary = getSummaryByContent(news.getFormattedContent());
			news.setSummary(summary);
			//System.out.println(news.getSummary().length());
			if(news.getSummary().length() > 30) {
				dao.updateNewsSummary(news);
			}
			//dao.updateNewsSummary(news);
			
		}
	}
	
	private String getSummaryByContent(String content) {
		content = content.replaceAll("<p(.*?)>", "").replaceAll("</p>", "").replaceAll("<img(.*?)>", "");
		if(content.length() <= 200 && content.length() > 150) {
			return content.trim().substring(0, content.length() - 1) + "..."; 
		} else if(content.length() > 200) {
			return content.trim().substring(0, 200) + "...";
		}
		return "";
		
	}

	/**
	 * 抽取采集的数据进行格式化
	 */
	@Test
	public void testSeparateKeywords2Classifies() {
		List<KeywordsThesaurus> list = thesaurusMapper.getAllTargetFields();
		List<KeywordsThesaurus> keywords = new ArrayList<>(list.size());
		for(KeywordsThesaurus thesaurus : list) {
			// 生成临时的 KeywordsThesaurus, 用于存放相关的属性
			KeywordsThesaurus keywordThesaurus = new KeywordsThesaurus();
			keywordThesaurus.setId(thesaurus.getId());
			StringBuilder sb = new StringBuilder();
			if(!StringUtils.isEmpty(thesaurus.getUpperWords())) {
				sb.append(thesaurus.getUpperWords() + "&&&");
			}
			if(!StringUtils.isEmpty(thesaurus.getLowerWords())) {
				sb.append(thesaurus.getLowerWords() + "&&&");
			}
			if(!StringUtils.isEmpty(thesaurus.getReferWords())) {
				sb.append(thesaurus.getReferWords() + "&&&");
			}
			
			if(!StringUtils.isEmpty(sb.toString())) {
				// 仅仅包含一个属性值
				if(!sb.toString().replaceFirst("&&&", "").contains("&&&")) {
					String temp = sb.toString().replaceFirst("&&&", "").trim();
					String prefix = temp.substring(0, 1);
					String value = temp.substring(2, temp.length());
					switch (prefix) {
					case "S":
						keywordThesaurus.setUpperWords(value);
						break;
					case "F":
						keywordThesaurus.setLowerWords(value);
						break;
					case "C":
						keywordThesaurus.setReferWords(value);
						break;
					case "Y":
						keywordThesaurus.setFormalWords(value);
						break;
					case "D":
						keywordThesaurus.setInformalWords(value);
						break;
					}
				} else {
					// 包含多个属性值
					String[] words = sb.toString().trim().split("&&&");
					for(String word : words) {
						String prefix = word.substring(0, 1);
						String value = word.substring(2, word.length());
						switch (prefix) {
						case "S":
							keywordThesaurus.setUpperWords(value);
							break;
						case "F":
							keywordThesaurus.setLowerWords(value);
							break;
						case "C":
							keywordThesaurus.setReferWords(value);
							break;
						case "Y":
							keywordThesaurus.setFormalWords(value);
							break;
						case "D":
							keywordThesaurus.setInformalWords(value);
							break;
						}
					}
					
				}
				keywords.add(keywordThesaurus);
			}
		}
		System.out.println("总的数据量为 ------------> " + keywords.size());
		// 批量更新目标的词表相关的值
		for(int i = 0, n = keywords.size(); i < n; i ++) {
			if(i > 0 && i % 1000 == 0 || i == n - 1) {
				int startIndex = (i > 1000)?i - 1000 : 0;
				int toIndex = i == n - 1?n-1:i;
				List<KeywordsThesaurus> subList = keywords.subList(startIndex, toIndex);
				
				thesaurusMapper.batchUpdateKeywordsThesaurus(subList);
				System.out.println("更新结果 ------------- " + i + "条");
			}
		}
	}
	
	/**
	 * 去除采集的范畴号的空格
	 */
	@Test
	public void testTrimThesaurusNum() {
		List<KeywordsThesaurus> list = thesaurusMapper.getAllThesaurusNum4Trim();
		List<KeywordsThesaurus> keywords = new ArrayList<>(list.size());
		long starttime = System.currentTimeMillis();
		for(KeywordsThesaurus thesaurus : list) {
			if(!StringUtils.isEmpty(thesaurus.getCategoryNum())) {
				String categoryNum = thesaurus.getCategoryNum().trim();
				Matcher m = Pattern.compile("\\d{4}").matcher(categoryNum);
				if(m.find()) {
					//System.out.println(m.group());
					thesaurus.setCategoryNum(m.group());
					//thesaurusMapper.trimThesaurusNum(thesaurus);
					keywords.add(thesaurus);
				}
			}
		}
		for(int i = 0, n = list.size(); i < n; i ++) {
			if(i > 0 && i % 1000 == 0 || i == n - 1) {
				int startIndex = (i > 1000)?i - 1000 : 0;
				int toIndex = i == n - 1?n-1:i;
				List<KeywordsThesaurus> subList = keywords.subList(startIndex, toIndex);
				thesaurusMapper.batchUpdateKeywordsThesaurus(subList);
			}
		}
		
		System.out.println("===运行耗时" + (System.currentTimeMillis() - starttime) + "SSS");
	}
	
	/**
	 * 添加关键字范畴相关的关系到表
	 * @throws IOException 
	 */
	@Test
	public void testInsert2Thesaurus() throws IOException {
		Map<String, Map<String, String>> data = new HashMap<>();
		//BufferedReader br = new BufferedReader(new FileReader(new File("words.txt")));
		BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream("words.txt"),"GB2312"));
		String content = null;
		//List<String> jsons = new ArrayList<String>(100);
		while((content = br.readLine()) != null) {
			try {
				int fromindex = content.indexOf("id=\"") + 4;
				String dataKey = content.subSequence(fromindex, fromindex + 6).toString();
				String[] as = content.split("</a>"); 
				Map<String, String> temp = new HashMap<>();
				KeywordsThesaurus thesaurus = new KeywordsThesaurus();
				thesaurus.setClassifyNum(dataKey);
				for(String a : as) {
					if(a.contains("<a")) {
						String tempKey = a.subSequence(a.indexOf("'") + 1, a.indexOf("'") + 5).toString().trim();
						String tempVal = a.subSequence(a.indexOf(tempKey) + 8, a.length()).toString().trim();
						//temp.put(tempKey, tempVal);
						thesaurus.setCategoryNum(tempKey);
						thesaurus.setCategoryName(tempVal);
						thesaurusMapper.insertThesaurus(thesaurus);
					}
				}
				//data.put(dataKey, temp);
			} catch (Exception e) {
				System.out.println("=====" + content.subSequence(0, 30));
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 将数据库的新闻字段的正文部分的内容进行格式化
	 * 		去除内部的样式, 仅保留内部文本的内容
	 * 		换行得有提示
	 */
	@Test
	public void testFormatContent() {
		Map<String, Object> params = new HashMap<>();
		String tablename = "news_en_copy";
		params.put("tbname", tablename);
		params.put("pubdate", "1970-01-01");
		params.put("crawlerdate", "1970-01-01");
		List<Map<String,Object>> list = dao.getAllBy(params);
		for(Map<String,Object> map : list) {
			String content = (String) map.get("content_tr");
			String id = (String) map.get("id");
			String formattedContent = formatContent(content);
			dao.updateNews(formattedContent, tablename, id);
		}
	}
	
	/**
	 * 格式化新闻的正文...
	 * @param content
	 * @return
	 */
	private String formatContent(String content) {
		// 对于外文的正文部分的开头内容进行替换标签的操作
		content = content.replaceAll("<div(.*?)>", "<p>").replaceAll("</div>", "</p>");
		StringBuilder sb = new StringBuilder();
		// 判断正文部分若包含 img 标签, img 标签是否被 p 标签包围
		String regex = "<p(.*?)<img(.*?)</p";
		String imgRegex = "<img(.*?)>";
		Matcher m = Pattern.compile(regex).matcher(content);
		Matcher imgMatcher = Pattern.compile(imgRegex).matcher(content);
		boolean flag = content.contains("<img") && !m.matches();
		if(flag) {
			while(imgMatcher.find()) {
				String imgStr = imgMatcher.group();
				imgStr = imgStr.replaceAll("\\.", "&&&").replaceAll("\\$\\{", "&&").replaceAll("\\}", "@@@");
				content = content.replaceAll("\\.", "&&&").replaceAll("\\$\\{", "&&").replaceAll("\\}", "@@@");
				String replaceMent = "<p>" + imgStr + "</p>";
				content = content.replace(imgStr, replaceMent).replace("&&&", ".").replace("&&", "${").replace("@@@", "}");
			}
		}
		
		// 正文部分由 br 进行换行
		if(!content.contains("<p") && content.contains("<br>")) {
			content = content.replaceAll("<div(.*?)>", "").replaceAll("</div>", "");
			regex = "(.*?)<br>";
			Matcher brMatcher = Pattern.compile(regex).matcher(content);
			while(brMatcher.find()) {
				//System.out.println(brMatcher.group());
				content = content.replace(brMatcher.group(), "<p>" + brMatcher.group() + "</p>");
			}
		}
		
		Document d = Jsoup.parseBodyFragment(content);
		// 获取指定的标签的集合
		Elements elementsByTag = d.getElementsByTag("p");
		for(Element e : elementsByTag) {
			if(e.children().size() > 0 && e.children().toString().contains("<img")) {
				sb.append("<p class=\"detail-pic\">" + e.children().toString() + "</p>");
				//System.out.println("<p>" + e.children().toString() + "</p>");
			} else {
				if(!StringUtils.isEmpty(e.text())) {
					sb.append("<p>" + e.text() + "</p>");
				}
				//System.out.println("<p>" + e.text() + "</p>");
			}
		}
		return sb.toString();
	}
	/**
	 * 为武器库添加同义词
	 * @throws IOException
	 */
	@Test
	public void testAddWeaponNames4Search() throws IOException {
		List<Weapon> list = mapper.getAllContent();
		System.out.println("================" + list.size());
		for(Weapon weapon : list) {
			String synonymTitle = getSynonymTitle(weapon.getTitle());
			if(!synonymTitle.equals(weapon.getTitle())) {
				weapon.setSynonymTitle(weapon.getTitle() + "," + synonymTitle);
			} else {
				weapon.setSynonymTitle(synonymTitle);
			}
			mapper.addSynonymTitle(weapon);
		}
	}
	
	private String getSynonymTitle(String title) {
		String synonymTitle = ""; 
		if(title.trim().contains("/")) {
			String[] temp = title.trim().split("/");
			/*String tempTitle = title.replaceAll("/", "");
			String regexTemp = "[0-9]+";
			Matcher m2 = Pattern.compile(regexTemp).matcher(tempTitle);
			List<String> list = new ArrayList<>();
			while(m2.find()) {
				list.add(m2.group());
				tempTitle = tempTitle.replaceAll(m2.group(), "&&");
			}
			for(String str : temp) {
				m2 = Pattern.compile(regexTemp).matcher(str);
				if(m2.find()) {
					synonymTitle += tempTitle.replace("&&", m2.group()) + ",";
				}
			}*/
			for(String str : temp) {
					synonymTitle += str + ",";
			}
			synonymTitle = synonymTitle.substring(0, synonymTitle.length() - 1).replaceAll("\\.", "").replaceAll("“", "").replaceAll("”", "").replaceAll("\\)", "").replaceAll("）", "").replaceAll("\\([\\s\\S]+", "").replaceAll("\\（[\\s\\S]+", "").replaceAll("/[\\s\\S]+", "").replaceAll(" ", "").replaceAll("-", "");
			System.out.println("===============5" + synonymTitle);
			return synonymTitle;
		}
		if(title.trim().contains(".")) {
			synonymTitle = title.replaceAll("\\.", "").replaceAll("“", "").replaceAll("”", "").replaceAll("\\([\\s\\S]+", "").replaceAll("\\（[\\s\\S]+", "").replaceAll("/[\\s\\S]+", "").replaceAll(" ", "").replaceAll("-", "");
			System.out.println(synonymTitle);
			return synonymTitle;
		}
		if(title.trim().contains("“") || title.trim().contains("”")) {
			synonymTitle = title.replaceAll("“", "").replaceAll("”", "").replaceAll("\\([\\s\\S]+", "").replaceAll("\\（[\\s\\S]+", "").replaceAll("/[\\s\\S]+", "").replaceAll(" ", "").replaceAll("-", "");
			System.out.println(synonymTitle);
			return synonymTitle;
		}
		if(title.trim().contains("(") || title.trim().contains("（")) {
			synonymTitle = title.replaceAll("\\([\\s\\S]+", "").replaceAll("\\（[\\s\\S]+", "").replaceAll(" ", "").replaceAll("-", "");
			System.out.println(synonymTitle);
			return synonymTitle;
		}
		if(title.trim().contains("\\u201c") || title.trim().contains("\\u201d")) {
			synonymTitle = title.replaceAll("\\u201c", "").replaceAll("\\u201d", "").replaceAll(" ", "").replaceAll("-", "");
			System.out.println(synonymTitle);
			return synonymTitle;
		}
		
		if(title.trim().contains(" ")) {
			synonymTitle = title.replaceAll(" ", "").replaceAll("-", "");
			System.out.println(synonymTitle);
			return synonymTitle;
		}
		if(title.trim().contains("-")) {
			synonymTitle = title.replaceAll("-", "");
			System.out.println(synonymTitle);
			return synonymTitle;
		}
		String regex = "[a-zA-Z\u4e00-\u9fa5][a-zA-Z0-9\u4e00-\u9fa5]+";
		Matcher m = Pattern.compile(regex).matcher(title);
		if(m.find()) {
			synonymTitle = m.group();
			System.out.println("===============" + m.group());
			return synonymTitle;
		}
		return synonymTitle;
	}

	/*
	 * 为数据库的数据添加指定的前缀
	 
	@Test
	public void testAddPrefix() {
		List<News> newses = dao.getNamespace4Replace();
		List<News> lists = new ArrayList<>();
		for(News news : newses) {
			String attchfiles = news.getAttchfiles();
			String title = news.getTitle();
			String content = news.getContent();
			String contentTr = news.getContentTr();
			if(StringUtils.isEmpty(attchfiles) || attchfiles.contains("${RootPath}")) {
				continue;
			}
			
			if(StringUtils.isEmpty(attchfiles) || contentTr.contains("${RootPath}")) {
				continue;
			}
			System.out.println("--------------begin" + contentTr.length());
			contentTr = addPrefix(contentTr);
			content = addPrefix(content);
			System.out.println("--------------after" + contentTr.length());
			
			attchfiles = addPrefix(attchfiles);
			news.setAttchfiles(attchfiles);
			//news.setContentTr(contentTr);
			//news.setContent(content);
			//news.setTitle(title);
			//lists.add(news);
			dao.updateNews(news, "news_cn_copy");
			
			if((!StringUtils.isEmpty(attchfiles) && !attchfiles.contains("${RootPath}")) || (!StringUtils.isEmpty(title) && !title.contains("${RootPath}"))) {
				if(attchfiles != null) {
					attchfiles = addPrefix(attchfiles);
				}
				if(title != null) {
					title = addPrefix(title);
				}
				//content = addPrefix(content);
				news.setAttchfiles(attchfiles);
				news.setContent(content);
				news.setTitle(title);
				lists.add(news);
			}
		}
		// 更新数据库
		//dao.updatebatch(lists);
	}
	*/
	
	// 添加指定的前缀
	private String addPrefix(String str) {
		if(str.contains("null\\")) {
			str = str.replaceAll("null", "");
			str = str.replaceAll("\\\\", "/");
			str = str.replaceAll("//", "/");
		}
		//(?<=src=\")
		String regex = "/[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+.?";
		Matcher m = Pattern.compile(regex).matcher(str);
		String temp = null;
		if(m.find()) {
			//System.out.println(m.group());
			String group = m.group();
			temp = group.subSequence(0, group.lastIndexOf("/") - 6).toString();
			System.out.println(temp + "===> ");
		}
		if(temp != null) {
			String replacement = "\\$\\{RootPath\\}" + temp;
			str = str.replaceAll(temp, replacement);
			return str;
		}
		return str;
	}
	
	@Test
	public void testParse() {
		String str = "<div class=\"detail clearfix\">  <div >   <div >    <div >     <div >      <span ><i ></i>D-21</span>      <span ><b><a href=\"/weaponmaps/list_182\" target=\"_blank\" title=\"查看美国军力信息\">美国</a></b></span>     </div>     <p> 　　D-21无人侦察机（Lockheed D-21）是美国空军所使用的一款三倍音速长程战略侦察机，取代原先的A-12侦察机。D-21无人侦察机从1962年10月开始研发，原本称为洛克希德Q-12设计案。 </p>    </div>   </div>   <div >    <div >     <div >      <h3 >使用情况</h3>      <div >       <div>        D-21高空高速无人机比A-12/SR-71“黑鸟”高空高速侦察机更加神秘，与后者不同，D-21仅仅实战出击了四次，就退出了历史舞台。据资料记载，这四次出击全部针对中国罗布泊核试验场。D-21从1969年11月到1971年3月，共进行了四次实战出击，全部针对中国罗布泊试验场，但四次出击全部遭到失败。其中前三次成功拍摄到情报照片，但全部回收失败，而第四次则在进入中国领空后失踪。       </div>      </div>     </div>    </div>   </div>    </div>  <div >   <div >    <img src=\"/weapon.huanqiu.com/image/771081ef00dace9524590f5317d877ea.jpg\" alt=\"图片名称\" >    <ul>     <li><span>名称：</span>D-21侦察机</li>     <li><span>首飞时间：</span>1964年</li>     <li><span>服役时间：</span>1969年</li>     <li><span>退役时间：</span>1971年</li>     <li><span>研发单位：</span>洛克希德公司</li>     <li><span>气动布局：</span>三角面</li>     <li><span>发动机数量：</span>双发</li>     <li><span>飞行速度：</span>超音速</li>    </ul>    <h4>技术数据</h4>    <ul >     <li><span>机长：</span><b>12.8米<a href=\"/Rank/aircraft_9_841_43824\" title=\"查看排名\" target=\"_blank\"></a></b></li>     <li><span>翼展：</span><b>5.79米<a href=\"/Rank/aircraft_9_842_43824\" title=\"查看排名\" target=\"_blank\"></a></b></li>     <li><span>机高：</span><b>2.14米<a href=\"/Rank/aircraft_9_844_43824\" title=\"查看排名\" target=\"_blank\"></a></b></li>     <li><span>发动机：</span>RJ43-MA-20S4冲压发动机</li>     <li><span>最大起飞重量：</span><b>5,000千克<a href=\"/Rank/aircraft_9_846_43824\" title=\"查看排名\" target=\"_blank\"></a></b></li>    </ul>    <h4>性能数据</h4>    <ul >     <li><span>最大飞行速度：</span><b>3,560千米每小时<a href=\"/Rank/aircraft_9_858_43824\" title=\"查看排名\" target=\"_blank\"></a></b></li>     <li><span>最大航程：</span><b>5,550千米<a href=\"/Rank/aircraft_9_859_43824\" title=\"查看排名\" target=\"_blank\"></a></b></li>    </ul>   </div>  </div> </div>";
		Weapon weapon = new Weapon();
		weapon.setContent(str);
		
	}
	
	// 将数据库的数据写出到 text 中
	@Test
	public void testWeapon() throws IOException {
		List<String> list = impl.parseContent2Json();
		System.out.println("================" + list.size());
		BufferedWriter writer = new BufferedWriter(new FileWriter("1.txt"));
		for(String str : list) {
			if(str.length() > 0) {
				writer.write(str);
				writer.newLine();
				writer.flush();
			}
		}
		writer.close();
		/*for(int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i).length() + " --= ");
		}*/
	}
	
	@Test
	public void test() throws FileNotFoundException, ParseException {
		String[] tables = {"news_cn_copy", "news_en_copy", "news_wx_copy"};
		import2CloudService.importByDate(new SimpleDateFormat("yyyy-MM-dd").parse("2018-01-01"), tables);
	}

}
