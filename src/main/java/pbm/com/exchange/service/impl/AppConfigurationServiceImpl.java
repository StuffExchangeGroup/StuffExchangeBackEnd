package pbm.com.exchange.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pbm.com.exchange.domain.AppConfiguration;
import pbm.com.exchange.repository.AppConfigurationRepository;
import pbm.com.exchange.service.AppConfigurationService;
import pbm.com.exchange.service.dto.AppConfigurationDTO;
import pbm.com.exchange.service.mapper.AppConfigurationMapper;

/**
 * Service Implementation for managing {@link AppConfiguration}.
 */
@Service
@Transactional
public class AppConfigurationServiceImpl implements AppConfigurationService {

    private final Logger log = LoggerFactory.getLogger(AppConfigurationServiceImpl.class);

    private final AppConfigurationRepository appConfigurationRepository;

    private final AppConfigurationMapper appConfigurationMapper;

    public AppConfigurationServiceImpl(
        AppConfigurationRepository appConfigurationRepository,
        AppConfigurationMapper appConfigurationMapper
    ) {
        this.appConfigurationRepository = appConfigurationRepository;
        this.appConfigurationMapper = appConfigurationMapper;
    }

    @Override
    public AppConfigurationDTO save(AppConfigurationDTO appConfigurationDTO) {
        log.debug("Request to save AppConfiguration : {}", appConfigurationDTO);
        AppConfiguration appConfiguration = appConfigurationMapper.toEntity(appConfigurationDTO);
        appConfiguration = appConfigurationRepository.save(appConfiguration);
        return appConfigurationMapper.toDto(appConfiguration);
    }

    @Override
    public Optional<AppConfigurationDTO> partialUpdate(AppConfigurationDTO appConfigurationDTO) {
        log.debug("Request to partially update AppConfiguration : {}", appConfigurationDTO);

        return appConfigurationRepository
            .findById(appConfigurationDTO.getId())
            .map(existingAppConfiguration -> {
                appConfigurationMapper.partialUpdate(existingAppConfiguration, appConfigurationDTO);

                return existingAppConfiguration;
            })
            .map(appConfigurationRepository::save)
            .map(appConfigurationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppConfigurationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all AppConfigurations");
        return appConfigurationRepository.findAll(pageable).map(appConfigurationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AppConfigurationDTO> findOne(Long id) {
        log.debug("Request to get AppConfiguration : {}", id);
        return appConfigurationRepository.findById(id).map(appConfigurationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete AppConfiguration : {}", id);
        appConfigurationRepository.deleteById(id);
    }
}
