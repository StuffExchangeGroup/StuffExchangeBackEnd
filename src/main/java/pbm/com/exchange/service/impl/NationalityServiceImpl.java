package pbm.com.exchange.service.impl;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pbm.com.exchange.domain.Nationality;
import pbm.com.exchange.repository.NationalityRepository;
import pbm.com.exchange.service.NationalityService;
import pbm.com.exchange.service.dto.NationalityDTO;
import pbm.com.exchange.service.mapper.NationalityMapper;

/**
 * Service Implementation for managing {@link Nationality}.
 */
@Service
@Transactional
public class NationalityServiceImpl implements NationalityService {

    private final Logger log = LoggerFactory.getLogger(NationalityServiceImpl.class);

    private final NationalityRepository nationalityRepository;

    private final NationalityMapper nationalityMapper;

    public NationalityServiceImpl(NationalityRepository nationalityRepository, NationalityMapper nationalityMapper) {
        this.nationalityRepository = nationalityRepository;
        this.nationalityMapper = nationalityMapper;
    }

    @Override
    public NationalityDTO save(NationalityDTO nationalityDTO) {
        log.debug("Request to save Nationality : {}", nationalityDTO);
        Nationality nationality = nationalityMapper.toEntity(nationalityDTO);
        nationality = nationalityRepository.save(nationality);
        return nationalityMapper.toDto(nationality);
    }

    @Override
    public Optional<NationalityDTO> partialUpdate(NationalityDTO nationalityDTO) {
        log.debug("Request to partially update Nationality : {}", nationalityDTO);

        return nationalityRepository
            .findById(nationalityDTO.getId())
            .map(existingNationality -> {
                nationalityMapper.partialUpdate(existingNationality, nationalityDTO);

                return existingNationality;
            })
            .map(nationalityRepository::save)
            .map(nationalityMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NationalityDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Nationalities");
        return nationalityRepository.findAll(pageable).map(nationalityMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NationalityDTO> findOne(Long id) {
        log.debug("Request to get Nationality : {}", id);
        return nationalityRepository.findById(id).map(nationalityMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Nationality : {}", id);
        nationalityRepository.deleteById(id);
    }

    @Override
    public List<NationalityDTO> findAll() {
        log.debug("request to get all nationalities");
        return nationalityMapper.toDto(nationalityRepository.findAll());
    }
}
