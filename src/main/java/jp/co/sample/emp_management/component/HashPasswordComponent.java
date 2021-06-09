package jp.co.sample.emp_management.component;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class HashPasswordComponent {
	
	final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
	
	public String hashPassword(String password) {
		return PASSWORD_ENCODER.encode(password);
	}
	
	public boolean comparePassword(String rawPassword, String hashedPassword) {
		return PASSWORD_ENCODER.matches(rawPassword, hashedPassword);
	}
}
