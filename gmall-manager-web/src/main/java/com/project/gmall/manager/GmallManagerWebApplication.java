package com.project.gmall.manager;

import com.project.gmall.bean.PmsBaseCatalog1;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
public class GmallManagerWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallManagerWebApplication.class, args);
	}

}
