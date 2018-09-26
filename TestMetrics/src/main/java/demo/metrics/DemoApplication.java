package demo.metrics;

import com.codahale.metrics.ConsoleReporter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class DemoApplication {
	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(DemoApplication.class, args);
		// Æô¶¯Reporter
		ConsoleReporter reporter = ctx.getBean(ConsoleReporter.class);
		reporter.start(2, TimeUnit.SECONDS);
	}
}
