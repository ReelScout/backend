package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.Watchlist;

import java.util.List;

public interface WatchlistBuilder extends EntityBuilder<Watchlist, WatchlistBuilder> {
    WatchlistBuilder name(String name);
    WatchlistBuilder contents(List<Content> contents);
    WatchlistBuilder isPublic(Boolean isPublic);
    WatchlistBuilder member(Member member);
}
