
package com.bus;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BusMonitorApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(
                BusMonitorApplication.class);
        builder.headless(false).run(args);// 不加这句话会报java.awt.HeadlessException:
    }
}
