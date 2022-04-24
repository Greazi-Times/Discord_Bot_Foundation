package com.greazi.discordbotfoundation.objects.mysql;

import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import net.dv8tion.jda.api.entities.Member;

import java.util.Objects;

public class MysqlMember {

    public int id, discriminator;
    public String discord_id, nickname, username, avatar_url;
    public long time_joined, time_created;
    public boolean is_bot;

    public MysqlMember(int id, String discord_id, String nickname, String username, int discriminator, long time_joined, long time_created, String avatar_url, boolean is_bot){
        this.id = id;
        this.discord_id = discord_id;
        this.nickname = nickname;
        this.username = username;
        this.discriminator = discriminator;
        this.time_joined = time_joined;
        this.time_created = time_created;
        this.avatar_url = avatar_url;
        this.is_bot = is_bot;
    }

    public int getId() {
        return id;
    }

    public String getDiscord_id() {
        return discord_id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUsername() {
        return username;
    }

    public int getDiscriminator() {
        return discriminator;
    }

    public long getTime_joined() {
        return time_joined;
    }

    public long getTime_created() {
        return time_created;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public Member getDiscordMember(){
        return Objects.requireNonNull(SimpleBot.getJDA().getGuildById(SimpleSettings.Bot.MainGuild())).getMemberById(discord_id);
    }

}
