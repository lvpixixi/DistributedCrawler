package yaycrawler.dao.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import yaycrawler.dao.domain.PageInfo;
import yaycrawler.dao.domain.RuleVersion;

/**
 * Created by ucs_yuananyun on 2016/8/30.
 */
@Repository
public interface RuleVersionRepository extends CrudRepository<RuleVersion, String> {

    @Query(value = "select max(version)  from conf_rule_version  ", nativeQuery = true)
    int getMaxVersionNo();

}

