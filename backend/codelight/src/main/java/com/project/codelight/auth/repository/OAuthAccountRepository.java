package com.project.codelight.auth.repository;

import com.project.codelight.auth.domain.OAuthAccount;
import com.project.codelight.auth.domain.OAuthProvider;
import com.project.codelight.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, Long> {

    @Query("SELECT oa FROM OAuthAccount oa WHERE oa.provider = :provider AND oa.providerUserId = :providerUserId")
    Optional<OAuthAccount> findByProviderAndProviderUserId(
        @Param("provider") OAuthProvider provider,
        @Param("providerUserId") String providerUserId
    );

    @Query("SELECT oa FROM OAuthAccount oa WHERE oa.user = :user")
    List<OAuthAccount> findByUser(@Param("user") User user);

    @Query("SELECT oa FROM OAuthAccount oa WHERE oa.user = :user AND oa.provider = :provider")
    Optional<OAuthAccount> findByUserAndProvider(
        @Param("user") User user,
        @Param("provider") OAuthProvider provider
    );

    @Query("SELECT CASE WHEN COUNT(oa) > 0 THEN true ELSE false END FROM OAuthAccount oa WHERE oa.provider = :provider AND oa.providerUserId = :providerUserId")
    boolean existsByProviderAndProviderUserId(
        @Param("provider") OAuthProvider provider,
        @Param("providerUserId") String providerUserId
    );
}
