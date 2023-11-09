package com.greazi.old.objects;

import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ChannelQuery extends Query<TextChannel> {

    public ChannelQuery(final List<TextChannel> objects) {
        super(objects);
    }

    public ChannelQuery inCategory(final String category) {
        final List<TextChannel> channels = all().stream().filter(textChannel -> Objects.requireNonNull(textChannel.getParentCategory()).getName().equalsIgnoreCase(category)).collect(Collectors.toList());
        return new ChannelQuery(channels);
    }

    public ChannelQuery inCategory(final Category category) {
        return inCategory(category.getName());
    }
}