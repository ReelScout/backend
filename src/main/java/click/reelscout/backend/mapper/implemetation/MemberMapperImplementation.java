package click.reelscout.backend.mapper.implemetation;

import click.reelscout.backend.builder.definition.MemberBuilder;
import click.reelscout.backend.dto.request.MemberRequestDTO;
import click.reelscout.backend.dto.response.MemberResponseDTO;
import click.reelscout.backend.mapper.definition.MemberMapper;
import click.reelscout.backend.model.elasticsearch.MemberDoc;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberMapperImplementation implements MemberMapper {
    private final MemberBuilder memberBuilder;
    private final PasswordEncoder passwordEncoder;

    /** {@inheritDoc} */
    @Override
    public MemberResponseDTO toDto(Member member, String base64Image) {
        return new MemberResponseDTO(member.getId(), member.getFirstName(), member.getLastName(), member.getBirthDate(), member.getFavoriteGenres(), member.getUsername(), member.getEmail(), member.getRole(), base64Image);
    }

    /** {@inheritDoc} */
    @Override
    public MemberBuilder toBuilder(Member member) {
        return memberBuilder
                .id(member.getId())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .birthDate(member.getBirthDate())
                .favoriteGenres(member.getFavoriteGenres())
                .username(member.getUsername())
                .email(member.getEmail())
                .password(member.getPassword())
                .s3ImageKey(member.getS3ImageKey())
                .role(member.getRole())
                .suspendedUntil(member.getSuspendedUntil())
                .suspendedReason(member.getSuspendedReason());
    }

    /** {@inheritDoc} */
    @Override
    public Member toEntity(MemberRequestDTO memberRequestDTO, String s3ImageKey) {
        return memberBuilder
                .id(null)
                .firstName(memberRequestDTO.getFirstName())
                .lastName(memberRequestDTO.getLastName())
                .birthDate(memberRequestDTO.getBirthDate())
                .favoriteGenres(memberRequestDTO.getFavoriteGenres())
                .username(memberRequestDTO.getUsername())
                .email(memberRequestDTO.getEmail())
                .password(passwordEncoder.encode(memberRequestDTO.getPassword()))
                .role(Role.MEMBER)
                .s3ImageKey(s3ImageKey)
                .suspendedUntil(null)
                .suspendedReason(null)
                .build();
    }

    /** {@inheritDoc} */
    @Override
    public MemberDoc toDoc(Member member) {
        return new MemberDoc(member);
    }
}
