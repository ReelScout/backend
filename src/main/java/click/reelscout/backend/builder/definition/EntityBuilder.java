package click.reelscout.backend.builder.definition;

public interface EntityBuilder<E, T extends EntityBuilder<E, T>> {
    T id(Long id);
    E build();
}
