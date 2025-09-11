package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.Watchlist;

import java.util.List;

/**
 * Builder contract for creating {@link Watchlist} instances.
 */
public interface WatchlistBuilder extends EntityBuilder<Watchlist, WatchlistBuilder> {
    /** Set the watchlist name. */
    WatchlistBuilder name(String name);
    /** Set the contents list. */
    WatchlistBuilder contents(List<Content> contents);
    /** Set whether the watchlist is public. */
    WatchlistBuilder isPublic(Boolean isPublic);
    /** Set the owner member. */
    WatchlistBuilder member(Member member);
}
