package pbm.com.exchange.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pbm.com.exchange.domain.ProductPurpose;
import pbm.com.exchange.repository.ProductPurposeRepository;
import pbm.com.exchange.service.ProductPurposeService;
import pbm.com.exchange.service.dto.ProductPurposeDTO;
import pbm.com.exchange.service.mapper.ProductPurposeMapper;

/**
 * Service Implementation for managing {@link ProductPurpose}.
 */
@Service
@Transactional
public class ProductPurposeServiceImpl implements ProductPurposeService {

    private final Logger log = LoggerFactory.getLogger(ProductPurposeServiceImpl.class);

    private final ProductPurposeRepository productPurposeRepository;

    private final ProductPurposeMapper productPurposeMapper;

    public ProductPurposeServiceImpl(ProductPurposeRepository productPurposeRepository, ProductPurposeMapper productPurposeMapper) {
        this.productPurposeRepository = productPurposeRepository;
        this.productPurposeMapper = productPurposeMapper;
    }

    @Override
    public ProductPurposeDTO save(ProductPurposeDTO productPurposeDTO) {
        log.debug("Request to save ProductPurpose : {}", productPurposeDTO);
        ProductPurpose productPurpose = productPurposeMapper.toEntity(productPurposeDTO);
        productPurpose = productPurposeRepository.save(productPurpose);
        return productPurposeMapper.toDto(productPurpose);
    }

    @Override
    public Optional<ProductPurposeDTO> partialUpdate(ProductPurposeDTO productPurposeDTO) {
        log.debug("Request to partially update ProductPurpose : {}", productPurposeDTO);

        return productPurposeRepository
            .findById(productPurposeDTO.getId())
            .map(existingProductPurpose -> {
                productPurposeMapper.partialUpdate(existingProductPurpose, productPurposeDTO);

                return existingProductPurpose;
            })
            .map(productPurposeRepository::save)
            .map(productPurposeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductPurposeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProductPurposes");
        return productPurposeRepository.findAll(pageable).map(productPurposeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductPurposeDTO> findOne(Long id) {
        log.debug("Request to get ProductPurpose : {}", id);
        return productPurposeRepository.findById(id).map(productPurposeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete ProductPurpose : {}", id);
        productPurposeRepository.deleteById(id);
    }
}
