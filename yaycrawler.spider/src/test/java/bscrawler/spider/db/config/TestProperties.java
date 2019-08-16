package bscrawler.spider.db.config;

public class TestProperties {
	public static void main(String[] args) {
		PropertiesConfig config = PropertiesService.getApplicationConfig();
		System.out.println(config.getProperty("jdbc.url"));
	}
}
