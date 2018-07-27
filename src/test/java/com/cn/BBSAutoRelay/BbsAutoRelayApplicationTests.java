package com.cn.BBSAutoRelay;

import com.cn.BBSAutoRelay.common.BBSAction;
import com.cn.BBSAutoRelay.sms.YmAPI;
import com.cn.BBSAutoRelay.zhihu.ZhihuAction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BbsAutoRelayApplicationTests {

	@Autowired
	private ZhihuAction zhihuAction;

	@Test
	public void contextLoads() throws Exception {

		WebDriver webDriver = new ChromeDriver();
		//BBSAction action = new ZhihuAction(0 ,null);
		//action.login(null,"17620351934","chenning1");
		//action.reply(null);
		zhihuAction.register(webDriver);

		webDriver.quit();;
		webDriver.close();

	}

}
