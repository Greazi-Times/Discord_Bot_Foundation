package com.greazi.discordbotfoundation.managers.members;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.SimpleBot;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberUpdateEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MemberStorageEvents extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        SimpleBot.getMemberStorage().insertMember(event.getMember());
        super.onGuildMemberJoin(event);
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        SimpleBot.getMemberStorage().deleteMember(event.getUser().getId());
    }

    @Override
    public void onGuildMemberUpdateNickname(GuildMemberUpdateNicknameEvent event) {
        Common.log.info("member updated nickname");
        SimpleBot.getMemberStorage().updateMember(event.getMember());
    }

    @Override
    public void onGuildMemberUpdate(GuildMemberUpdateEvent event) {
        Common.log.info("member updated");
        SimpleBot.getMemberStorage().updateMember(event.getMember());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Common.log.info(event.getMessage().getContentRaw());
    }
}
