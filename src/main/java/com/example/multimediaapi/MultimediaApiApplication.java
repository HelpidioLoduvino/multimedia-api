package com.example.multimediaapi;

import com.example.multimediaapi.model.Member;
import com.example.multimediaapi.model.Group;
import com.example.multimediaapi.model.User;
import com.example.multimediaapi.repository.GroupRepository;
import com.example.multimediaapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@AllArgsConstructor
public class MultimediaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultimediaApiApplication.class, args);
	}


	@Bean
	CommandLineRunner initDatabase(UserRepository userRepository, GroupRepository groupRepository) {
		return args -> {
			User adminUser = userRepository.findByUserRole("ADMIN");

			if (adminUser == null) {
				adminUser = new User();
				adminUser.setName("Helpidio");
				adminUser.setSurname("Mateus");
				adminUser.setEmail("helpidio@gmail.com");

				BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
				String encodedPassword = passwordEncoder.encode("12345");

				adminUser.setPassword(encodedPassword);
				adminUser.setConfirmPassword(encodedPassword);
				adminUser.setUserRole("ADMIN");
				userRepository.save(adminUser);
			}

			boolean groupExists = groupRepository.existsByName("Público");

			if (!groupExists) {
				Group firstMyGroup = new Group();
				Member member = new Member(true, true, adminUser);
				firstMyGroup.setName("Público");
				firstMyGroup.setStatus("Público");
				firstMyGroup.getMembers().add(member);
				groupRepository.save(firstMyGroup);
			}
		};
	}
}
