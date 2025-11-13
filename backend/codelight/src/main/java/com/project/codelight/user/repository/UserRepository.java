package com.project.codelight.user.repository;

import com.project.codelight.user.domain.LoginType;
import com.project.codelight.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = :email and u.loginType = :loginType")
    Optional<User> findUserIncludingDeletedByEmailAndLoginType(@Param("email") String email,
                                                               @Param("loginType") LoginType loginType);

    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE User u SET u.deleted = false WHERE u.email = :email AND u.loginType = :loginType")
    void restoreUserByEmailAndLoginType(@Param("email") String email,
                                        @Param("loginType") LoginType loginType);
}
