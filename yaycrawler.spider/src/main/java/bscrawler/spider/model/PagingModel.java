package bscrawler.spider.model;

import java.io.Serializable;
import java.net.URLEncoder;

/**
 * 分页model
 * @author 伟其
 *
 */
public class PagingModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 分页的XPath 
	 **/
	private String pagerXpath;
	
	/**
	 * 分页页数规则
	 */
	private String pageNumRules;
	
	/**
	 * 分页运行类型
	 */
	private String pagingType;
	
	/**
	 * 当前页
	 */
	private int currentPage;
	
	/**
	 * 分页URL拼接key
	 */
	private String pagingKey;
	
	/**
	 * 起始页
	 */
	private int startPage;
	
	/**
	 * 结束页
	 */
	private int endPage;
	
	/**
	 * 总页数
	 */
	private int totalPage;
	
	/**
	 * 附加参数
	 */
	private String extra;

	public String getPagerXpath() {
		return pagerXpath;
	}

	public void setPagerXpath(String pagerXpath) {
		this.pagerXpath = pagerXpath;
	}

	public String getPageNumRules() {
		return pageNumRules;
	}

	public void setPageNumRules(String pageNumRules) {
		this.pageNumRules = pageNumRules;
	}

	public String getPagingType() {
		return pagingType;
	}

	public void setPagingType(String pagingType) {
		this.pagingType = pagingType;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public String getPagingKey() {
		return pagingKey;
	}

	public void setPagingKey(String pagingKey) {
		this.pagingKey = pagingKey;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}
	
	public static void main(String[] args) {
		System.out.println(URLEncoder.encode("(archive)"));
	}
}
