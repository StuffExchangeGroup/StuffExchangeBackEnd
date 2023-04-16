package pbm.com.exchange.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pbm.com.exchange.domain.Purchase;
import pbm.com.exchange.repository.PurchaseRepository;
import pbm.com.exchange.service.PurchaseService;
import pbm.com.exchange.service.dto.PurchaseDTO;
import pbm.com.exchange.service.mapper.PurchaseMapper;

/**
 * Service Implementation for managing {@link Purchase}.
 */
@Service
@Transactional
public class PurchaseServiceImpl implements PurchaseService {

    private final Logger log = LoggerFactory.getLogger(PurchaseServiceImpl.class);

    private final PurchaseRepository purchaseRepository;

    private final PurchaseMapper purchaseMapper;

    public PurchaseServiceImpl(PurchaseRepository purchaseRepository, PurchaseMapper purchaseMapper) {
        this.purchaseRepository = purchaseRepository;
        this.purchaseMapper = purchaseMapper;
    }

    @Override
    public PurchaseDTO save(PurchaseDTO purchaseDTO) {
        log.debug("Request to save Purchase : {}", purchaseDTO);
        Purchase purchase = purchaseMapper.toEntity(purchaseDTO);
        purchase = purchaseRepository.save(purchase);
        return purchaseMapper.toDto(purchase);
    }

    @Override
    public Optional<PurchaseDTO> partialUpdate(PurchaseDTO purchaseDTO) {
        log.debug("Request to partially update Purchase : {}", purchaseDTO);

        return purchaseRepository
            .findById(purchaseDTO.getId())
            .map(existingPurchase -> {
                purchaseMapper.partialUpdate(existingPurchase, purchaseDTO);

                return existingPurchase;
            })
            .map(purchaseRepository::save)
            .map(purchaseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Purchases");
        return purchaseRepository.findAll(pageable).map(purchaseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PurchaseDTO> findOne(Long id) {
        log.debug("Request to get Purchase : {}", id);
        return purchaseRepository.findById(id).map(purchaseMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Purchase : {}", id);
        purchaseRepository.deleteById(id);
    }
}
