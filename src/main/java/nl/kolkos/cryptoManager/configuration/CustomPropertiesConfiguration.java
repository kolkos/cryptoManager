package nl.kolkos.cryptoManager.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@ComponentScan(basePackages = { "nl.kolkos.cryptoManager.*" })
@PropertySource("classpath:application.yml")
public class CustomPropertiesConfiguration {
	@Autowired
	private Environment env;
	
	public void config() {
        env.getProperty("custom.mail.send.from");
    }
	
//	@Bean
//    public JavaMailSenderImpl mailSender() {
//        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
//
//        javaMailSender.setProtocol("SMTP");
//        javaMailSender.setHost("127.0.0.1");
//        javaMailSender.setPort(25);
//
//        return javaMailSender;
//    }
}
