package com.airquality;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 空气质量分析平台启动类
 */
@SpringBootApplication
@MapperScan("com.airquality.mapper")
public class AirQualityApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirQualityApplication.class, args);
        System.out.println("==========================================");
        System.out.println("   空气质量分析平台启动成功！");
        System.out.println("   访问地址: http://localhost:8080");
        System.out.println("==========================================");
    }
}
