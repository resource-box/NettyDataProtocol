package com.hooniegit.NettyDataProtocol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NettyDataProtocolApplication {

	public static void main(String[] args) {
		SpringApplication.run(NettyDataProtocolApplication.class, args);
	}

}
