package com.edso.resume.account.repo;

import com.edso.resume.account.entities.SessionEntity;
import com.edso.resume.account.entities.UserSessionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
public class SessionRepository {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SessionEntityRepo cachedUserInfoRepo;
    private final UserSessionEntityRepo userSessionEntityRepo;

    public SessionRepository(SessionEntityRepo cachedUserInfoRepo, UserSessionEntityRepo userSessionEntityRepo) {
        this.cachedUserInfoRepo = cachedUserInfoRepo;
        this.userSessionEntityRepo = userSessionEntityRepo;
    }

    public synchronized void addSession(SessionEntity sessionEntity) {
        cachedUserInfoRepo.save(sessionEntity);
        UserSessionEntity userSessionEntity = userSessionEntityRepo.findById(sessionEntity.getUsername()).orElse(null);
        if (userSessionEntity != null) {
            userSessionEntity.getTokens().add(sessionEntity.getToken());
            userSessionEntityRepo.save(userSessionEntity);
        } else {
            userSessionEntity = new UserSessionEntity();
            userSessionEntity.setUsername(sessionEntity.getUsername());
            Set<String> tokens = new HashSet<>();
            tokens.add(sessionEntity.getToken());
            userSessionEntity.setTokens(tokens);
            userSessionEntityRepo.save(userSessionEntity);
        }
    }

    public synchronized void deleteByUserId(String username) {
        int count = 0;
        UserSessionEntity sessionEntity = userSessionEntityRepo.findById(username).orElse(null);
        if (sessionEntity != null) {
            Set<String> tokens = sessionEntity.getTokens();
            if (tokens != null) {
                for (String token : tokens) {
                    cachedUserInfoRepo.deleteById(token);
                    count++;
                }
            }
            userSessionEntityRepo.deleteById(username);
        }
        logger.info("deleteByUserId: {}, count: {}", username, count);
    }

}
