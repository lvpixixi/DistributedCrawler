package bscrawler.spider;

public final class SpiderConstants {
	
	//通用网站处理模型
	public static final String SITE_PAGE_PROCESSORMODEL="site";
	
	//主题网站处理模型
	public static final String SUBJECT_PAGE_PROCESSORMODEL="subject";
	
	//微信公众号处理模型
	public static final String WEIXIN_PAGE_PROCESSORMODEL="weixin";
	
	//微信搜索处理模型
    public static final String WEIXINARTICLE_PAGE_PROCESSORMODEL="weixinArticle";
	
	//批量Url采集处理模型
	public static final String BATCHURLS_PAGE_PROCESSORMODEL="batchUrls";
	
	//列表详情采集模型
	public static final String LISTANDDETAIL_PAGE_PROCESSORMODEL="listAndDetail";
	
	//主题检索采集模型
	public static final String SEARCH_PAGE_PROCESSORMODEL="searchModel";
	
	// 站内检索(基于网站主题词进行信息的检索)
	public static final String WEBSITE_SUBJECT_PROCESSORMODEL="websiteSubject";
	
	//网站发现采集模型
	public static final String SUBJECTDISCOVER_PROCESSORMODEL="SubjectDiscover";
	
	//ip地址测试采集模型
	public static final String IPTEST_PAGE_PROCESSORMODEL="ipTest";
	
	
	
	
	//json文件管道
	public static final String PIPELINE_JSONPIPELINE="JSONPipeline";
	
	//资源文件文件管道
	public static final String PIPELINE_IMGPIPELINE="ImgPipeline";
	
	//数据库管道
	public static final String PIPELINE_NEWSDBPIPELINE="NewsDBPipeline";
	
	//数据库+外文翻译处理管道
	public static final String PIPELINE_NEWSTRANSLATE2DBPIPELINE="NewsTranslate2DBPipeline";
	
	//html原文文件管道
	public static final String PIPELINE_Html2PdfPipeline="Html2PdfPipeline";
	
	//mongo原文文件管道
	public static final String PIPELINE_MongoDBPipeline="MongoDBPipeline";
		
	//mongo原文带翻译文件管道
	public static final String PIPELINE_MongoTranslateDBPipeline="MongoTranslateDBPipeline";
	
	//ftp 文件管道
	public static final String PIPELINE_FTPFilePipeline="FTPFilePipeline";
	
	/***************************************页面对象定义*******************************************************/
	
    //代理类型—没有代理
    public static final String PROXY_TYPE_NO="no";
    
	//代理类型—大蚂蚁代理
    public static final String PROXY_TYPE_MAYI="damayiProxy";
    
    //代理类型—本机代理
    public static final String PROXY_TYPE_LOCAL="localProxy";
	
	//downloader组件--通用http下载
    public static final String DOWNLOADER_HTTP="CrawlerHttpClientDownloader";
	
    //downloader组件--动态js执行下载
    public static final String DOWNLOADER_PHANTOMJS="PhantomJsMockDonwnloader";
	
	/**
	 * 请求类型
	 */
	public static final String REQUEST_TYPE = "requestType";
	
	/**
	 * 首页
	 */
	public static final String REQUEST_INDEX = "index";
	
	/**
	 * 详情
	 */
	public static final String REQUEST_DETAIL = "detail";
	
	/**
	 * 搜索请求
	 */
	public static final String REQUEST_SEARCH = "search";
	
	/**
	 * 搜索请求
	 */
	public static final String REQUEST_DETAIL_LIST = "detailList";
	
	/**
	 * 分页PAGE请求
	 */
	public static final String REQUEST_LIST_PAGING = "listPaging";
	
	/**
	 * 状态码
	 */
    public static final String STATUS_CODE = "statusCode";
    /**
     * 代理
     */
    public static final String PROXY = "proxy"; 
    
    /***************************************页面对象定义*******************************************************/
    
    /**
     * 页面对象中的资源标签，通过该标签可获取资源内容
     */
    public static final String PAGE_RESOURCE_LABEL = "resources"; 
    
    /**
     * 页面对象中的对象标签，通过该标签可获取对象内容
     */
    public static final String PAGE_OBJECT_LABEL = "objectNames"; 
    

    /***************************************列抽取类型*******************************************************/
    /**
     * 列抽取类型-新闻类型，可以支持垃圾广告过滤
     */
    public static final String FIELD_DATATYPE_HTML = "Html";
    /**
     * 列抽取类型-文件类型，支持后续文件下载
     */
    public static final String FIELD_DATATYPE_FILE = "File";
	/**
	 * 列抽取类型-日期类型，需要对日期进行格式化
	 */
    public static final String FIELD_DATATYPE_DATE = "Date";
	
	/**
	 * 列抽取类型-字符串类型，通用类型
	 */
    public static final String FIELD_DATATYPE_STRING = "String";
    
    

}
