package com.greazi.discordbotfoundation.module;

import net.dv8tion.jda.annotations.ForRemoval;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.*;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h2><b>Slash Commands In JDA-Chewtils</b></h2>
 *
 * <p>This intends to mimic the {@link SimpleCommand command} with minimal breaking changes,
 * to make migration easy and smooth.</p>
 * <p>Breaking changes are documented
 * <a href="https://github.com/Chew/JDA-Chewtils/wiki/Command-to-SlashCommand-Migration">here</a>.</p>
 * {@link SimpleSlashCommand#execute(SlashCommandEvent) #execute(CommandEvent)} body:
 *
 * <pre><code> public class ExampleCmd extends Command {
 *
 *      public ExampleCmd() {
 *          this.name = "example";
 *          this.help = "gives an example of commands do";
 *      }
 *
 *      {@literal @Override}
 *      protected void execute(SlashCommandEvent event) {
 *          event.reply("Hey look! This would be the bot's reply if this was a command!").queue();
 *      }
 *
 * }</code></pre>
 *
 * Execution is with the provision of the SlashCommandEvent is performed in two steps:
 * <ul>
 *     <li>{@link SimpleSlashCommand#run(SlashCommandEvent) run} - The command runs
 *     through a series of conditionals, automatically terminating the command instance if one is not met,
 *     and possibly providing an error response.</li>
 *
 *     <li>{@link SimpleSlashCommand#execute(SlashCommandEvent) execute} - The command,
 *     now being cleared to run, executes and performs whatever lies in the abstract body method.</li>
 * </ul>
 *
 * @author Olivia (Chew)
 */
public abstract class SimpleSlashCommand extends SimpleCommand
{
    /**
     * This option is deprecated in favor of {@link #enabledRoles}
     * Please replace this with this.enabledRoles = new String[]{Roles};
     * While this check is still done, it's better to let Discord do the work.<br>
     * This deprecation can be ignored if you intend to support normal and slash commands.
     */
    @Deprecated
    protected String requiredRole = null;

    /**
     * The list of role IDs who can use this Slash Command.
     * Because command privileges are restricted to a Guild, these will not take effect for Global commands.<br>
     * This is useless if {@link #defaultEnabled} isn't false.
     */
    protected String[] enabledRoles = new String[]{};

    /**
     * The list of user IDs who can use this Slash Command.
     * Because command privileges are restricted to a Guild, these will not take effect for Global commands.<br>
     * This is useless if {@link #defaultEnabled} isn't false.
     */
    protected String[] enabledUsers = new String[]{};

    /**
     * The list of role IDs who cannot use this Slash Command.
     * Because command privileges are restricted to a Guild, these will not take effect for Global commands.<br>
     * This is useless if {@link #defaultEnabled} isn't true.
     */
    protected String[] disabledRoles = new String[]{};

    /**
     * The list of user IDs who cannot use this Slash Command.
     * Because command privileges are restricted to a Guild, these will not take effect for Global commands.<br>
     * This is useless if {@link #defaultEnabled} isn't true.
     */
    protected String[] disabledUsers = new String[]{};

    /**
     * Whether this command is disabled by default.
     * If disabled, you must give yourself permission to use it.<br>
     * In order for {@link #enabledUsers} and {@link #enabledRoles} to work, this must be set to false.
     * @see net.dv8tion.jda.api.requests.restaction.CommandCreateAction#setDefaultEnabled(boolean)
     * @see SimpleSlashCommand#enabledRoles
     * @see SimpleSlashCommand#enabledUsers
     */
    protected boolean defaultEnabled = true;

    /**
     * The child commands of the command. These are used in the format {@code /<parent name>
     * <child name>}.
     * This is synonymous with sub commands. Additionally, sub-commands cannot have children.<br>
     */
    protected SimpleSlashCommand[] children = new SimpleSlashCommand[0];

    /**
     * The subcommand/child group this is associated with.
     * Will be in format {@code /<parent name> <subcommandGroup name> <subcommand name>}.
     *
     * <b>This only works in a child/subcommand.</b>
     *
     * To instantiate: <code>{@literal new SubcommandGroupData(name, description)}</code><br>
     * It's important the instantiations are the same across children if you intend to keep them in the same group.
     *
     * Can be null, and it will not be assigned to a group.
     */
    protected SubcommandGroupData subcommandGroup = null;

    /**
     * An array list of OptionData.
     *
     * <b>This is incompatible with children. You cannot have a child AND options.</b>
     *
     * This is to specify different options for arguments and the stuff.
     *
     * For example, to add an argument for "input", you can do this:<br>
     * <pre><code>
     *     OptionData data = new OptionData(OptionType.STRING, "input", "The input for the command").setRequired(true);
     *    {@literal List<OptionData> dataList = new ArrayList<>();}
     *     dataList.add(data);
     *     this.options = dataList;</code></pre>
     */
    protected List<OptionData> options = new ArrayList<>();

    /**
     * The command client to be retrieved if needed.
     * @deprecated This is now retrieved from {@link SlashCommandEvent#getClient()}.
     */
    protected CommandClient client;

    /**
     * The main body method of a {@link SimpleSlashCommand SlashCommand}.
     * <br>This is the "response" for a successful
     * {@link SimpleSlashCommand#run(SlashCommandEvent) #run(CommandEvent)}.
     *
     * @param  event
     *         The {@link SlashCommandEvent SlashCommandEvent} that
     *         triggered this Command
     */
    protected abstract void execute(SlashCommandEvent event);

    /**
     * This body is executed when an auto-complete event is received.
     * This only ever gets executed if an auto-complete {@link #options option} is set.
     *
     * @param event The event to handle.
     * @see OptionData#setAutoComplete(boolean)
     */
    public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {}

    /**
     * The main body method of a {@link SimpleCommand Command}.
     * <br>This is the "response" for a successful
     * {@link SimpleCommand#run(CommandEvent) #run(CommandEvent)}.
     * <b>
     *     Because this is a SlashCommand, this is called, but does nothing.
     *     You can still override this if you want to have a separate response for normal [prefix][name].
     *     Keep in mind you must add it as a Command via {@link CommandClientBuilder#addCommand(SimpleCommand)} for it to work properly.
     * </b>
     *
     * @param  event
     *         The {@link com.greazi.discordbotfoundation.module.CommandEvent CommandEvent} that
     *         triggered this Command
     */
    @Override
    protected void execute(CommandEvent event) {}

    /**
     * Runs checks for the {@link SimpleSlashCommand SlashCommand} with the
     * given {@link SlashCommandEvent SlashCommandEvent} that called it.
     * <br>Will terminate, and possibly respond with a failure message, if any checks fail.
     *
     * @param  event
     *         The SlashCommandEvent that triggered this Command
     */
    public final void run(SlashCommandEvent event)
    {
        // set the client
        this.client = event.getClient();

        // child check
        if(event.getSubcommandName() != null)
        {
            for(SimpleSlashCommand cmd: getChildren())
            {
                if(cmd.isCommandFor(event.getSubcommandName()))
                {
                    cmd.run(event);
                    return;
                }
            }
        }

        // owner check
        if(ownerCommand && !(isOwner(event, client)))
        {
            terminate(event, "Only an owner may run this command. Sorry.", client);
            return;
        }

        // is allowed check
        if((event.getChannelType() == ChannelType.TEXT) && !isAllowed(event.getTextChannel()))
        {
            terminate(event, "That command cannot be used in this channel!", client);
            return;
        }

        // required role check
        if(requiredRole!=null)
            if(!(event.getChannelType() == ChannelType.TEXT) || event.getMember().getRoles().stream().noneMatch(r -> r.getName().equalsIgnoreCase(requiredRole)))
            {
                terminate(event, client.getError()+" You must have a role called `"+requiredRole+"` to use that!", client);
                return;
            }

        // availability check
        if(event.getChannelType() != ChannelType.PRIVATE)
        {
            //user perms
            for(Permission p: userPermissions)
            {
                // Member will never be null because this is only ran in a server (text channel)
                if(event.getMember() == null)
                    continue;

                if(p.isChannel())
                {
                    if(!event.getMember().hasPermission(event.getTextChannel(), p))
                    {
                        terminate(event, String.format(userMissingPermMessage, client.getError(), p.getName(), "channel"), client);
                        return;
                    }
                }
                else
                {
                    if(!event.getMember().hasPermission(p))
                    {
                        terminate(event, String.format(userMissingPermMessage, client.getError(), p.getName(), "server"), client);
                        return;
                    }
                }
            }

            // bot perms
            for(Permission p: botPermissions)
            {
                // We can ignore this permission because bots can reply with embeds even without either of these perms.
                // The only thing stopping them is the user's ability to use Application Commands.
                // It's extremely dumb, but what more can you do.
                if (p == Permission.VIEW_CHANNEL || p == Permission.MESSAGE_EMBED_LINKS)
                    continue;

                Member selfMember = event.getGuild() == null ? null : event.getGuild().getSelfMember();
                if(p.isChannel())
                {
                    if(p.isVoice())
                    {
                        GuildVoiceState gvc = event.getMember().getVoiceState();
                        AudioChannel vc = gvc == null ? null : gvc.getChannel();
                        if(vc==null)
                        {
                            terminate(event, client.getError()+" You must be in a voice channel to use that!", client);
                            return;
                        }
                        else if(!selfMember.hasPermission(vc, p))
                        {
                            terminate(event, String.format(botMissingPermMessage, client.getError(), p.getName(), "voice channel"), client);
                            return;
                        }
                    }
                    else
                    {
                        if(!selfMember.hasPermission(event.getTextChannel(), p))
                        {
                            terminate(event, String.format(botMissingPermMessage, client.getError(), p.getName(), "channel"), client);
                            return;
                        }
                    }
                }
                else
                {
                    if(!selfMember.hasPermission(p))
                    {
                        terminate(event, String.format(botMissingPermMessage, client.getError(), p.getName(), "server"), client);
                        return;
                    }
                }
            }

            // nsfw check
            if (nsfwOnly && event.getChannelType() == ChannelType.TEXT && !event.getTextChannel().isNSFW())
            {
                terminate(event, "This command may only be used in NSFW text channels!", client);
                return;
            }
        }
        else if(guildOnly)
        {
            terminate(event, client.getError()+" This command cannot be used in direct messages", client);
            return;
        }

        // cooldown check, ignoring owner
        if(cooldown>0 && !(isOwner(event, client)))
        {
            String key = getCooldownKey(event);
            int remaining = client.getRemainingCooldown(key);
            if(remaining>0)
            {
                terminate(event, getCooldownError(event, remaining, client), client);
                return;
            }
            else client.applyCooldown(key, cooldown);
        }

        // run
        try {
            execute(event);
        } catch(Throwable t) {
            if(client.getListener() != null)
            {
                client.getListener().onSlashCommandException(event, this, t);
                return;
            }
            // otherwise we rethrow
            throw t;
        }

        if(client.getListener() != null)
            client.getListener().onCompletedSlashCommand(event, this);
    }

    /**
     * Tests whether or not the {@link net.dv8tion.jda.api.entities.User User} who triggered this
     * event is an owner of the bot.
     *
     * @param event the event that triggered the command
     * @param client the command client for checking stuff
     * @return {@code true} if the User is the Owner, else {@code false}
     */
    public boolean isOwner(SlashCommandEvent event, CommandClient client)
    {
        if(event.getUser().getId().equals(client.getOwnerId()))
            return true;
        if(client.getCoOwnerIds()==null)
            return false;
        for(String id : client.getCoOwnerIds())
            if(id.equals(event.getUser().getId()))
                return true;
        return false;
    }

    /**
     * Gets the CommandClient.
     *
     * @return the CommandClient.
     * @deprecated This is now retrieved from {@link SlashCommandEvent#getClient()}.
     */
    @Deprecated
    @ForRemoval(deadline = "2.0.0")
    public CommandClient getClient()
    {
        return client;
    }

    /**
     * Gets the enabled roles for this Slash Command.
     * A user MUST have a role for a command to be ran.
     *
     * @return a list of String role IDs
     */
    public String[] getEnabledRoles()
    {
        return enabledRoles;
    }

    /**
     * Gets the enabled users for this Slash Command.
     * A user with an ID in this list is required for the command to be ran.
     *
     * @return a list of String user IDs
     */
    public String[] getEnabledUsers()
    {
        return enabledUsers;
    }

    /**
     * Gets the disabled roles for this Slash Command.
     * A user with this role may not run this command.
     *
     * @return a list of String role IDs
     */
    public String[] getDisabledRoles()
    {
        return disabledRoles;
    }

    /**
     * Gets the disabled users for this Slash Command.
     * Uses in this list may not run this command.
     *
     * @return a list of String user IDs
     */
    public String[] getDisabledUsers()
    {
        return disabledUsers;
    }

    /**
     * Whether or not this command is enabled by default.
     * If disabled by default, you MUST enable {@link #enabledRoles roles}
     * or {@link #enabledUsers users} to access it.
     * This does NOT hide it, it simply appears greyed out.
     *
     * @return a list of String user IDs
     */
    public boolean isDefaultEnabled()
    {
        return defaultEnabled;
    }

    /**
     * Gets the subcommand data associated with this subcommand.
     *
     * @return subcommand data
     */
    public SubcommandGroupData getSubcommandGroup()
    {
        return subcommandGroup;
    }

    /**
     * Gets the options associated with this command.
     *
     * @return the OptionData array for options
     */
    public List<OptionData> getOptions()
    {
        return options;
    }

    /**
     * Builds CommandData for the SlashCommand upsert.
     * This code is executed when we need to upsert the command.
     *
     * Useful for manual upserting.
     *
     * @return the built command data
     */
    public CommandData buildCommandData()
    {
        // Make the command data
        SlashCommandData data = Commands.slash(getName(), getHelp());
        if (!getOptions().isEmpty())
        {
            data.addOptions(getOptions());
        }
        // Check for children
        if (children.length != 0)
        {
            // Temporary map for easy group storage
            Map<String, SubcommandGroupData> groupData = new HashMap<>();
            for (SimpleSlashCommand child : children)
            {
                // Create subcommand data
                SubcommandData subcommandData = new SubcommandData(child.getName(), child.getHelp());
                // Add options
                if (!child.getOptions().isEmpty())
                {
                    subcommandData.addOptions(child.getOptions());
                }

                // If there's a subcommand group
                if (child.getSubcommandGroup() != null)
                {
                    SubcommandGroupData group = child.getSubcommandGroup();

                    SubcommandGroupData newData = groupData.getOrDefault(group.getName(), group)
                        .addSubcommands(subcommandData);

                    groupData.put(group.getName(), newData);
                }
                // Just add to the command
                else
                {
                    data.addSubcommands(subcommandData);
                }
            }
            if (!groupData.isEmpty())
                data.addSubcommandGroups(groupData.values());
        }

        // Default enabled is synonymous with hidden now.
        data.setDefaultEnabled(isDefaultEnabled());

        return data;
    }

    /**
     * Builds CommandPrivilege for the SlashCommand permissions.
     * This code is executed after upsertion to update the permissions.
     * <br>
     * <b>The max amount of privilege is 10, keep this in mind.</b>
     *
     * Useful for manual upserting.
     *
     * @param client the command client for owner checking.
     *               if null, owner checks won't be performed
     * @return the built privilege data
     */
    public List<CommandPrivilege> buildPrivileges(@Nullable CommandClient client)
    {
        List<CommandPrivilege> privileges = new ArrayList<>();
        // Privilege Checks
        for (String role : getEnabledRoles())
            privileges.add(CommandPrivilege.enableRole(role));
        for (String user : getEnabledUsers())
            privileges.add(CommandPrivilege.enableUser(user));
        for (String role : getDisabledRoles())
            privileges.add(CommandPrivilege.disableRole(role));
        for (String user : getDisabledUsers())
            privileges.add(CommandPrivilege.disableUser(user));
        // Co/Owner checks
        if (isOwnerCommand() && client != null)
        {
            // Clear array, we have the priority here.
            privileges.clear();
            // Add owner
            privileges.add(CommandPrivilege.enableUser(client.getOwnerId()));
            // Add co-owners
            if (client.getCoOwnerIds() != null)
                for (String user : client.getCoOwnerIds())
                    privileges.add(CommandPrivilege.enableUser(user));
        }

        // can only have up to 10 privileges
        if (privileges.size() > 10)
            privileges = privileges.subList(0, 10);

        return privileges;
    }

    /**
     * Gets the {@link SimpleSlashCommand#children Command.children} for the Command.
     *
     * @return The children for the Command
     */
    public SimpleSlashCommand[] getChildren()
    {
        return children;
    }

    private void terminate(SlashCommandEvent event, String message, CommandClient client)
    {
        if(message!=null)
            event.reply(message).setEphemeral(true).queue();
        if(client.getListener()!=null)
            client.getListener().onTerminatedSlashCommand(event, this);
    }

    /**
     * Gets the proper cooldown key for this Command under the provided
     * {@link SlashCommandEvent SlashCommandEvent}.
     *
     * @param  event
     *         The CommandEvent to generate the cooldown for.
     *
     * @return A String key to use when applying a cooldown.
     */
    public String getCooldownKey(SlashCommandEvent event)
    {
        switch (cooldownScope)
        {
            case USER:         return cooldownScope.genKey(name,event.getUser().getIdLong());
            case USER_GUILD:   return event.getGuild()!=null ? cooldownScope.genKey(name,event.getUser().getIdLong(),event.getGuild().getIdLong()) :
                    CooldownScope.USER_CHANNEL.genKey(name,event.getUser().getIdLong(), event.getChannel().getIdLong());
            case USER_CHANNEL: return cooldownScope.genKey(name,event.getUser().getIdLong(),event.getChannel().getIdLong());
            case GUILD:        return event.getGuild()!=null ? cooldownScope.genKey(name,event.getGuild().getIdLong()) :
                    CooldownScope.CHANNEL.genKey(name,event.getChannel().getIdLong());
            case CHANNEL:      return cooldownScope.genKey(name,event.getChannel().getIdLong());
            case SHARD:
                event.getJDA().getShardInfo();
                return cooldownScope.genKey(name, event.getJDA().getShardInfo().getShardId());
            case USER_SHARD:
                event.getJDA().getShardInfo();
                return cooldownScope.genKey(name,event.getUser().getIdLong(),event.getJDA().getShardInfo().getShardId());
            case GLOBAL:       return cooldownScope.genKey(name, 0);
            default:           return "";
        }
    }

    /**
     * Gets an error message for this Command under the provided
     * {@link SlashCommandEvent SlashCommandEvent}.
     *
     * @param  event
     *         The CommandEvent to generate the error message for.
     * @param  remaining
     *         The remaining number of seconds a command is on cooldown for.
     * @param client
     *         The CommandClient for checking stuff
     *
     * @return A String error message for this command if {@code remaining > 0},
     *         else {@code null}.
     */
    public String getCooldownError(SlashCommandEvent event, int remaining, CommandClient client)
    {
        if(remaining<=0)
            return null;
        String front = client.getWarning()+" That command is on cooldown for "+remaining+" more seconds";
        if(cooldownScope.equals(CooldownScope.USER))
            return front+"!";
        else if(cooldownScope.equals(CooldownScope.USER_GUILD) && event.getGuild()==null)
            return front+" "+ CooldownScope.USER_CHANNEL.errorSpecification+"!";
        else if(cooldownScope.equals(CooldownScope.GUILD) && event.getGuild()==null)
            return front+" "+ CooldownScope.CHANNEL.errorSpecification+"!";
        else
            return front+" "+cooldownScope.errorSpecification+"!";
    }
}
