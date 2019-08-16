package yaycrawler.master;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import yaycrawler.master.listener.MasterRegisterListener;

@SpringBootApplication
@ImportResource(locations = {"classpath*:spring/*.xml"})
@EntityScan(basePackages = {"yaycrawler.dao.domain"})
@EnableJpaRepositories(basePackages = {"yaycrawler.dao.repositories"})
public class MasterApplication {

	public static void main(String[] args) {
		SpringApplication springApplication =new SpringApplication(MasterApplication.class);
		springApplication.addListeners(new MasterRegisterListener());
		springApplication.run(args);
	}


}
