package pbm.com.exchange.service.impl;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pbm.com.exchange.domain.Nationality;
import pbm.com.exchange.domain.Province;
import pbm.com.exchange.repository.NationalityRepository;
import pbm.com.exchange.repository.ProvinceRepository;
import pbm.com.exchange.service.ProvinceService;
import pbm.com.exchange.service.dto.ProvinceDTO;
import pbm.com.exchange.service.mapper.ProvinceMapper;

/**
 * Service Implementation for managing {@link Province}.
 */
@Service
@Transactional
public class ProvinceServiceImpl implements ProvinceService {

    private final Logger log = LoggerFactory.getLogger(ProvinceServiceImpl.class);

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private ProvinceMapper provinceMapper;

    @Autowired
    private NationalityRepository nationalityRepository;

    @Override
    public ProvinceDTO save(ProvinceDTO provinceDTO) {
        log.debug("Request to save Province : {}", provinceDTO);
        Province province = provinceMapper.toEntity(provinceDTO);
        province = provinceRepository.save(province);
        return provinceMapper.toDto(province);
    }

    @Override
    public Optional<ProvinceDTO> partialUpdate(ProvinceDTO provinceDTO) {
        log.debug("Request to partially update Province : {}", provinceDTO);

        return provinceRepository
            .findById(provinceDTO.getId())
            .map(existingProvince -> {
                provinceMapper.partialUpdate(existingProvince, provinceDTO);

                return existingProvince;
            })
            .map(provinceRepository::save)
            .map(provinceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProvinceDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Provinces");
        return provinceRepository.findAll(pageable).map(provinceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProvinceDTO> findOne(Long id) {
        log.debug("Request to get Province : {}", id);
        return provinceRepository.findById(id).map(provinceMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Province : {}", id);
        provinceRepository.deleteById(id);
    }

    @Override
    public List<ProvinceDTO> findAll() {
        log.debug("Request to get all Provinces");
        return provinceMapper.toDto(provinceRepository.findAll());
    }

    @Override
    public List<ProvinceDTO> findByLocationId(Long locationId) {
        log.debug("Request to get Provinces by locationId");
        Nationality nationality = nationalityRepository.findById(locationId).get();
        return provinceMapper.toDto(provinceRepository.findByNationality(nationality));
    }
}
