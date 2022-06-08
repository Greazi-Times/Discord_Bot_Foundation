package com.greazi.discordbotfoundation.commands;

import com.greazi.discordbotfoundation.commands.annotation.JDACommand;
import java.util.List;

/**
 * A "compiler" for {@link java.lang.Object Object}s that uses {@link java.lang.annotation.Annotation Annotation}s
 * as helpers for creating {@link SimpleCommand Command}s.
 *
 * <p>Previous to version 1.6 all Commands required the Command abstract class to be extended in source.
 * The primary issue that came with this was that Commands were restricted to that method of creation, offering
 * no support for popular means such as annotated commands.
 *
 * <p>Since 1.6 the introduction of {@link com.greazi.discordbotfoundation.commands.CommandBuilder CommandBuilder}
 * has allowed the potential to create unique {@link SimpleCommand Command}
 * objects after compilation.
 * <br>The primary duty of this class is to provide a "in runtime" converter for generics that are annotated with
 * the {@link JDACommand.Module JDACommand.Module}
 *
 */
public interface AnnotatedModuleCompiler
{
    /**
     * Compiles one or more {@link SimpleCommand Command}s
     * using method annotations as for properties from the specified {@link java.lang.Object
     * Object}.
     *
     * <p><b>This Object must be annotated with {@link
     * com.greazi.discordbotfoundation.commands.annotation.JDACommand.Module @JDACommand.Module}!</b>
     *
     * @param  o
     *         The Object, annotated with {@code @JDACommand.Module}.
     *
     * @return A {@link java.util.List} of Commands generated from the provided Object
     */
    List<SimpleCommand> compile(Object o);
}
