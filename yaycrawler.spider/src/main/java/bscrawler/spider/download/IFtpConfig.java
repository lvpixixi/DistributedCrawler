package bscrawler.spider.download;

public interface IFtpConfig {
	public String getFtpUrl();
	public int getPort();
	public String getUsername();
	public String getPassword();
	public String getFtpPath();
}
