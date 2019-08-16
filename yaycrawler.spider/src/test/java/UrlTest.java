import java.net.MalformedURLException;
import java.net.URL;

public class UrlTest {

	public static void main(String[] args) throws MalformedURLException {
		// TODO Auto-generated method stub
		URL baseUri =new URL("http://www.enet.com.cn/enews/inforcenter/abc/designmore.jsp");
		URL absoluteUri =new URL(baseUri,"http://www.enet.com.cn/enews/inforcenter/res/share.jpg");
		System.out.println(absoluteUri.toString());
		
	}

}
