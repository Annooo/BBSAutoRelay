package com.cn.BBSAutoRelay;

import com.cn.BBSAutoRelay.common.BBSAction;
import com.cn.BBSAutoRelay.zhihu.ZhihuAction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BbsAutoRelayApplicationTests {

	@Test
	public void contextLoads() {

		BBSAction action = new ZhihuAction(0 ,null);
		WebDriver webDriver = new ChromeDriver();
		action.login(webDriver);

		webDriver.close();
		webDriver.quit();
	}

}
