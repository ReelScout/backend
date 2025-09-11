package click.reelscout.backend.mapper.definition;

import click.reelscout.backend.builder.definition.MemberBuilder;
import click.reelscout.backend.dto.request.MemberRequestDTO;
import click.reelscout.backend.dto.response.MemberResponseDTO;
import click.reelscout.backend.model.jpa.Member;

/**
 * Mapper interface for converting between {@link Member} entities, DTOs, and builders.
 */
public interface MemberMapper extends UserMapper<Member, MemberRequestDTO, MemberResponseDTO, MemberBuilder> {
    // No additional methods; inherits all methods from UserMapper.
}