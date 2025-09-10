package click.reelscout.backend.mapper.implemetation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConversationMapperImplementationTest {


    // System Under Test
    private final ConversationMapperImplementation mapper = new ConversationMapperImplementation();


    @Test
    @DisplayName("toDmConversation should fail fast when lastMessage is null")
    void toDmConversation_throwsOnNullMessage() {
        // Given
        String counterpart = "eve";

        // When / Then: a NullPointerException is acceptable here because the method dereferences lastMessage
        assertThatThrownBy(() -> mapper.toDmConversation(null, counterpart))
                .isInstanceOf(NullPointerException.class);
    }
}