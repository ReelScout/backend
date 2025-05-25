package click.reelscout.backend.mapper.definition;

import click.reelscout.backend.builder.definition.MemberBuilder;
import click.reelscout.backend.dto.request.MemberRequestDTO;
import click.reelscout.backend.dto.response.MemberResponseDTO;
import click.reelscout.backend.model.Member;

public interface MemberMapper extends UserMapper<Member, MemberRequestDTO, MemberResponseDTO, MemberBuilder, MemberMapper> {
}