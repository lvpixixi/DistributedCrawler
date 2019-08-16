package yaycrawler.spider;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import bscrawler.spider.db.utils.DbBuilder;

public class SiteIni {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Connection conn = DbBuilder.getConnection();
		DbBuilder.beginTransaction(conn);
		List<Map> objs = DbBuilder.getListBean(" select * from spider_site where domain is null ",Map.class);
		for(Map map :objs) {
			System.out.println(map);
		}
		DbBuilder.commit(conn);
		DbBuilder.close(conn);

	}

}
