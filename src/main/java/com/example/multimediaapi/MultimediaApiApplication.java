package com.example.multimediaapi;

import com.example.multimediaapi.model.Member;
import com.example.multimediaapi.model.MyGroup;
import com.example.multimediaapi.model.User;
import com.example.multimediaapi.repository.GroupRepository;
import com.example.multimediaapi.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
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
				adminUser.setPassword("12345");
				adminUser.setConfirmPassword("12345");
				adminUser.setUserRole("ADMIN");
				userRepository.save(adminUser);
			}

			boolean groupExists = groupRepository.existsByGroupName("Público");

			if (!groupExists) {
				MyGroup firstMyGroup = new MyGroup();
				Member member = new Member(true, true, adminUser);
				firstMyGroup.setGroupName("Público");
				firstMyGroup.setGroupStatus("Público");
				firstMyGroup.getMembers().add(member);
				groupRepository.save(firstMyGroup);
			}
		};
	}
}
