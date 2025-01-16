package site.xiaofei.apibackend;


import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@MapperScan("site.xiaofei.apibackend.mapper")
@EnableDubbo(scanBasePackages = "site.xiaofei.apibackend.service.impl.dubbo")
@SpringBootApplication(scanBasePackages = {"site.xiaofei.apibackend", "site.xiaofei.apicommon.common"})
public class ApiBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiBackendApplication.class, args);
		log.info("api-backend启动成功...");
	}

}
