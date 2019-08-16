package io.renren.modules.spider.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mss.crawler.custom.weapon.Weapon;

@Mapper
public interface WeaponMapper {
	// 获取数据库的所有的记录的 content
	List<Weapon> getAllContent();
	
	List<Weapon> getNames();
	
	void addSynonymTitle(Weapon weapon);
}
