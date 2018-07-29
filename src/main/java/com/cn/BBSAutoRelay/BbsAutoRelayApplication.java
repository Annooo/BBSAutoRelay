package com.cn.BBSAutoRelay;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cn.BBSAutoRelay.mapper")
public class BbsAutoRelayApplication {

	public static void main(String[] args) {
		SpringApplication.run(BbsAutoRelayApplication.class, args);
	}
}
