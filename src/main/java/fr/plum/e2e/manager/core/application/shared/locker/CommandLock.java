package fr.plum.e2e.manager.core.application.shared.locker;

import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// @CommandLock is an annotation that is used to lock a command execution.
// Indeed sometimes we need to lock a command execution to avoid concurrency issues.
// By example a scheduler verification is run in same time as a webhook result.
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface CommandLock {}
