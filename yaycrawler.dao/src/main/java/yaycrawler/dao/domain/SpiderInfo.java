package yaycrawler.dao.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * 网页抽取模板
 *
 * @author Gao Shen
 * @version 16/4/12
 */
@Entity
@Table(name = "spider_info")
public class SpiderInfo implements Serializable{

	private static final long serialVersionUID = -2211329133190737811L;
	/**
     * 抓取模板id
     */
	@Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;
    /**
     * 网站名称
     */
	@Column(name = "site_name", columnDefinition = "varchar(200)")
    private String siteName;
    /**
     * 域名
     */
	@Column(name = "domain", columnDefinition = "varchar(200)")
    private String domain;
    /**
     * json配置
     */
	@Column(name = "json_data", columnDefinition = "longtext")
    private String jsonData;
    
    /**
	 * 项目信息
	 */
	@Column(name = "json_project", columnDefinition = "text")
    private String jsonProject;
    
	@Column(name = "create_time", columnDefinition = "datetime")
	private Date createTime;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getJsonData() {
		return jsonData;
	}
	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getJsonProject() {
		return jsonProject;
	}
	public void setJsonProject(String jsonProject) {
		this.jsonProject = jsonProject;
	}
}
