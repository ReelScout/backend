package click.reelscout.backend.builder.definition;

public interface EntityBuilder<E, B extends EntityBuilder<E, B>> {
    B id(Long id);
    E build();
}
