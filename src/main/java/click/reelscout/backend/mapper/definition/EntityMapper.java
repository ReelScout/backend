package click.reelscout.backend.mapper.definition;

public interface EntityMapper<E, R, S, B> {
    S toDto(E entity);

    S toDto(E entity, String base64Image);

    B toBuilder(E entity);

    E toEntity(R dto);

    E toEntity(R dto, String s3ImageKey);
}
