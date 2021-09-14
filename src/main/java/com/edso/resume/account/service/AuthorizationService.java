package com.edso.resume.account.service;

public interface AuthorizationService {
    boolean enforce(String subject, String tenant, String object, String action);
}
