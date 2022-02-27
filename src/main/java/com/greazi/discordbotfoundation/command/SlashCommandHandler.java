package com.greazi.discordbotfoundation.command;

import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SlashCommandHandler extends ListenerAdapter {

    private final HashMap<String, SimpleSlashCommand> cmdList = new HashMap<>();
    private final List<SlashCommandData> slashCommands = new ArrayList<>();

    public SlashCommandHandler(){
        SimpleBot.getJDA().addEventListener(this);
    }

    public void addCommand(SimpleSlashCommand module) {
        SlashCommandData command = Commands.slash(module.getCommand(), module.getDescription());

        module.getSubCommands().forEach(command::addSubcommands);
        module.getSubcommandGroup().forEach(command::addSubcommandGroups);
        if (!module.getSubCommands().isEmpty() || !module.getSubcommandGroup().isEmpty()){
            module.getOptions().forEach(command::addOptions);
        }
        command.setDefaultEnabled(module.getDefaultEnabled());

        slashCommands.add(command);
        cmdList.put(module.getCommand(), module);
    }

    public void registerCommands() {
        if (slashCommands.isEmpty()) return;

        SimpleBot.getJDA().updateCommands()
                .addCommands(slashCommands)
                .queue(commands -> {
                    commands.forEach(cmd -> {
                        SimpleSlashCommand module = cmdList.get(cmd.getName());

                        List<CommandPrivilege> commandPrivileges = new ArrayList<>();
                        module.getDisabledRoles().forEach(role -> commandPrivileges.add(CommandPrivilege.disableRole(role.getId())));
                        module.getDisabledUsers().forEach(user -> commandPrivileges.add(CommandPrivilege.disableUser(user.getId())));
                        module.getEnabledRoles().forEach(role -> commandPrivileges.add(CommandPrivilege.enableRole(role.getId())));
                        module.getEnabledUsers().forEach(user -> commandPrivileges.add(CommandPrivilege.enableUser(user.getId())));

                        // TODO - Add Command Privileges
                    });
                });
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        SimpleSlashCommand module = cmdList.get(event.getName());

        if (module.getGuildOnly() && !Objects.requireNonNull(event.getGuild()).getId().equals(SimpleSettings.getInstance().getMainGuild())){
            return;
        }

        if (event.getTextChannel().isNSFW() && !module.getNsfwOnly()){
            return;
        }

        module.execute(event);
    }
}
