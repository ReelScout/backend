package click.reelscout.backend.mapper.implemetation;

import click.reelscout.backend.builder.definition.MemberBuilder;
import click.reelscout.backend.dto.request.MemberRequestDTO;
import click.reelscout.backend.dto.response.MemberResponseDTO;
import click.reelscout.backend.mapper.definition.MemberMapper;
import click.reelscout.backend.model.Member;
import click.reelscout.backend.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class MemberMapperImplementation implements MemberMapper {
    private final MemberBuilder memberBuilder;
    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberResponseDTO toDto(Member member) {
        return toDto(member, null);
    }

    @Override
    public MemberResponseDTO toDto(Member member, String base64Image) {
        return new MemberResponseDTO(member.getId(), member.getFirstName(), member.getLastName(), member.getBirthDate(), member.getUsername(), member.getEmail(), member.getRole(), base64Image);
    }

    @Override
    public MemberBuilder toBuilder(Member member) {
        return memberBuilder
                .id(member.getId())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .birthDate(member.getBirthDate())
                .username(member.getUsername())
                .email(member.getEmail())
                .password(member.getPassword())
                .s3ImageKey(member.getS3ImageKey())
                .role(member.getRole());
    }

    @Override
    public Member toEntity(MemberRequestDTO memberRequestDTO) {
        return toEntity(memberRequestDTO, null);
    }

    @Override
    public Member toEntity(MemberRequestDTO memberRequestDTO, String s3ImageKey) {
        return memberBuilder
                .firstName(memberRequestDTO.getFirstName())
                .lastName(memberRequestDTO.getLastName())
                .birthDate(memberRequestDTO.getBirthDate())
                .username(memberRequestDTO.getUsername())
                .email(memberRequestDTO.getEmail())
                .password(passwordEncoder.encode(memberRequestDTO.getPassword()))
                .role(Role.MEMBER)
                .s3ImageKey(s3ImageKey)
                .build();
    }
}
