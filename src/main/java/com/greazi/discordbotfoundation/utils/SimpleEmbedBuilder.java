package com.greazi.discordbotfoundation.utils;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.awt.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * A simple class that allows you to create an embed
 */
public class SimpleEmbedBuilder extends EmbedBuilder {

    /**
     * Create an embed
     */
    public SimpleEmbedBuilder() {
        color(new Color(47, 49, 54));
    }

    /**
     * Create a embed with a title
     *
     * @param title
     */
    public SimpleEmbedBuilder(final String title) {
        if (title != null)
            setAuthor(title, SimpleSettings.Embed.Link(), SimpleSettings.Embed.Image.Author());

        color(new Color(47, 49, 54));
        footer(SimpleSettings.Developer.Name());
    }

    /**
     * Create an embed with a title and if needed disable the footer
     *
     * @param title
     * @param footer
     */
    public SimpleEmbedBuilder(final String title, final boolean footer) {
        if (title != null)
            setAuthor(title, SimpleSettings.Embed.Link(), SimpleSettings.Embed.Image.Author());
        if (footer)
            footer(SimpleSettings.Developer.Name());

        color(new Color(47, 49, 54));
    }

    /**
     * Create an embed with a disabled footer
     *
     * @param footer
     */
    public SimpleEmbedBuilder(final boolean footer) {
        if (footer)
            footer(SimpleSettings.Developer.Name());

        color(new Color(47, 49, 54));
    }

    /**
     * Set the footer of the embed
     *
     * @param text
     */
    public SimpleEmbedBuilder footer(final String text) {
        setFooter(text + " • " + SimpleSettings.Embed.Footer(), SimpleSettings.Embed.Image.Footer());

        return this;
    }

    /**
     * Set the embed color to red
     */
    public SimpleEmbedBuilder error() {
        color(new Color(255, 67, 67));

        return this;
    }

    /**
     * Set the embed color to green
     */
    public SimpleEmbedBuilder success() {
        color(new Color(140, 255, 142));

        return this;
    }

    /**
     * Set the text of the embed
     *
     * @param text
     */
    public SimpleEmbedBuilder text(final String text) {
        setDescription(text);

        return this;
    }

    /**
     * Set the text with multiple lines
     *
     * @param text
     */
    public SimpleEmbedBuilder text(final String... text) {
        setDescription(String.join("\n", text));

        return this;
    }

    /**
     * Set the thumbnail of the gui
     *
     * @param url
     */
    public SimpleEmbedBuilder thumbnail(final String url) {
        super.setThumbnail(url);

        return this;
    }

    /**
     * Set the color of the gui requires a `new Color()`
     *
     * @param color
     * @return
     */
    public SimpleEmbedBuilder color(final Color color) {
        if (color == null)
            return this;

        super.setColor(color);
        return this;
    }

    /**
     * Set the image of the embed
     *
     * @param url
     */
    public SimpleEmbedBuilder image(final String url) {
        super.setImage(url);

        return this;
    }

    /**
     * Add a field on the embed
     *
     * @param name
     * @param value
     * @param inline
     */
    public SimpleEmbedBuilder field(final String name, final String value, final boolean inline) {
        super.addField(name, value, inline);

        return this;
    }

    /**
     * Add a blank field on the embed
     *
     * @param inline
     */
    public SimpleEmbedBuilder blankField(final boolean inline) {
        super.addBlankField(inline);

        return this;
    }

    /**
     * Get the text of the embed
     */
    public String getText() {
        return super.getDescriptionBuilder().toString();
    }

    /**
     * Send the embed to a specific channel
     *
     * @param textChannel
     */
    public void queue(final TextChannel textChannel) {
        textChannel.sendMessageEmbeds(build()).queue();
    }

    /**
     * Send the embed to a user
     *
     * @param member
     */
    public void queue(final Member member) {
        queue(member.getUser());
    }

    /**
     * Send the embed to a user
     *
     * @param member
     * @param consumer
     */
    public void queue(final Member member, final Consumer<Message> consumer) {
        queue(member.getUser(), consumer);
    }

