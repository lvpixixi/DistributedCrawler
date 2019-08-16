package io.renren.modules.spider.service.impl;

import static com.hankcs.hanlp.utility.Predefine.logger;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.hankcs.hanlp.classification.classifiers.LinearSVMClassifier;
import com.hankcs.hanlp.classification.models.LinearSVMModel;

import io.renren.modules.spider.entity.KlVo;
import io.renren.modules.spider.service.IAutoClassification;

@Service("AutoClassitionImpl")
public class AutoClassitionImpl implements IAutoClassification{
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Value("${classify.modelpath}")
	private String modelPath;
	
	private LinearSVMClassifier classifier = null;
	
	public List<KlVo> queryObjectsByTime(String entity,Date fromdate,Date todate) {
		Query query = new Query();
		if(fromdate!=null&&todate!=null)
		query.addCriteria(Criteria.where("pubDate").gte(fromdate).lte(todate));
		return mongoTemplate.find(query, KlVo.class, entity);
	}
	
	private void updateEntitys(String entity,String id,String classId,String className) {
    	Query query = new Query(Criteria.where("_id").in(id));
    	Update update = Update.update("class_id", classId);
    	update.set("class_name", className);
    	mongoTemplate.updateMulti(query, update, entity);
	}

	@Override
	public void executeClassification(String entity, Date fromdate, Date todate) {
		if(this.classifier==null) {
			this.classifier = new LinearSVMClassifier(readObjectFrom(modelPath));
		}
		List<KlVo> list = this.queryObjectsByTime(entity, fromdate, todate);
		for(KlVo vo:list) {
			String className = classifier.classify(vo.getContent());
			String classId = className;
			this.updateEntitys(entity,vo.getId(), classId,className);
		}
		
	}
	
	 /**
     * 反序列化对象
     *
     * @param path
     * @return
     */
    public LinearSVMModel readObjectFrom(String path)
    {
    	ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource(path);
        ObjectInputStream ois = null;
        try
        {
            ois = new ObjectInputStream(new FileInputStream(url.getFile()));
            Object o = ois.readObject();
            ois.close();
            return (LinearSVMModel)o;
        }
        catch (Exception e)
        {
            logger.warning("在从" + path + "读取对象时发生异常" + e);
        }

        return null;
    }

}
