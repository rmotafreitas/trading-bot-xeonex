package xeonex.xeonex;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import xeonex.xeonex.service.IndicatorService;



@SpringBootApplication
@EnableScheduling

public class XeonexApplication {


	public static void main(String[] args) {

		SpringApplication.run(XeonexApplication.class, args);
	}

}
