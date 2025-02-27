package com.ik.peakypicks;

import com.ik.peakypicks.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class PeakypicksApplication {
	private final UserRepository userRepository;

	public PeakypicksApplication(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	public static void main(String[] args) {
		SpringApplication.run(PeakypicksApplication.class, args);

	}
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

}
