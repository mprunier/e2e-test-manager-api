package fr.njj.galaxion.endtoendtesting.domain.enumeration;

import fr.njj.galaxion.endtoendtesting.domain.exception.GitlabWebHookStatusNotExistException;

public enum GitlabJobStatus {
    created,
    pending,
    running,
    success,
    failed,
    canceled,
    skipped;

    public static GitlabJobStatus fromHeaderValue(String headerValue) {
        for (GitlabJobStatus status : values()) {
            if (status.name().equals(headerValue)) {
                return status;
            }
        }
        throw new GitlabWebHookStatusNotExistException(headerValue);
    }
}
