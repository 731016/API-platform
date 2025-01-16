package site.xiaofei.apiinterface;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@Slf4j
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class},
		scanBasePackages = {"site.xiaofei.apiinterface", "site.xiaofei.apicommon.common.exception"}
)
public class ApiInterfaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiInterfaceApplication.class, args);
		log.info("api-interface启动成功...");
	}

}
