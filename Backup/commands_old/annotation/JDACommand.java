package com.greazi.discordbotfoundation.commands.annotation;

import com.greazi.discordbotfoundation.commands.SimpleCommand;
import net.dv8tion.jda.api.Permission;

import java.lang.annotation.*;

/**
 * An Annotation applicable to {@link java.lang.reflect.Method Method}s that will act as
 * {@link SimpleCommand Command}s when added to a Client
 * using {@link com.greazi.discordbotfoundation.commands.CommandClientBuilder#addAnnotatedModule(Object)
 * CommandClientBuilder#addAnnotatedModule()} serving as metadata "constructors" for what
 * would be a class extending Command of the same functionality and settings.
 *
 * <p>The primary issue that command systems face when trying to implement "annotated command"
 * systems is that reflection is a powerful but also costly tool and requires much more overhead
 * than most other types systems.
 *
 * To circumvent this, classes annotated with this are put through an {@link
 * com.greazi.discordbotfoundation.commands.AnnotatedModuleCompiler AnnotatedModuleCompiler}.
 * where they will be converted to Commands using {@link com.greazi.discordbotfoundation.commands.CommandBuilder
 * CommandBuilder}.
 *
 * <p>Classes that wish to be contain methods to be used as commands must be annotated with
 * {@link com.greazi.discordbotfoundation.commands.annotation.JDACommand.Module @Module}.
 * <br>Following that, any methods of said class annotated with this annotation (whose names
 * are also given as parameters of the {@code @Module} annotation) will be registered to the
 * module and "compiled" through the AnnotatedModuleCompiler provided in CommandClientBuilder.
 *
 * <pre><code>   {@link com.greazi.discordbotfoundation.commands.annotation.JDACommand.Module @JDACommand.Module}({@link com.greazi.discordbotfoundation.commands.annotation.JDACommand.Module#value() value} = "example")
 * public class AnnotatedModuleCmd {
 *
 *     {@literal @JDACommand(}
 *          {@link com.greazi.discordbotfoundation.commands.annotation.JDACommand#name() name} = {"example", "test", "demo"},
 *          {@link com.greazi.discordbotfoundation.commands.annotation.JDACommand#help() help} = "gives an example of what commands do"
 *      )
 *      public void example(CommandEvent) {
 *          event.reply("Hey look! This would be the bot's reply if this was a command!");
 *      }
 *
 * }</code></pre>
 *
 * @see    com.greazi.discordbotfoundation.commands.annotation.JDACommand.Module
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JDACommand
{
    /**
     * The name and aliases of the command.
     *
     * <p>The first index is the name, and following indices are aliases.
     *
     * @return An array of strings, the first one being the name
     * of the command, and following ones being aliases.
     */
    String[] name() default {"null"};

    /**
     * The help string for a command.
     *
     * @return The help string for a command.
     */
    String help() default "no help available";

    /**
     * Whether or not the command is only usable in a guild.
     * <br>Default {@code true}.
     *
     * @return {@code true} if the command can only be used in a guild,
     * {@code false} otherwise.
     */
    boolean guildOnly() default true;

    /**
     * The name of a role required to use this command.
     *
     * @return The name of a role required to use this command.
     */
    String requiredRole() default "";

    /**
     * Whether or not the command is owner only.
     * <br>Default {@code true}.
     *
     * @return {@code true} if the command is owner only, {@code false} otherwise.
     */
    boolean ownerCommand() default false;

    /**
     * The arguments string for the command.
     *
     * @return The arguments string for the command.
     */
    String arguments() default "";

    /**
     * The {@link JDACommand.Cooldown JDACommand.Cooldown} for the command.
     *
     * <p>This holds both metadata for both the
     * {@link SimpleCommand#cooldown Command#cooldown}
     * and {@link SimpleCommand#cooldownScope
     * Command#cooldownScope}.
     *
     * @return The {@code @Cooldown} for the command.
     */
    Cooldown cooldown() default @Cooldown(0);

    /**
     * The {@link net.dv8tion.jda.api.Permission Permissions} the bot must have
     * on a guild to use this command.
     *
     * @return The required permissions the bot must have to use this command.
     */
    Permission[] botPermissions() default {};

    /**
     * The {@link net.dv8tion.jda.api.Permission Permissions} the user must have
     * on a guild to use this command.
     *
     * @return The required permissions a user must have to use this command.
     */
    Permission[] userPermissions() default {};

    /**
     * Whether or not this command uses topic tags.
     * <br>Default {@code true}.
     *
     * <p>For more information on topic tags, see
     * {@link SimpleCommand#usesTopicTags
     * Command#usesTopicTags}
     *
     * @return {@code true} if this command uses topic tags, {@code false} otherwise.
     */
    boolean useTopicTags() default true;

    /**
     * The names of any methods representing child commands for this command.
     *
     * @return The names of any methods representing child commands.
     */
    String[] children() default {};

    /**
     * Whether or not this command should remain hidden in the help builder.
     *
     * @return {@code true} if this command should remain hidden, {@code false} otherwise.
     */
    boolean isHidden() default false;

    /**
     * The {@link JDACommand.Category JDACommand.Category} for this command.
     * <br>This holds data to properly locate a <b>static field</b> representing
     * this command's {@link SimpleCommand.Category
     * Category}.
     *
     * @return The {@code @Category} for this command.
     */
    Category category() default @Category(name = "null", location = Category.class);

    /**
     * A helper annotation to assist in location of methods that will generate
     * into {@link SimpleCommand Command}s.
     *
     * <p>Method names provided to this annotation must have one or two parameters.
     * Either a single parameter {@link com.greazi.discordbotfoundation.commands.CommandEvent
     * CommandEvent}, or a double parameter {@code CommandEvent} and {@code Command}.
     * <br>The arrangement of the double parameters is not important, so methods
     * may do it as {@code (CommandEvent, Command)} or {@code (Command, CommandEvent)}.
     *
     * @see    JDACommand
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Module
    {
        /**
         * The names of any methods that will be targeted when compiling this object
         * using the {@link com.greazi.discordbotfoundation.commands.AnnotatedModuleCompiler
         * AnnotatedModuleCompiler}.
         *
         * <p><b>This is not the same thing as the name of the commands!</b> These are
         * the names of the methods representing execution of the commands!
         *
         * @return An array of method names used when creating commands.
         */
        String[] value();
    }

    /**
     * A value wrapper for what would be {@link SimpleCommand#cooldown
     * Command#cooldown} and {@link SimpleCommand#cooldownScope
     * Command#cooldownScope}.
     *
     * The default {@link SimpleCommand.CooldownScope CooldownScope}
     * is {@link SimpleCommand.CooldownScope#USER CooldownScope.USER}.
     *
     * @see    JDACommand#cooldown()
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Cooldown
    {
        /**
         * The number of seconds the annotated Command will be on cooldown.
         *
         * @return The number of seconds the annotated Command will be on cooldown.
         */
        int value();

        /**
         * The {@link SimpleCommand.CooldownScope CooldownScope}
         * for the annotated Command.
         *
         * <p>By default this is {@link SimpleCommand.CooldownScope#USER
         * CooldownScope.USER}.
         *
         * @return The CooldownScope for this annotated Command.
         */
        SimpleCommand.CooldownScope scope() default SimpleCommand.CooldownScope.USER;
    }

    /**
     * A helper annotation to assist in location of Category instance.
     *
     * <p>This will target a <b>static field</b> in the specified class
     * {@link com.greazi.discordbotfoundation.commands.annotation.JDACommand.Category#location() location} using reflections, with a
     * matching {@link com.greazi.discordbotfoundation.commands.annotation.JDACommand.Category#name() name}.
     *
     * <p>It is important to remember the target must be a <b>static field</b>
     * and any other attempted inputs will result in errors from the
     * {@link com.greazi.discordbotfoundation.commands.AnnotatedModuleCompiler compiler}.
     *
     * @see    com.greazi.discordbotfoundation.commands.annotation.JDACommand#category()
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Category
    {
        /**
         * The name of the <b>static field</b> in the {@link com.greazi.discordbotfoundation.commands.annotation.JDACommand.Category#location()
         * target class} that will be the category for the annotated command.
         *
         * @return The name of the <b>static field</b> in the target class.
         */
        String name();

        /**
         * The target class where the <b>static field</b> is located.
         *
         * @return The target class where the <b>static field</b> is located.
         */
        Class<?> location();
    }

}
