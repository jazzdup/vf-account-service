package com.vodafone.charging.accountservice.exception;


/**
 * Should be thrown when a repository resource is not found given a criteria
 */
public class RepositoryResourceNotFoundException extends RuntimeException {

    public RepositoryResourceNotFoundException(String message, Throwable thr) {
        super(message, thr);
    }
    public RepositoryResourceNotFoundException(String message) {
        super(message);
    }
}
