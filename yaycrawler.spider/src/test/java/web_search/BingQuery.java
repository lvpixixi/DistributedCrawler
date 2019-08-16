package web_search;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


public class BingQuery {
	
	public static void main(String[] args){
		try {
			query("xxx.txt", "北京 中国");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void query(String outputfile,String query) throws IOException {
		// TODO Auto-generated method stub
		BufferedWriter ps = new BufferedWriter(new PrintWriter(outputfile));
		ArrayList<String> queries = new ArrayList<>();
		String[] split = query.split(" ");
		for (String s: split)
			queries.add(s);
		ArrayList<String> a = BingSearcher.search(queries);
		for (String p : a){
			//System.out.println(p);
			ps.append(p);
			ps.newLine();
		}
		ps.close();
	}

}
