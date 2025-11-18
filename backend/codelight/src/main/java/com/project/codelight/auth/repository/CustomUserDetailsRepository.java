package com.project.codelight.auth.repository;

import com.project.codelight.user.domain.LoginType;
import com.project.codelight.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomUserDetailsRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = :email and u.loginType = :loginType")
    Optional<User> findByEmailAndLoginType(@Param("email") String email,
                                           @Param("loginType") LoginType loginType);

}
