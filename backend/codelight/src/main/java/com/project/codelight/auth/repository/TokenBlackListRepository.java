package com.project.codelight.auth.repository;

import com.project.codelight.auth.domain.TokenBlackList;
import org.springframework.data.repository.CrudRepository;

public interface TokenBlackListRepository extends CrudRepository<TokenBlackList, String> {

}
