package com.example.multimediaapi;

import com.example.multimediaapi.model.ShareGroup;
import com.example.multimediaapi.model.User;
import com.example.multimediaapi.model.UserGroup;
import com.example.multimediaapi.repository.GroupRepository;
import com.example.multimediaapi.repository.UserGroupRepository;
import com.example.multimediaapi.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
public class MultimediaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultimediaApiApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(UserRepository userRepository, GroupRepository groupRepository, UserGroupRepository userGroupRepository) {
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
				ShareGroup firstShareGroup = new ShareGroup();
				firstShareGroup.setGroupName("Público");
				firstShareGroup.setGroupStatus("Público");
				ShareGroup shareGroup = groupRepository.save(firstShareGroup);
				UserGroup firstUserGroup = new UserGroup(null, adminUser, shareGroup);
				userGroupRepository.save(firstUserGroup);
			}
		};
	}
}