    /**
     * Send an embed to a user
     *
     * @param user
     */
    public void queue(final User user) {
        try {
            user.openPrivateChannel().submit()
                    .thenCompose(channel -> channel.sendMessageEmbeds(build()).submit())
                    .whenComplete((message, error) -> {
                        if (error != null) {
                            Common.warning("Could not send pm to " + user.getName());
                        }
                    });
        } catch (final Exception ignore) {
        }
    }

    /**
     * Send an embed to a user
     *
     * @param user
     * @param consumer
     */
    public void queue(final User user, final Consumer<Message> consumer) {
        try {
            user.openPrivateChannel().queue(c -> c.sendMessageEmbeds(build()).queue(consumer));
        } catch (final Exception ignore) {
        }
    }

    /**
     * Send an embed
     *
     * @param textChannel
     * @param consumer
     */
    public void queue(final TextChannel textChannel, final Consumer<Message> consumer) {
        textChannel.sendMessageEmbeds(build()).queue(consumer);
    }

    /**
     * Send the embed with a delay
     *
     * @param textChannel
     * @param delay
     * @param unit
     */
    public void queueAfter(final TextChannel textChannel, final int delay, final TimeUnit unit) {
        textChannel.sendMessageEmbeds(build()).queueAfter(delay, unit);
    }

    /**
     * Send the embed with a delay
     *
     * @param textChannel
     * @param delay
     * @param unit
     * @param success
     */
    public void queueAfter(final TextChannel textChannel, final int delay, final TimeUnit unit, final Consumer<Message> success) {
        textChannel.sendMessageEmbeds(build()).queueAfter(delay, unit, success);
    }

    /**
     * Send the embed with a delay
     *
     * @param user
     * @param delay
     * @param time
     */
    public void queueAfter(final User user, final int delay, final TimeUnit time) {
        try {
            user.openPrivateChannel().complete().sendMessageEmbeds(build()).queueAfter(delay, time);
        } catch (final ErrorResponseException ignore) {
        }
    }

    /**
     * Reply with a message
     *
     * @param message
     * @return
     */
    /*public Message reply(final Message message) {
        return reply(message, true);
    }*/

    /**
     * Reply with a message and mention teh user
     *
     * @param message
     * @param mention
     * @return
     */
    /*public Message reply(final Message message, final boolean mention) {
        return message.reply(message).mentionRepliedUser(mention).complete();
    }*/

    /**
     * Reply to a message only for a limited time
     *
     * @param message
     * @param duration
     * @param timeUnit
     */
    public void replyTemporary(final Message message, final int duration, final TimeUnit timeUnit) {
        replyTemporary(message, true, duration, timeUnit);
    }

    /**
     * Reply to a message only for a limited time
     *
     * @param message
     * @param mention
     * @param duration
     * @param timeUnit
     */
    public void replyTemporary(final Message message, final boolean mention, final int duration, final TimeUnit timeUnit) {
        message.replyEmbeds(build()).mentionRepliedUser(mention).queue((msg -> msg.delete().submitAfter(duration, timeUnit)));
    }

    /**
     * Send the message temporarily
     *
     * @param textChannel
     * @param duration
     * @param timeUnit
     */
    public void sendTemporary(final TextChannel textChannel, final int duration, final TimeUnit timeUnit) {
        queue(textChannel, (msg) -> msg.delete().submitAfter(duration, timeUnit));
    }

    /**
     * Send the message temporarily
     *
     * @param textChannel
     * @param duration
     */
    public void sendTemporary(final TextChannel textChannel, final int duration) {
        sendTemporary(textChannel, duration, TimeUnit.SECONDS);
    }

    /**
     * Send the message after a period of time
     *
     * @param textChannel
     * @param duration
     * @param onSuccess
     */
    public ScheduledFuture<?> sendAfter(final TextChannel textChannel, final int duration, final Consumer<Message> onSuccess) {
        return textChannel.sendMessageEmbeds(build()).queueAfter(duration, TimeUnit.SECONDS, onSuccess);
    }

    /**
     * Send the message after a period of time
     *
     * @param textChannel
     * @param duration
     * @param timeUnit
     * @param onSuccess
     */
    public ScheduledFuture<?> sendAfter(final TextChannel textChannel, final int duration, final TimeUnit timeUnit, final Consumer<Message> onSuccess) {
        return textChannel.sendMessageEmbeds(build()).queueAfter(duration, timeUnit, onSuccess);
    }
}
