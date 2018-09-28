package com.dvipersquad.editableprofile.di;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * As unscoped components cant depend on scoped components and fragments are injected
 * we will apply this scope to all fragments
 */
@Documented
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ActivityScoped {
}
