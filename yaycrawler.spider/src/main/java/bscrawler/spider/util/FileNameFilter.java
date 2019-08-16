package bscrawler.spider.util;

import java.io.File;
import java.io.FilenameFilter;

public class FileNameFilter implements FilenameFilter{

	private String filterDetail;
	
	public FileNameFilter() {
		
	}
	
	public FileNameFilter(String filterDetail) {
		this.filterDetail = filterDetail;
	}
	
	@Override
	public boolean accept(File dir, String name) {
		return name.startsWith(filterDetail) && !name.substring(0, name.lastIndexOf(".")).equals(filterDetail);
	}
}
