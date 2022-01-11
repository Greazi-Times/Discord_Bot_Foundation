package com.greazi.discordbotfoundation.managers.members;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.mysql.MySQL;
import com.greazi.discordbotfoundation.mysql.query.Create;
import com.greazi.discordbotfoundation.mysql.query.Insert;
import com.greazi.discordbotfoundation.mysql.query.Select;
import com.greazi.discordbotfoundation.mysql.table.ITable;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import net.dv8tion.jda.api.entities.Member;

import java.sql.SQLException;
import java.util.Map;

public class MemberStorage {

    public MemberStorage() {
        if (SimpleSettings.getInstance().isMysqlEnabled()) return;

        ITable membersTable = SimpleBot.getMySQL().table("members");

        try {
            if (!membersTable.exists()){
                Create create = membersTable.create().ifNotExists();

                create.id().increment().primary();
                create.string("nickname", 50);
                create.string("username", 50);
                create.integer("discriminator", 5);
                create.bigInteger("time_joined");
                create.bigInteger("time_created");
                create.string("avatar_url").nullable();

                Boolean result = create.execute();
                if (!result){
                    Common.log.error("There was an error creating the members table");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public boolean InsertMember(Member member){
        ITable membersTable = SimpleBot.getMySQL().table("members");

        try {
            Insert query = membersTable.insert();
            query.field("nickname", member.getNickname());
            query.field("username", member.getUser().getName());
            query.field("discriminator", member.getUser().getDiscriminator());
            query.field("time_joined", member.getTimeJoined());
            query.field("time_created", member.getTimeCreated());
            query.field("avatar_url", member.getUser().getAvatarUrl());

            return query.execute() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Member getMember(int id){
        Member member = null;
        ITable membersTable = SimpleBot.getMySQL().table("members");

        try {
            Select query = membersTable.select().where("id = ?", id).limit(1);
            Map<String, Object> fetch = query.fetch();

            Common.log.info(fetch.toString());

            //member = SimpleBot.getJDA().getGuildById("").getMemberById("");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return member;
    }

}
