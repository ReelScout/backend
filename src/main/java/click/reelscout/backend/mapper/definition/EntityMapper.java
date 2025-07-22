package click.reelscout.backend.mapper.definition;

public interface EntityMapper<E, R, S, B> {
    S toDto(E entity);

    B toBuilder(E entity);

    E toEntity(R dto);
}
