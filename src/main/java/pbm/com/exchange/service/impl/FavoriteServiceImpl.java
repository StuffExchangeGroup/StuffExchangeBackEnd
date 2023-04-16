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
import pbm.com.exchange.app.rest.respone.WishProductRes;
import pbm.com.exchange.domain.Favorite;
import pbm.com.exchange.domain.Product;
import pbm.com.exchange.domain.Profile;
import pbm.com.exchange.domain.User;
import pbm.com.exchange.exception.BadRequestException;
import pbm.com.exchange.message.Message;
import pbm.com.exchange.message.MessageHelper;
import pbm.com.exchange.repository.FavoriteRepository;
import pbm.com.exchange.repository.ProductRepository;
import pbm.com.exchange.repository.ProfileRepository;
import pbm.com.exchange.service.FavoriteService;
import pbm.com.exchange.service.UserService;
import pbm.com.exchange.service.dto.FavoriteDTO;
import pbm.com.exchange.service.mapper.FavoriteMapper;

/**
 * Service Implementation for managing {@link Favorite}.
 */
@Service
@Transactional
public class FavoriteServiceImpl implements FavoriteService {

    private final Logger log = LoggerFactory.getLogger(FavoriteServiceImpl.class);

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    public FavoriteDTO save(FavoriteDTO favoriteDTO) {
        log.debug("Request to save Favorite : {}", favoriteDTO);
        Favorite favorite = favoriteMapper.toEntity(favoriteDTO);
        favorite = favoriteRepository.save(favorite);
        return favoriteMapper.toDto(favorite);
    }

    @Override
    public Optional<FavoriteDTO> partialUpdate(FavoriteDTO favoriteDTO) {
        log.debug("Request to partially update Favorite : {}", favoriteDTO);

        return favoriteRepository
            .findById(favoriteDTO.getId())
            .map(existingFavorite -> {
                favoriteMapper.partialUpdate(existingFavorite, favoriteDTO);

                return existingFavorite;
            })
            .map(favoriteRepository::save)
            .map(favoriteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FavoriteDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Favorites");
        return favoriteRepository.findAll(pageable).map(favoriteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FavoriteDTO> findOne(Long id) {
        log.debug("Request to get Favorite : {}", id);
        return favoriteRepository.findById(id).map(favoriteMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Favorite : {}", id);
        favoriteRepository.deleteById(id);
    }

    @Override
    public Boolean isFavouriteExists(Long productId) {
        User currentUser = userService.getCurrentUser().get();
        Profile profile = profileRepository.findOneByUser(currentUser).get();

        // get product
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0071), new Throwable());
        }
        Product product = productOptional.get();

        Favorite favorite = favoriteRepository.findByProductAndProfile(product, profile);
        if (favorite == null) {
            favorite = new Favorite();
            ZonedDateTime currentDate = ZonedDateTime.now();
            favorite.setProduct(product);
            favorite.setProfile(profile);
            favorite.setCreatedDate(currentDate);
            favoriteRepository.save(favorite);

            //increase favorite count
            product.setFavoriteCount(product.getFavoriteCount() + 1);
            productRepository.save(product);

            return true;
        } else {
            favoriteRepository.delete(favorite);

            //decrease favorite count
            product.setFavoriteCount(product.getFavoriteCount() - 1);
            productRepository.save(product);

            return false;
        }
    }

    @Override
    public List<WishProductRes> getWishList(Pageable pageable) {
        List<WishProductRes> wishList = new ArrayList<>();
        WishProductRes productRes = null;
        User currentUser = userService.getCurrentUser().get();
        Profile profile = profileRepository.findOneByUser(currentUser).get();

        List<Favorite> favorites = favoriteRepository.findByProfile(profile, pageable);

        for (Favorite favorite : favorites) {
            Product product = productRepository.getById(favorite.getProduct().getId());
            Profile ownerProfile = profileRepository.getById(product.getProfile().getId());

            productRes = new WishProductRes();
            productRes.setId(product.getId());
            productRes.setTitle(product.getName());
            productRes.setThumbnail(product.getThumbnail());
            productRes.setDisplayName(ownerProfile.getDisplayName());
            productRes.setAvatar(ownerProfile.getAvatar());
            productRes.setIsFavorite(true);
            wishList.add(productRes);
        }

        return wishList;
    }
}
