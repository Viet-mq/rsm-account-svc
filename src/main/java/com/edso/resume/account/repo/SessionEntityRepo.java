package com.edso.resume.account.repo;

import com.edso.resume.account.entities.SessionEntity;
import org.springframework.data.repository.CrudRepository;

public interface SessionEntityRepo extends CrudRepository<SessionEntity, String> {
}
