package pbm.com.exchange.service.impl;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pbm.com.exchange.app.rest.request.AuctionReq;
import pbm.com.exchange.app.rest.respone.GetAuctionRes;
import pbm.com.exchange.domain.Auction;
import pbm.com.exchange.domain.Product;
import pbm.com.exchange.domain.Profile;
import pbm.com.exchange.exception.BadRequestException;
import pbm.com.exchange.framework.handler.ExistenceHandler;
import pbm.com.exchange.message.Message;
import pbm.com.exchange.message.MessageHelper;
import pbm.com.exchange.repository.AuctionRepository;
import pbm.com.exchange.repository.ProductRepository;
import pbm.com.exchange.service.AuctionService;
import pbm.com.exchange.service.ProfileService;
import pbm.com.exchange.service.dto.AuctionDTO;
import pbm.com.exchange.service.mapper.AuctionMapper;

/**
 * Service Implementation for managing {@link Auction}.
 */
@Service
@Transactional
public class AuctionServiceImpl implements AuctionService {

    private final Logger log = LoggerFactory.getLogger(AuctionServiceImpl.class);

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private AuctionMapper auctionMapper;

    @Autowired
    private ExistenceHandler existenceHandler;

    @Override
    public AuctionDTO save(AuctionDTO auctionDTO) {
        log.debug("Request to save Auction : {}", auctionDTO);
        Auction auction = auctionMapper.toEntity(auctionDTO);
        auction = auctionRepository.save(auction);
        return auctionMapper.toDto(auction);
    }

    @Override
    public Optional<AuctionDTO> partialUpdate(AuctionDTO auctionDTO) {
        log.debug("Request to partially update Auction : {}", auctionDTO);

        return auctionRepository
            .findById(auctionDTO.getId())
            .map(existingAuction -> {
                auctionMapper.partialUpdate(existingAuction, auctionDTO);

                return existingAuction;
            })
            .map(auctionRepository::save)
            .map(auctionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuctionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Auctions");
        return auctionRepository.findAll(pageable).map(auctionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuctionDTO> findOne(Long id) {
        log.debug("Request to get Auction : {}", id);
        return auctionRepository.findById(id).map(auctionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Auction : {}", id);
        auctionRepository.deleteById(id);
    }

    @Override
    public void auctionProduct(AuctionReq auctionReq) {
        Optional<Product> productOptional = productRepository.findById(auctionReq.getProductId());
        if (!productOptional.isPresent()) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0071, auctionReq.getProductId()), new Throwable());
        }
        Product product = productOptional.get();

        Double auctionPoint = auctionReq.getAuctionPoint();
        // check auction point > current point
        if (auctionPoint <= product.getCurrentPoint()) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0095), new Throwable());
        }

        // save data
        product.setCurrentPoint(auctionPoint);
        product = productRepository.save(product);

        // get current profile
        Profile profile = profileService.getCurrentProfile();

        Auction auction = Auction.builder().point(auctionPoint).product(product).profile(profile).createdDate(ZonedDateTime.now()).build();
        auctionRepository.save(auction);
    }

    @Override
    public List<GetAuctionRes> findByProductId(Pageable pageable, Long productId) {
        Product product = existenceHandler.getProductFromId(productId);
        List<Auction> auctionList = auctionRepository.findByProduct(product, pageable);

        // get list of auction response
        List<GetAuctionRes> auctionListRes = new ArrayList<>();
        GetAuctionRes auctionRes = null;
        for (Auction auction : auctionList) {
            auctionRes =
                GetAuctionRes
                    .builder()
                    .auctionId(auction.getId())
                    .auctionPoint(auction.getPoint())
                    .auctionTime(auction.getCreatedDate())
                    .displayName(auction.getProfile().getDisplayName())
                    .avatar(auction.getProfile().getAvatar())
                    .build();
            auctionListRes.add(auctionRes);
        }
        return auctionListRes;
    }
}
