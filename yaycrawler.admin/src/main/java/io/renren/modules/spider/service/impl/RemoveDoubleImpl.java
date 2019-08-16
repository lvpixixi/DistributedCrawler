package io.renren.modules.spider.service.impl;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import io.renren.common.utils.DateUtils;
import io.renren.modules.spider.entity.KlVo;
import io.renren.modules.spider.service.IRemoveDouble;
import yaycrawler.common.utils.MySimHash;

@Service("RemoveDoubleImpl")
public class RemoveDoubleImpl implements IRemoveDouble{
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public List<KlVo> queryObjectsByTime(String entity,Date fromdate,Date todate) {
		Query query = new Query();
		if(fromdate!=null&&todate!=null) {
			query.addCriteria(Criteria.where("pubDate").gte(fromdate).lte(todate));
		}
		return mongoTemplate.find(query, KlVo.class, entity);
	}
	
	private void updateEntitys(String entity,Collection<String> ids, String doubleStatus) {
    	Query query = new Query(Criteria.where("_id").in(ids));
    	Update update = Update.update("status", doubleStatus);
    	update.set("double_key", ids.iterator().next());
    	mongoTemplate.updateMulti(query, update, entity);
	}
	

	@Override
	public int executeCompute(String entity, Date fromdate, Date todate,int distance) {
		List<KlVo> list = this.queryObjectsByTime(entity, fromdate, todate);
		Set<String> ids;
 		Set<String> doubleIds = new HashSet<>();
 		for(int i=0;i<list.size()-1;i++) {
			KlVo vo_i = list.get(i);
			ids = new HashSet<String>();
			for(int j=i+1;j<list.size();j++) {
				KlVo vo_j = list.get(j);
				if(!doubleIds.contains(vo_j.getId())) {
					int dis = MySimHash.hammingDistance(new BigInteger(vo_i.getSimhash()), new BigInteger(vo_j.getSimhash()));
					if(dis<distance) {
						doubleIds.add(vo_i.getId());
						doubleIds.add(vo_j.getId());
						ids.add(vo_i.getId());
						ids.add(vo_j.getId());
					}
				}
			}
			if(ids.size()>1) {
				//设置疑似重复状态
				this.updateEntitys(entity, ids, this.STATUS_DOUBLE);
			}
		}
 		return doubleIds.size();
	}

	
	private Query getQuery(String entity,String keyWord,Map<String, Object> params) {
		Query query = new Query();
		if(!StringUtils.isEmpty(keyWord)) {
			Pattern pattern = Pattern.compile("^.*"+keyWord+".*$", Pattern.CASE_INSENSITIVE);
			query.addCriteria(Criteria.where("title").regex(pattern));
		}
		
		// 是否需要判定信息的审核状态
		if(params.containsKey("status")) {
			query.addCriteria(Criteria.where("status").is(MapUtils.getString(params, "status")));
		}
		
		
		if(params.containsKey("begindate")&&params.containsKey("enddate")) {
			String begindateStr = MapUtils.getString(params, "begindate");
			String enddateStr = MapUtils.getString(params, "enddate");
			Date begindate = StringUtils.isEmpty(begindateStr)?null:DateUtils.strToDate(begindateStr, DateUtils.simpleDateFormat_yyyy_MM_dd);
			Date enddate = StringUtils.isEmpty(enddateStr)?null:DateUtils.strToDate(enddateStr, DateUtils.simpleDateFormat_yyyy_MM_dd);
			if(begindate!=null&&enddate!=null) {
				query.addCriteria(Criteria.where("pubDate").gte(DateUtils.strToDate(begindateStr, DateUtils.simpleDateFormat_yyyy_MM_dd)).lte(DateUtils.strToDate(enddateStr, DateUtils.simpleDateFormat_yyyy_MM_dd)));
			}
		}
		
		//设置排序
		
		if(params.containsKey("desc")) {
			// 设置排序的规则 ---> 替换为按照发布时间倒叙排序
			query.with(new Sort(new Sort.Order(Sort.Direction.DESC, MapUtils.getString(params, "desc"))));
		}
		
		if(params.containsKey("asc")) {
			query.with(new Sort(new Sort.Order(Sort.Direction.DESC, MapUtils.getString(params, "asc"))));
		}
		
	    return query;
	}

	@Override
	public List<Map> searchDoubleList(String entity, String keyWord, Map<String, Object> params, int pageIndex,
			int pageSize) {
		Query query = this.getQuery(entity, keyWord, params);
		query.skip(pageIndex);// 从那条记录开始
		query.limit(pageSize);// 取多少条记录
		return mongoTemplate.find(query, Map.class, entity);
	}

	@Override
	public long searchDoubleCount(String entity, String keyWord, Map<String, Object> params) {
		Query query = this.getQuery(entity, keyWord, params);
		return mongoTemplate.count(query, entity);
	}

	@Override
	public void multiVerifyNODouble(String entity, List<String> ids) {
		this.updateEntitys(entity, ids, this.STATUS_INITIAL);
	}

}
