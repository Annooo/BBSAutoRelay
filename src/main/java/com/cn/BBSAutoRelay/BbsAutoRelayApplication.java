package com.cn.BBSAutoRelay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.cn.BBSAutoRelay.mapper")//不能扫描本地通用Mapper接口包
public class BbsAutoRelayApplication {

	public static void main(String[] args) {
		SpringApplication.run(BbsAutoRelayApplication.class, args);
	}
}
