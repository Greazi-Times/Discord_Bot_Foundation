package com.greazi.discordbotfoundation.command;

import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.debug.Debugger;
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
        Debugger.debug("SlashCommandHandler", "adding SlashCommandHandler event listener");
        SimpleBot.getJDA().addEventListener(this);
    }

    public void addCommand(SimpleSlashCommand module) {
        Debugger.debug("SlashCommandHandler", "Adding commands...");
        Debugger.debug("SlashCommandHandler", "Getting SlashCommandData");
        SlashCommandData command = Commands.slash(module.getCommand(), module.getDescription());

        Debugger.debug("SlashCommandHandler", "Retrieving sub commands");
        module.getSubCommands().forEach(command::addSubcommands);
        module.getSubcommandGroup().forEach(command::addSubcommandGroups);
        if (!module.getSubCommands().isEmpty() || !module.getSubcommandGroup().isEmpty()){
            Debugger.debug("SlashCommandHandler", "No subcommands getting options");
            module.getOptions().forEach(command::addOptions);
        }
        Debugger.debug("SlashCommandHandler", " Settings default enabled and description");
        command.setDefaultEnabled(module.getDefaultEnabled());
        command.setDescription(module.getDescription());

        Debugger.debug("SlashCommandHandler", "Adding slash commands " + command.getName());
        slashCommands.add(command);
        cmdList.put(module.getCommand(), module);
    }

    public void registerCommands() {
        Debugger.debug("SlashCommandHandler", "Registering command");
        if (slashCommands.isEmpty()) return;

        SimpleBot.getGuild().updateCommands()
                .addCommands(slashCommands)
                .queue(commands -> {
                    commands.forEach(cmd -> {
                        Debugger.debug("SlashCommandHandler", "Getting command from cmdList " + cmd.getName());
                        SimpleSlashCommand module = cmdList.get(cmd.getName());

                        Debugger.debug("SlashCommandHandler", "Adding privilages to " + cmd.getName());
                        List<CommandPrivilege> commandPrivileges = new ArrayList<>();
                        module.getDisabledRoles().forEach(role -> commandPrivileges.add(CommandPrivilege.disableRole(role.getId())));
                        module.getDisabledUsers().forEach(user -> commandPrivileges.add(CommandPrivilege.disableUser(user.getId())));
                        module.getEnabledRoles().forEach(role -> commandPrivileges.add(CommandPrivilege.enableRole(role.getId())));
                        module.getEnabledUsers().forEach(user -> commandPrivileges.add(CommandPrivilege.enableUser(user.getId())));

                        Debugger.debug("SlashCommandHandler", "Updating the command " + cmd.getName());
                        SimpleBot.getGuild().updateCommandPrivilegesById(cmd.getId(), commandPrivileges);
                    });
                });
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        // Retrieve the command class from the command that has been run
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
