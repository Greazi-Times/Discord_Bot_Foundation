package com.greazi.discordbotfoundation.managers.members;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.mysql.query.*;
import com.greazi.discordbotfoundation.mysql.table.ITable;
import com.greazi.discordbotfoundation.objects.mysql.MysqlMember;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.sql.SQLException;
import java.util.Map;

public class MemberStorage {

    public MemberStorage() {
        if (!SimpleSettings.getInstance().isMysqlEnabled()) return;

        ITable membersTable = SimpleBot.getMySQL().table("members");

        try {
            if (!membersTable.exists()){
                Create create = membersTable.create().ifNotExists();

                create.id().increment().primary();
                create.integer("discord_id").unique();
                create.string("nickname", 50).nullable();
                create.string("username", 50);
                create.integer("discriminator", 5);
                create.bigInteger("time_joined");
                create.bigInteger("time_created");
                create.text("avatar_url").nullable();
                create.integer("is_bot", 1).defaultValue(0);

                Boolean result = create.execute();
                if (result){
                    Common.warning("There was an error creating the members table");
                }else{
                    Common.warning("Members table created");

                    SimpleBot.getMySQL().getConnection().prepareStatement("alter table "+membersTable.getName()+" add primary key(`discord_id`);");

                    Common.warning("Adding all members");
                    Guild mainGuild = SimpleBot.getJDA().getGuildById(SimpleSettings.getInstance().getMainGuild());
                    mainGuild.getMembers().forEach(member -> {
                        Insert query = membersTable.insert();
                        query.field("discord_id", member.getId());
                        query.field("nickname", member.getNickname());
                        query.field("username", member.getUser().getName());
                        query.field("discriminator", member.getUser().getDiscriminator());
                        query.field("time_joined", member.getTimeJoined().toEpochSecond());
                        query.field("time_created", member.getTimeCreated().toEpochSecond());
                        query.field("avatar_url", member.getUser().getAvatarUrl());
                        query.field("is_bot", member.getUser().isBot() ? 1 : 0);

                        try{
                            query.execute();
                        }catch (Exception e){
                            Common.warning("Error while inserting member: "+e.getMessage());
                            e.printStackTrace();
                        }
                    });
                    Common.warning("All members added");
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        SimpleBot.getJDA().addEventListener(new MemberStorageEvents());
        Common.warning("Events registered");

        MysqlMember member = this.getMember("240439907833741322");
        if (member != null){
            Common.log(member.getDiscriminator()+"");
        }
    }

    public boolean insertMember(Member member){
        ITable membersTable = SimpleBot.getMySQL().table("members");

        try {
            Insert query = membersTable.insert();
            query.field("discord_id", member.getId());
            query.field("nickname", member.getNickname());
            query.field("username", member.getUser().getName());
            query.field("discriminator", member.getUser().getDiscriminator());
            query.field("time_joined", member.getTimeJoined());
            query.field("time_created", member.getTimeCreated());
            query.field("avatar_url", member.getUser().getAvatarUrl());
            query.field("is_bot", member.getUser().isBot() ? 1 : 0);

            return query.execute() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean removeMember(Member member){
        ITable membersTable = SimpleBot.getMySQL().table("members");

        try {
            Select query = membersTable.select().where("id = ?", member.getId()).limit(1);
            Map<String, Object> fetch = query.fetch();

            Common.log(fetch.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public MysqlMember getMember(int dbId){
        MysqlMember member = null;
        ITable membersTable = SimpleBot.getMySQL().table("members");

        try {
            Select query = membersTable.select().where("id = ?", dbId);
            Map<String, Object> fetch = query.fetch();

            if (!fetch.isEmpty()){
                int id = (int) fetch.get("id");
                String discord_id = (String) fetch.get("discord_id");
                String nickname = (String) fetch.get("nickname");
                String username = (String) fetch.get("username");
                int discriminator = (int) fetch.get("discriminator");
                long time_joined = (long) fetch.get("time_joined");
                long time_created = (long) fetch.get("time_created");
                String avatar_url = (String) fetch.get("avatar_url");
                boolean is_bot = (int) fetch.get("is_bot") == 1;

                member = new MysqlMember(id, discord_id, nickname, username, discriminator, time_joined, time_created, avatar_url, is_bot);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (member != null){
            Common.log(member.getDiscriminator()+"");
        }
        return member;
    }

    public MysqlMember getMember(String discordId){
        MysqlMember member = null;
        ITable membersTable = SimpleBot.getMySQL().table("members");

        try {
            Select query = membersTable.select().where("discord_id = ?", discordId);
            Map<String, Object> fetch = query.fetch();

            if (!fetch.isEmpty()){
                int id = (int) fetch.get("id");
                String discord_id = (String) fetch.get("discord_id");
                String nickname = (String) fetch.get("nickname");
                String username = (String) fetch.get("username");
                int discriminator = (int) fetch.get("discriminator");
                long time_joined = (long) fetch.get("time_joined");
                long time_created = (long) fetch.get("time_created");
                String avatar_url = (String) fetch.get("avatar_url");
                boolean is_bot = (int) fetch.get("is_bot") == 1;

                member = new MysqlMember(id, discord_id, nickname, username, discriminator, time_joined, time_created, avatar_url, is_bot);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return member;
    }

    public void updateMember(Member member){
        ITable membersTable = SimpleBot.getMySQL().table("members");

        try {
            Update query = membersTable.update().where("discord_id = ?", member.getId());

            query.field("nickname", member.getNickname());
            query.field("username", member.getUser().getName());
            query.field("discriminator", member.getUser().getDiscriminator());
            query.field("time_joined", member.getTimeJoined());
            query.field("time_created", member.getTimeCreated());
            query.field("avatar_url", member.getUser().getAvatarUrl());
            query.field("is_bot", member.getUser().isBot() ? 1 : 0);

            query.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteMember(String memberId){
        ITable membersTable = SimpleBot.getMySQL().table("members");

        try {
            Delete query = membersTable.delete().where("discord_id = ?", memberId);
            query.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
