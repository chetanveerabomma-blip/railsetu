package com.railsetu.repository;

import com.railsetu.domain.Role;
import com.railsetu.domain.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
