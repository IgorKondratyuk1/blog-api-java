package org.development.blogApi.configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.development.blogApi.modules.user.entity.RoleEntity;
import org.development.blogApi.modules.user.repository.RoleRepository;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RoleInitializer {

    private final RoleRepository roleRepository;

    @PostConstruct
    public void initRoles() {
        createRoleIfNotExists("ADMIN");
        createRoleIfNotExists("USER");
    }

    private void createRoleIfNotExists(String roleName) {
        if (this.roleRepository.findRoleEntityByName(roleName).isEmpty()) {
            RoleEntity role = new RoleEntity();
            role.setName(roleName);
            roleRepository.save(role);
            System.out.println("Role " + roleName + " created.");
        } else {
            System.out.println("Role " + roleName + " already exists.");
        }
    }

}
