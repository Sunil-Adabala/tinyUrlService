package com.hldprojects.tinyurlservice;

import com.hldprojects.tinyurlservice.keymanager.dao.KeyDao;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class TinyurlserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TinyurlserviceApplication.class, args);
		System.out.println("started");
	}

}
