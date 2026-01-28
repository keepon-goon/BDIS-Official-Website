package com.bdis.bdis_website;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// 扫描mapper包下所有接口，确保LabInfoMapper被包含
@MapperScan("com.bdis.bdis_website.mapper")
public class BdisWebsiteApplication {
	public static void main(String[] args) {
		SpringApplication.run(BdisWebsiteApplication.class, args);
	}
}


