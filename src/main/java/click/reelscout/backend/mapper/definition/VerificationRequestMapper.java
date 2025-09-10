package click.reelscout.backend.mapper.definition;

import click.reelscout.backend.builder.definition.VerificationRequestBuilder;
import click.reelscout.backend.dto.response.VerificationRequestResponseDTO;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.VerificationRequest;
import click.reelscout.backend.model.jpa.VerificationRequestStatus;

public interface VerificationRequestMapper {
    VerificationRequestResponseDTO toDto(VerificationRequest request);

    VerificationRequestBuilder toBuilder(VerificationRequest request);

    VerificationRequest toEntity(Member requester, String message, VerificationRequestStatus status);
}

