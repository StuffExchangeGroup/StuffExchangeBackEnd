package pbm.com.exchange.service.mapper;

import org.mapstruct.*;
import pbm.com.exchange.domain.AppConfiguration;
import pbm.com.exchange.service.dto.AppConfigurationDTO;

/**
 * Mapper for the entity {@link AppConfiguration} and its DTO {@link AppConfigurationDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface AppConfigurationMapper extends EntityMapper<AppConfigurationDTO, AppConfiguration> {}
