package com.project.gmall.manager;

import org.csource.fastdfs.ClientGlobal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManagerWebApplicationTests {

	@Test
	public void contextLoads() {
		// 1.连接trackerServer
		// 2.通过trackerServer获取可用的storageServer地址
		// 3.向storageServer上传文件
		// 4.storageServer返回存储地址给客户端
		// 5.客户端将返回的存储地址保存到数据库
	}

}
