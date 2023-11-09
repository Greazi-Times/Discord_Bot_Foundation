/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.old.utils.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * A program element annotated {@code @Disabled} is one that programmers
 * are discouraged from using. An element may be disabled for any of several
 * reasons, for example, its usage is likely to lead to errors; it may
 * be changed incompatibly or removed in a future version; it has been
 * superseded by a newer, usually preferable alternative; or it is obsolete.
 *
 * <p>Compilers issue warnings when a disabled program element is used or
 * overridden in non-disabled code. Use of the {@code @Disabled}
 * annotation on a local variable declaration or on a parameter declaration
 * or a package declaration has no effect on the warnings issued by a compiler.
 *
 * <p>When a module is disabled, the use of that module in {@code
 * requires}, but not in {@code exports} or {@code opens} clauses causes
 * a warning to be issued. A module being disabled does <em>not</em> cause
 * warnings to be issued for uses of types within the module.
 *
 * <p>This annotation type has a string-valued element {@code since}. The value
 * of this element indicates the version in which the annotated program element
 * was first disabled.
 *
 * <p>This annotation type has a boolean-valued element {@code forRemoval}.
 * A value of {@code true} indicates intent to remove the annotated program
 * element in a future version. A value of {@code false} indicates that use of
 * the annotated program element is discouraged, but at the time the program
 * element was annotated, there was no specific intent to remove it.
 *
 * @author Neal Gafter
 * @apiNote It is strongly recommended that the reason for deprecating a program element
 * be explained in the documentation, using the {@code @Disabled}
 * javadoc tag. The documentation should also suggest and link to a
 * recommended replacement API, if applicable. A replacement API often
 * has subtly different semantics, so such issues should be discussed as
 * well.
 *
 * <p>It is recommended that a {@code since} value be provided with all newly
 * annotated program elements. Note that {@code since} cannot be mandatory,
 * as there are many existing annotations that lack this element value.
 *
 * <p>There is no defined order among annotation elements. As a matter of
 * style, the {@code since} element should be placed first.
 *
 * <p>The {@code @Disabled} annotation should always be present if
 * the {@code @Disabled} javadoc tag is present, and vice-versa.
 * @jls 9.6.4.6 @Disabled
 * @since 1.5
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {CONSTRUCTOR, FIELD, LOCAL_VARIABLE, METHOD, PACKAGE, MODULE, PARAMETER, TYPE})
public @interface Disabled {
    /**
     * Returns the version in which the annotated element became disabled.
     * The version string is in the same format and namespace as the value of
     * the {@code @since} javadoc tag. The default value is the empty
     * string.
     *
     * @return the version string
     * @since 9
     */
    String since() default "";

    /**
     * Indicates whether the annotated element is subject to removal in a
     * future version. The default value is {@code false}.
     *
     * @return whether the element is subject to removal
     * @since 9
     */
    boolean forRemoval() default false;
}