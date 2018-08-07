package com.cn.BBSAutoRelay;

import com.cn.BBSAutoRelay.Configuration.WebDriverConfig;
import com.cn.BBSAutoRelay.common.BBSAction;
import com.cn.BBSAutoRelay.httpClient.AbstractHttpClient;
import com.cn.BBSAutoRelay.httpClient.HttpResult;
import com.cn.BBSAutoRelay.model.Account;
import com.cn.BBSAutoRelay.sms.YmAPI;
import com.cn.BBSAutoRelay.zhihu.ZhihuAction;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
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

	@Autowired
	private AbstractHttpClient httpClient;

	@Autowired
	private WebDriverConfig webDriverConfig;

	@Test
	public void contextLoads() throws Exception {

		WebDriver webDriver = webDriverConfig.getWebDriver();
//		WebDriver webDriver = new ChromeDriver();
//		BBSAction zhihuAction = new ZhihuAction(0 ,null);
		Account account = new Account();
        account.setId(1);
        account.setUserName("18444005278");
        account.setPassword("aaa18444005278");
        account.setCookies("{\"capsion_ticket\":\"2|1:0|10:1533546040|14:capsion_ticket|44:NmM3NGUxYjc5NmIwNDUxOTg3NjU4Nzc0YTA4YTAyMzY=|1b7dd0a5af82c0da01aada581d84c3c08143e6fbf8d4732e3cdf8d7f4ac95c35\",\"l_n_c\":\"1\",\"l_cap_id\":\"ZGYxZDg3Y2YxMjkyNDMyYWE0ZjY2OGJkZGY2ZGZkZjk=|1533546040|eb69ae6243dbf317abe5a7a8b369307f6b10cafe\",\"cap_id\":\"MGE1MmRkNjcyYzdhNDg5NGE0ZmRjMjRmZmE3MzI5N2I=|1533546040|1e5638c3eeba2c91c531af622f6a4f394e8c09f0\",\"n_c\":\"1\",\"r_cap_id\":\"MWQ3YzRmMDk0NGIyNDQwYjlkZjgxYTA2YjNiZDY3YWU=|1533546040|2a6f3d2eab2118e9dad841635fae5b5c8db15544\",\"q_c1\":\"d17fd03d98e34a2689ac1158d3b0d9a9|1533546040000|1533546040000\",\"tgw_l7_route\":\"27a99ac9a31c20b25b182fd9e44378b8\",\"z_c0\":\"2|1:0|10:1533546043|4:z_c0|80:MS4xTFBaWEN3QUFBQUFtQUFBQVlBSlZUVHRjVlZ6UF9kLTNUUU43VnJCb05lQUQ3QjhXUEVLS1hRPT0=|114480f323987620de4e9fbbf4faeb2cacc4a35ea468866c17f04d62f1169418\",\"_xsrf\":\"qeWihiXYovFzTL68umMDIzM3QyXvWpI4\"}");
//        zhihuAction.login(null,account);
//		//action.reply(null);
		zhihuAction.register(webDriver);
//        //account.setCookies("capsion_ticket=\"2|1:0|10:1532934945|14:capsion_ticket|44:NGM5MzM4OTAyMWZkNGQ3Mzg2ODM0YWMxZWQ4YTNmN2U=|1f2aa5f7cc428032ca10678d1773f308d0f0bc694caaf9b5cff8a1f6eb95175c\"; expires=星期三, 29 八月 2018 03:15:44 CST; path=/; domain=.zhihu.com, d_c0=\"ACAn7GaW-g2PTiaZ2lQ5DPz1eZDggRr6d2U=|1532934942\"; expires=星期四, 29 七月 2021 03:15:42 CST; path=/; domain=.zhihu.com, _zap=99ad5348-272a-4471-850e-5fc63d5eaf84; expires=星期三, 29 七月 2020 03:15:42 CST; path=/; domain=.zhihu.com, tgw_l7_route=1c2b7f9548c57cd7d5a535ac4812e20e; expires=星期一, 30 七月 2018 03:30:42 CST; path=/; domain=www.zhihu.com, q_c1=f0ecdd5742f749aaa141684261d43b32|1532934942000|1532934942000; expires=星期四, 29 七月 2021 03:15:42 CST; path=/; domain=.zhihu.com, _xsrf=8c46300d-d527-4b7d-973c-b8713c89d097; path=/; domain=.zhihu.com");
//        zhihuAction.replyRecord(webDriver, account);
//		zhihuAction.posted(webDriver, account);

//		zhihuAction.postedRecord(webDriver, account);

		webDriverConfig.closeAll();

	}

	@Test
	public void test(){
		HttpResult httpResult = httpClient.doGet("http://www.baidu.com",null,null);
		System.out.println(httpResult.getContent());
	}

}
