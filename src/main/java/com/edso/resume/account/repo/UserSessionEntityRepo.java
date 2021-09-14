package com.edso.resume.account.repo;

import com.edso.resume.account.entities.UserSessionEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserSessionEntityRepo extends CrudRepository<UserSessionEntity, String> {
}
