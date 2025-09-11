package click.reelscout.backend.model.jpa;

import click.reelscout.backend.builder.implementation.WatchlistBuilderImplementation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Entity class representing a Watchlist.
 */
@Entity
@Getter
@NoArgsConstructor
public class Watchlist implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Content> contents;

    @Column(nullable = false)
    private Boolean isPublic;

    @ManyToOne
    private Member member;

    public Watchlist(WatchlistBuilderImplementation builder) {
        this.id = builder.getId();
        this.name = builder.getName();
        this.contents = builder.getContents();
        this.isPublic = builder.getIsPublic();
        this.member = builder.getMember();
    }
}
