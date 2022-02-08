package com.greazi.discordbotfoundation.commands.impl;

import com.greazi.discordbotfoundation.commands.AnnotatedModuleCompiler;
import com.greazi.discordbotfoundation.commands.SimpleCommand;
import com.greazi.discordbotfoundation.commands.CommandBuilder;
import com.greazi.discordbotfoundation.commands.CommandEvent;
import com.greazi.discordbotfoundation.commands.annotation.JDACommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Default implementation for {@link com.greazi.discordbotfoundation.commands.AnnotatedModuleCompiler
 * AnnotatedModuleCompiler}.
 *
 */
public class AnnotatedModuleCompilerImpl implements AnnotatedModuleCompiler
{
    private static final Logger LOG = LoggerFactory.getLogger(AnnotatedModuleCompiler.class);

    @Override
    public List<SimpleCommand> compile(Object o)
    {
        JDACommand.Module module = o.getClass().getAnnotation(JDACommand.Module.class);
        if(module == null)
            throw new IllegalArgumentException("Object provided is not annotated with JDACommand.Module!");
        if(module.value().length<1)
            throw new IllegalArgumentException("Object provided is annotated with an empty command module!");

        List<Method> commands = collect((Method method) -> {
            for(String name : module.value())
            {
                if(name.equalsIgnoreCase(method.getName()))
                    return true;
            }
            return false;
        }, o.getClass().getMethods());

        List<SimpleCommand> list = new ArrayList<>();
        commands.forEach(method -> {
            try {
                list.add(compileMethod(o, method));
            } catch(MalformedParametersException e) {
                LOG.error(e.getMessage());
            }
        });
        return list;
    }

    private SimpleCommand compileMethod(Object o, Method method) throws MalformedParametersException
    {
        JDACommand properties = method.getAnnotation(JDACommand.class);
        if(properties == null)
            throw new IllegalArgumentException("Method named "+method.getName()+" is not annotated with JDACommand!");
        CommandBuilder builder = new CommandBuilder();

        // Name
        String[] names = properties.name();
        builder.setName(names.length < 1 ? "null" : names[0]);

        // Aliases
        if(names.length>1)
            for(int i = 1; i<names.length; i++)
                builder.addAlias(names[i]);

        // Help
        builder.setHelp(properties.help());

        // Arguments
        builder.setArguments(properties.arguments().trim().isEmpty()? null : properties.arguments().trim());

        // Category
        if(!properties.category().location().equals(JDACommand.Category.class))
        {
            JDACommand.Category category = properties.category();
            for(Field field : category.location().getDeclaredFields())
            {
                if(Modifier.isStatic(field.getModifiers()) && field.getType().equals(SimpleCommand.Category.class))
                {
                    if(category.name().equalsIgnoreCase(field.getName()))
                    {
                        try {
                            builder.setCategory((SimpleCommand.Category) field.get(null));
                        } catch(IllegalAccessException e) {
                            LOG.error("Encountered Exception ", e);
                        }
                    }
                }
            }
        }

        // Guild Only
        builder.setGuildOnly(properties.guildOnly());

        // Required Role
        builder.setRequiredRole(properties.requiredRole().trim().isEmpty()? null : properties.requiredRole().trim());

        // Owner Command
        builder.setOwnerCommand(properties.ownerCommand());

        // Cooldown Delay
        builder.setCooldown(properties.cooldown().value());

        // Cooldown Scope
        builder.setCooldownScope(properties.cooldown().scope());

        // Bot Permissions
        builder.setBotPermissions(properties.botPermissions());

        // User Permissions
        builder.setUserPermissions(properties.userPermissions());

        // Uses Topic Tags
        builder.setUsesTopicTags(properties.useTopicTags());

        // Hidden
        builder.setHidden(properties.isHidden());

        // Child Commands
        if(properties.children().length>0)
        {
            collect((Method m) -> {
                for(String cName : properties.children())
                {
                    if(cName.equalsIgnoreCase(m.getName()))
                        return true;
                }
                return false;
            }, o.getClass().getMethods()).forEach(cm -> {
                try {
                    builder.addChild(compileMethod(o, cm));
                } catch(MalformedParametersException e) {
                    LOG.error("Encountered Exception ", e);
                }
            });
        }

        // Analyze parameter types as a final check.

        Class<?>[] parameters = method.getParameterTypes();
        // Dual Parameter Command, CommandEvent
        if(parameters[0] == SimpleCommand.class && parameters[1] == CommandEvent.class)
        {
            return builder.build((command, event) -> {
                try {
                    method.invoke(o, command, event);
                } catch(IllegalAccessException | InvocationTargetException e) {
                    LOG.error("Encountered Exception ", e);
                }
            });
        }
        else if(parameters[0] == CommandEvent.class)
        {
            // Single parameter CommandEvent
            if(parameters.length == 1)
            {
                return builder.build(event -> {
                    try {
                        method.invoke(o, event);
                    } catch(IllegalAccessException | InvocationTargetException e) {
                        LOG.error("Encountered Exception ", e);
                    }
                });
            }
            // Dual Parameter CommandEvent, Command
            else if(parameters[1] == SimpleCommand.class)
            {
                return builder.build((command, event) -> {
                    try {
                        method.invoke(o, event, command);
                    } catch(IllegalAccessException | InvocationTargetException e) {
                        LOG.error("Encountered Exception ", e);
                    }
                });
            }
        }

        // If we reach this point there is a malformed method and we shouldn't finish the compilation.
        throw new MalformedParametersException("Method named "+method.getName()+" was not compiled due to improper parameter types!");
    }

    @SafeVarargs
    private static <T> List<T> collect(Predicate<T> filter, T... entities)
    {
        List<T> list = new ArrayList<>();
        for(T entity : entities)
        {
            if(filter.test(entity))
                list.add(entity);
        }
        return list;
    }

}
