package click.reelscout.backend.mapper.definition;

public interface EntityMapper<E, R, S, B, T extends EntityMapper<E, R, S, B, T>> {
    S toDto(E entity);

    B toBuilder(E entity);

    E toEntity(R dto);
}
