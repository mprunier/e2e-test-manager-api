package fr.njj.galaxion.endtoendtesting.mapper;

import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationSynchronizationResponse;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSynchronizationEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigurationSynchronizationMapper {

    public static ConfigurationSynchronizationResponse build(ConfigurationSynchronizationEntity entity) {
        return ConfigurationSynchronizationResponse.builder()
                                                   .status(entity.getStatus())
                                                   .syncDate(entity.getLastSynchronization())
                                                   .error(entity.getError())
                                                   .build();
    }
}

