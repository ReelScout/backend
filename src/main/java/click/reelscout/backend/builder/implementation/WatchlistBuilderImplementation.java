package click.reelscout.backend.builder.implementation;

import click.reelscout.backend.builder.definition.WatchlistBuilder;
import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.Watchlist;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
public class WatchlistBuilderImplementation implements WatchlistBuilder {
    private Long id;
    private String name;
    private List<Content> contents;
    private Boolean isPublic;
    private Member member;

    @Override
    public WatchlistBuilder id(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public WatchlistBuilder name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public WatchlistBuilder contents(List<Content> contents) {
        this.contents = contents;
        return this;
    }

    @Override
    public WatchlistBuilder isPublic(Boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }

    @Override
    public WatchlistBuilder member(Member member) {
        this.member = member;
        return this;
    }

    @Override
    public Watchlist build() {
        return new Watchlist(this);
    }
}
