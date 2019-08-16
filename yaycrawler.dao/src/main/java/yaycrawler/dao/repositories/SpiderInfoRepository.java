package yaycrawler.dao.repositories;

import org.springframework.data.repository.CrudRepository;

import yaycrawler.dao.domain.SpiderInfo;

public interface SpiderInfoRepository extends CrudRepository<SpiderInfo, String> {

}
