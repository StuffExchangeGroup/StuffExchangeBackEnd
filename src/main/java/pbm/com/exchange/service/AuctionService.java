package pbm.com.exchange.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pbm.com.exchange.app.rest.request.AuctionReq;
import pbm.com.exchange.app.rest.respone.GetAuctionRes;
import pbm.com.exchange.service.dto.AuctionDTO;

/**
 * Service Interface for managing {@link pbm.com.exchange.domain.Auction}.
 */
public interface AuctionService {
    /**
     * Save a auction.
     *
     * @param auctionDTO the entity to save.
     * @return the persisted entity.
     */
    AuctionDTO save(AuctionDTO auctionDTO);

    /**
     * Partially updates a auction.
     *
     * @param auctionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AuctionDTO> partialUpdate(AuctionDTO auctionDTO);

    /**
     * Get all the auctions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AuctionDTO> findAll(Pageable pageable);

    /**
     * Get the "id" auction.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AuctionDTO> findOne(Long id);

    /**
     * Delete the "id" auction.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * make new product auction
     *
     * @param auctionReq
     */
    void auctionProduct(AuctionReq auctionReq);

    /**
     * get auction list from productId
     *
     * @param pageable
     * @param productId
     * @return List<GetAuctionRes>
     */
    List<GetAuctionRes> findByProductId(Pageable pageable, Long productId);
}
