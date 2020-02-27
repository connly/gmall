package com.project.gmall.manager;

import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManagerWebApplicationTests {

	@Test
	public void contextLoads() throws Exception {
		// 1.连接trackerServer
		String conf_filename = GmallManagerWebApplicationTests.class.getClassLoader().getResource("tracker.conf").getPath();
		ClientGlobal.init(conf_filename);

		TrackerServer trackerServer = new TrackerClient().getTrackerServer();

		// 2.通过trackerServer获取可用的storageServer地址
		StorageClient storageClient = new StorageClient(trackerServer, null);

		// 3.向storageServer上传文件(ext_name:后缀名)
		String[] gifs = storageClient.upload_file("e:/11.gif", "gif", null);

		// 4.storageServer返回存储地址给客户端
		for (String gif : gifs) {
			System.out.println(gif);
		}
		// 5.客户端将返回的存储地址保存到数据库
	}

}
