package pbm.com.exchange.service.impl;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pbm.com.exchange.app.rest.request.PostProductReq;
import pbm.com.exchange.app.rest.request.UpdateProductReq;
import pbm.com.exchange.app.rest.respone.FilterProductRes;
import pbm.com.exchange.app.rest.respone.GetProductRes;
import pbm.com.exchange.app.rest.respone.MyProductRes;
import pbm.com.exchange.app.rest.respone.PaginateRes;
import pbm.com.exchange.app.rest.respone.SimilarProductRes;
import pbm.com.exchange.app.rest.vm.CriteriaDTO;
import pbm.com.exchange.app.rest.vm.FilterDTO;
import pbm.com.exchange.domain.AppConfiguration;
import pbm.com.exchange.domain.Category;
import pbm.com.exchange.domain.City;
import pbm.com.exchange.domain.Favorite;
import pbm.com.exchange.domain.File;
import pbm.com.exchange.domain.Image;
import pbm.com.exchange.domain.Product;
import pbm.com.exchange.domain.ProductCategory;
import pbm.com.exchange.domain.ProductPurpose;
import pbm.com.exchange.domain.Product_;
import pbm.com.exchange.domain.Profile;
import pbm.com.exchange.domain.Province;
import pbm.com.exchange.domain.User;
import pbm.com.exchange.domain.enumeration.Condition;
import pbm.com.exchange.domain.enumeration.ProductStatus;
import pbm.com.exchange.domain.enumeration.ProductType;
import pbm.com.exchange.domain.enumeration.PurposeType;
import pbm.com.exchange.domain.enumeration.QueryOperator;
import pbm.com.exchange.domain.enumeration.Status;
import pbm.com.exchange.exception.BadRequestException;
import pbm.com.exchange.message.Message;
import pbm.com.exchange.message.MessageHelper;
import pbm.com.exchange.repository.AppConfigurationRepository;
import pbm.com.exchange.repository.CategoryRepository;
import pbm.com.exchange.repository.CityRepository;
import pbm.com.exchange.repository.ExchangeRepository;
import pbm.com.exchange.repository.FavoriteRepository;
import pbm.com.exchange.repository.FileRepository;
import pbm.com.exchange.repository.ImageRepository;
import pbm.com.exchange.repository.ProductCategoryRepository;
import pbm.com.exchange.repository.ProductPurposeRepository;
import pbm.com.exchange.repository.ProductRepository;
import pbm.com.exchange.repository.ProfileRepository;
import pbm.com.exchange.service.ProductService;
import pbm.com.exchange.service.ProfileService;
import pbm.com.exchange.service.UserService;
import pbm.com.exchange.service.dto.ProductDTO;
import pbm.com.exchange.service.mapper.PostProductMapper;
import pbm.com.exchange.service.mapper.ProductMapper;
import pbm.com.exchange.specifications.ProductSpecification;
import pbm.com.exchange.specifications.model.Filter;

/**
 * Service Implementation for managing {@link Product}.
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final String FEATURED_KEY = "TOP_CATEGORY";

    @Autowired
    ProductCategoryRepository productCategoryRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    FavoriteRepository favoriteRepository;

    @Autowired
    ExchangeRepository exchangeRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    CityRepository cityRepository;

    @Autowired
    AppConfigurationRepository appConfigurationRepository;

    @Autowired
    ProductPurposeRepository productPurposeRepository;

    @Autowired
    UserService userService;
    
    @Autowired
    ProfileService profileService;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    PostProductMapper postProductMapper;

    @Override
    public ProductDTO save(ProductDTO productDTO) {
        log.debug("Request to save Product : {}", productDTO);
        Product product = productMapper.toEntity(productDTO);
        product = productRepository.save(product);
        return productMapper.toDto(product);
    }

    @Override
    public Optional<ProductDTO> partialUpdate(ProductDTO productDTO) {
        log.debug("Request to partially update Product : {}", productDTO);

        return productRepository
            .findById(productDTO.getId())
            .map(existingProduct -> {
                productMapper.partialUpdate(existingProduct, productDTO);

                return existingProduct;
            })
            .map(productRepository::save)
            .map(productMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Products");
        return productRepository.findAll(pageable).map(productMapper::toDto);
    }

    public Page<ProductDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productRepository.findAllWithEagerRelationships(pageable).map(productMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductDTO> findOne(Long id) {
        log.debug("Request to get Product : {}", id);
        return productRepository.findOneWithEagerRelationships(id).map(productMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Product : {}", id);
        productRepository.deleteById(id);
    }

    @Override
    public GetProductRes getProductDetail(Long productId) {
        log.debug("Request to get product detail with id: " + productId);

        Optional<Product> productOptional = productRepository.findById(productId);
        // Not found product
        if (!productOptional.isPresent()) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0071, productId), new Throwable());
        }

        Product product = productOptional.get();
        // find image links of product
        List<Image> imageProducts = imageRepository.findByProduct(product);
        List<String> imageLinks = new ArrayList<>();
        if (!imageProducts.isEmpty()) {
            for (Image image : imageProducts) {
                imageLinks.add(image.getLink());
            }
        }
        // find all categories
        List<ProductCategory> productCategories = productCategoryRepository.findByProduct(product);
        List<Category> categorieProducts = new ArrayList<Category>();
        if (!productCategories.isEmpty()) {
            for (ProductCategory productCategory : productCategories) {
                categorieProducts.add(categoryRepository.findById(productCategory.getCategory().getId()).get());
            }
        }
        // find profile, city, province
        Profile profile = profileRepository.findById(product.getProfile().getId()).get();
        City city = product.getCity();
        Province province = city.getProvince();

        // check isFavorite currentUser
        boolean isFavourite = false;
        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isPresent()) {
            Profile currentProfile = profileRepository.findOneByUser(currentUser.get()).get();
            boolean checkExistFavourite = favoriteRepository.existsByProductAndProfile(product, currentProfile);
            isFavourite = checkExistFavourite ? true : false;
        }

        // set values
        GetProductRes productDTO = new GetProductRes();
        productDTO = productMapper.toDTO(product);
        productDTO.setCategoryId(categorieProducts.get(0).getId());
        productDTO.setCategoryName(categorieProducts.get(0).getName());
        productDTO.setProfileId(profile.getId());
        productDTO.setAvatar(profile.getAvatar());
        productDTO.setIsFavorite(isFavourite);
        productDTO.setDisplayName(profile.getDisplayName());
        productDTO.setStateId(province.getId());
        productDTO.setStateName(province.getName());
        productDTO.setCityId(city.getId());
        productDTO.setCityName(city.getName());
        productDTO.setImageLinks(imageLinks);
        productDTO.setProductCondition(product.getCondition());
//        productDTO.setIsGift(product.getIsGift());
        productDTO.setIsExchange(product.getIsExchange());

        return productDTO;
    }

    @Override
    public PostProductReq save(PostProductReq postProductReq, HttpServletRequest request) {
        Optional<Category> categoryOptional = categoryRepository.findById(postProductReq.getCategoryId());
        Optional<City> cityOptional = cityRepository.findById(postProductReq.getCityId());
        String[] linkImageProduct = null;

        // validate category, city and user are not null
        if (!categoryOptional.isPresent()) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0066, postProductReq.getCategoryId()), new Throwable());
        }
        if (!cityOptional.isPresent()) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0085, postProductReq.getCityId()), new Throwable());
        }
        if (!userService.getCurrentUser().isPresent()) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0088), new Throwable());
        }

        // create set ProductPurpose for Product
        Set<ProductPurpose> productPurposes = new HashSet<>();

        for (Long productPurposeId : postProductReq.getPurposeIds()) {
            Optional<ProductPurpose> productPurposeOptional = productPurposeRepository.findById(productPurposeId);
            // validate category purpose
            if (!productPurposeOptional.isPresent()) {
                throw new BadRequestException(
                    MessageHelper.getMessage(Message.Keys.E0098, postProductReq.getCategoryId()),
                    new Throwable()
                );
            }
            ProductPurpose productPurpose = productPurposeOptional.get();

            if (productPurpose.getName() == PurposeType.GIFT) {
                productPurposes.clear();
                productPurposes.add(productPurpose);
                break;
            }

            productPurposes.add(productPurpose);
        }

        Category category = categoryOptional.get();
        City city = cityOptional.get();
        User currentUser = userService.getCurrentUser().get();
        Profile profile = profileRepository.findOneByUser(currentUser).get();

        // save new product
        Product newProduct = postProductMapper.toEntity(postProductReq);
        newProduct.setCity(city);
        newProduct.setProfile(profile);
        newProduct.setRequestCount(0);
        newProduct.setReceiveCount(0);
        newProduct.setFavoriteCount(0);
        newProduct.setCondition(postProductReq.getCondition());
        newProduct.setPurposes(productPurposes);
        newProduct.setIsBlock(true);
        
        // set purpose for product
//        newProduct.setIsGift(false);
//        newProduct.setIsSell(false);
//        newProduct.setIsAuction(false);
//        newProduct.setIsExchange(true);
//        newProduct.setIsAuctionNow(false);
        newProduct.setIsSwapAvailable(true);
        newProduct.status(ProductStatus.AVAILABLE);
//        for (ProductPurpose productPurpose : productPurposes) {
//            // the purpose of product either Gift or (Auction, Exchange, Sell)
//            switch (productPurpose.getName()) {
//                case AUCTION:
//                    // set default startAuctionTime
//                    if (postProductReq.getStartAuctionTime() == null) {
//                        postProductReq.setStartAuctionTime(ZonedDateTime.now());
//                    }
//
//                    // Auction Product  must have endAuctionTime
//                    if (postProductReq.getEndAuctionTime() == null) {
//                        throw new BadRequestException(
//                            MessageHelper.getMessage(Message.Keys.E0096, postProductReq.getCategoryId()),
//                            new Throwable()
//                        );
//                    }
//                    // check startAuctionTime must be less than endAuctionTime if Auction
//                    if (postProductReq.getStartAuctionTime().isAfter(postProductReq.getEndAuctionTime())) {
//                        throw new BadRequestException(
//                            MessageHelper.getMessage(Message.Keys.E0097, postProductReq.getCategoryId()),
//                            new Throwable()
//                        );
//                    }
//
//                    // check auction point is not null
//                    if (postProductReq.getAuctionPoint() == null) {
//                        throw new BadRequestException(MessageHelper.getMessage("Auction point is not null"), new Throwable());
//                    }
//
//                    // if product is Auction right now
//                    if (
//                        postProductReq.getStartAuctionTime().isBefore(ZonedDateTime.now()) ||
//                        postProductReq.getStartAuctionTime().isEqual(ZonedDateTime.now())
//                    ) {
//                        newProduct.setIsAuctionNow(true);
//                    }
//                    newProduct.setIsAuction(true);
//                    newProduct.setCurrentPoint(0.0);
//                    newProduct.setAuctionPoint(postProductReq.getAuctionPoint());
//                    newProduct.setStartAuctionTime(postProductReq.getStartAuctionTime());
//                    newProduct.setEndAuctionTime(postProductReq.getEndAuctionTime());
//                    break;
//                case EXCHANGE:
//                    newProduct.setIsExchange(true);
//                    newProduct.setIsSwapAvailable(true);
//                    newProduct.status(ProductStatus.AVAILABLE);
//                    break;
//                case SELL:
//                    // check sell point is not null
//                    if (postProductReq.getSellPoint() == null) {
//                        throw new BadRequestException(MessageHelper.getMessage("Sell point is not null"), new Throwable());
//                    }
//                    newProduct.setSalePoint(postProductReq.getSellPoint());
//                    newProduct.setIsSell(true);
//                    break;
//                case GIFT:
//                    newProduct.setIsGift(true);
//                    break;
//            }
//        }

        newProduct = productRepository.save(newProduct);

        if (postProductReq.getImageIds() != null) {
            linkImageProduct = new String[postProductReq.getImageIds().length];
            int i = 0;
            String linkImageDefault = null;
            for (String imageFileId : postProductReq.getImageIds()) {
                try {
                    Long imageId = Long.parseLong(imageFileId);
                    File file = fileRepository.findById(imageId).get();

                    // link of image
                    String urlImage = request.getRequestURL().toString();
                    String uriImage = request.getRequestURI();
                    urlImage = urlImage.substring(0, urlImage.indexOf(uriImage));

                    String link = urlImage + "/api/app/image/download/" + file.getId();

                    linkImageProduct[i] = link;
                    if (i == 0) {
                        linkImageDefault = link;
                    }
                    i++;

                    // save image of product
                    Image newImage = new Image();
                    newImage.setProduct(newProduct);
                    newImage.setImageFile(file);
                    newImage.setLink(link);
                    newImage.setText(file.getFileName());
                    imageRepository.save(newImage);
                } catch (NumberFormatException e) {
                    log.error(e.getMessage());
                }
            }
            newProduct.setThumbnail(linkImageDefault);
            productRepository.save(newProduct);
        }

        // save ProductCategory
        ProductCategory productCategory = new ProductCategory();
        productCategory.setProduct(newProduct);
        productCategory.setCategory(category);
        productCategory = productCategoryRepository.save(productCategory);

        postProductReq.setId(newProduct.getId());
        postProductReq.setImageIds(linkImageProduct);

        return postProductReq;
    }

    @Override
    public boolean deleteProduct(Long productId) {
        log.debug("Request to delete product with id: " + productId);

        Optional<Product> productOptional = productRepository.findById(productId);
        // Not found product
        if (!productOptional.isPresent()) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0071, productId), new Throwable());
        }

        Product product = productOptional.get();

        boolean isExitExchangeAvailable = exchangeRepository.existsBySendProductOrReceiveProduct(product, product);

        if (isExitExchangeAvailable) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0084, productOptional.get().getName()), new Throwable());
        }

        favoriteRepository.deleteByProduct(product);
        imageRepository.deleteByProduct(product);
        productCategoryRepository.deleteByProduct(product);
        productRepository.delete(product);

        return true;
    }

    @Override
    public List<MyProductRes> findMyProducts(Pageable pageable, ProductStatus status) {
        log.debug("Request to get my products");
        Pageable pageable2 = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdDate").descending());
        User currentUser = userService.getCurrentUser().get();
        Profile profile = profileRepository.findOneByUser(currentUser).get();
        List<Product> myProducts = new ArrayList<Product>();
        if (status != null) {
            switch (status) {
                case AVAILABLE:
                    myProducts = productRepository.findByProfileAndStatus(profile, ProductStatus.AVAILABLE, pageable2);
                    break;
                case SWAPPED:
                    myProducts = productRepository.findByProfileAndStatus(profile, ProductStatus.SWAPPED, pageable2);
                    break;
                default:
                    myProducts = productRepository.findByProfile(profile, pageable2);
                    break;
            }
        } else {
            myProducts = productRepository.findByProfile(profile, pageable2);
        }

        if (myProducts == null) {
            return null;
        }
        List<MyProductRes> myProductDTOs = new ArrayList<MyProductRes>();
        for (Product myProduct : myProducts) {
            MyProductRes myProductDTO = new MyProductRes();
            myProductDTO.setId(myProduct.getId());
            myProductDTO.setTitle(myProduct.getName());
            myProductDTO.setThumbnail(myProduct.getThumbnail());
            myProductDTO.setDescription(myProduct.getDescription());
            myProductDTO.setIsSwapAvailable(myProduct.getIsSwapAvailable());
            myProductDTO.setCreatedDate(myProduct.getCreatedDate());
            myProductDTOs.add(myProductDTO);
        }

        return myProductDTOs;
    }

    @Override
    public List<SimilarProductRes> getSimilarProducts(Long productId) {
        log.debug("Request to get similars product");
        Optional<Product> productOptional = productRepository.findById(productId);

        if (!productOptional.isPresent()) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0071, productId), new Throwable());
        }

        Product product = productOptional.get();
        List<ProductCategory> productCategories = productCategoryRepository.findByProduct(product);
        Collection<Long> categoryIds = null;
        String categoryName = null;
        if (!productCategories.isEmpty()) {
            categoryIds = new HashSet<>();
            for (ProductCategory productCategory : productCategories) {
                categoryIds.add(productCategory.getCategory().getId());
                if (categoryName == null) {
                    categoryName = productCategory.getCategory().getName();
                }
            }
        }

        List<Long> wishlish = null;
        // default profileId if user has not login
        Long profileId = 0L;
        try {
            Optional<User> currentUser = userService.getCurrentUser();
            if (currentUser.isPresent()) {
                wishlish = this.getWishlistFromUser(currentUser.get());
            }
            Profile profile = profileRepository.findOneByUser(currentUser.get()).get();
            profileId = profile.getId();
        } catch (Exception e) {
            log.warn("not found current user ");
        }

        List<Product> similarProducts = productRepository.findSimilarProducts(categoryIds, product.getId(), profileId);
        List<SimilarProductRes> SimilarProductRess = new ArrayList<SimilarProductRes>();

        for (Product similarProduct : similarProducts) {
            // set is favorite
            boolean isFavorite = false; // set default
            if (wishlish != null && wishlish.contains(similarProduct.getId())) {
                isFavorite = true;
            }

            SimilarProductRes similarProductDTO = SimilarProductRes
                .builder()
                .id(similarProduct.getId())
                .productName(similarProduct.getName())
                .isFavorite(isFavorite)
                .productCondition(similarProduct.getCondition())
                .thumbnail(similarProduct.getThumbnail())
                .updatedDate(similarProduct.getLastModifiedDate())
                .createdDate(similarProduct.getCreatedDate())
                .displayName(similarProduct.getProfile().getDisplayName())
                .categoryId(categoryIds.stream().findFirst().orElse(null))
                .categoryName(categoryName)
                .avatar(similarProduct.getProfile().getAvatar())
                .isExchange(similarProduct.getIsExchange())
//                .isGift(similarProduct.getIsGift())
//                .isSell(similarProduct.getIsSell())
//                .isAuction(similarProduct.getIsAuction())
                .build();

            SimilarProductRess.add(similarProductDTO);
        }
        return SimilarProductRess;
    }

    @Override
    public UpdateProductReq updateProduct(UpdateProductReq updateProductReq, HttpServletRequest request) {
        if (updateProductReq.getId() == null || !productRepository.findById(updateProductReq.getId()).isPresent()) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0086), new Throwable());
        }

        Product product = productRepository.findById(updateProductReq.getId()).get();
        productMapper.partialUpdate(product, updateProductReq);
        product.setLastModifiedDate(ZonedDateTime.now());
        product.setCondition(updateProductReq.getCondition());

        // update category
        if (updateProductReq.getCategoryId() != null) {
            Optional<Category> categoryOptional = categoryRepository.findById(updateProductReq.getCategoryId());
            if (!categoryOptional.isPresent()) {
                throw new BadRequestException(
                    MessageHelper.getMessage(Message.Keys.E0066, updateProductReq.getCategoryId()),
                    new Throwable()
                );
            }
            List<ProductCategory> list = productCategoryRepository.findByProduct(product);
            // relationship between product and category is n-1 -> so we only get first
            // record to update
            ProductCategory productCategory = list.get(0);
            productCategory.setCategory(categoryOptional.get());

            productCategoryRepository.save(productCategory);
        }

        // update city
        if (updateProductReq.getCityId() != null) {
            Optional<City> cityOptional = cityRepository.findById(updateProductReq.getCityId());
            if (!cityOptional.isPresent()) {
                throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0085, updateProductReq.getCityId()), new Throwable());
            }
            product.setCity(cityOptional.get());
        }

        // update purpose
        Set<ProductPurpose> productPurposes = new HashSet<>();
        if (!updateProductReq.getPurposeIds().isEmpty()) {
            // create set ProductPurpose for Product
            for (Long productPurposeId : updateProductReq.getPurposeIds()) {
                Optional<ProductPurpose> productPurposeOptional = productPurposeRepository.findById(productPurposeId);
                // validate category purpose
                if (!productPurposeOptional.isPresent()) {
                    throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0098, productPurposeId), new Throwable());
                }
                ProductPurpose productPurpose = productPurposeOptional.get();

                if (productPurpose.getName() == PurposeType.GIFT) {
                    productPurposes.clear();
                    productPurposes.add(productPurpose);
                    break;
                }

                productPurposes.add(productPurpose);
            }

            product.setPurposes(productPurposes);
        }

        for (ProductPurpose productPurpose : productPurposes) {
            // the purpose of product either Gift or (Auction, Exchange, Sell)
            switch (productPurpose.getName()) {
                case EXCHANGE:
//                    product.setIsGift(false);
                    product.setIsExchange(true);
                    break;
                case GIFT:
//                    product.setIsGift(true);
                    product.setIsExchange(false);
//                    product.setIsAuction(false);
//                    product.setIsSell(false);
                    break;
                default:
                    break;
            }
        }
        // update images
        if (updateProductReq.getImageIds() != null) {
            // delete old images
            imageRepository.deleteByProduct(product);
            imageRepository.flush();

            // update new images
            String[] imageLinks = new String[updateProductReq.getImageIds().length];
            int i = 0;
            for (String fileId : updateProductReq.getImageIds()) {
                try {
                    Long imageId = Long.parseLong(fileId);
                    File file = fileRepository.findById(imageId).get();

                    // create a link of image
                    String urlImage = request.getRequestURL().toString();
                    String uriImage = request.getRequestURI();
                    urlImage = urlImage.substring(0, urlImage.indexOf(uriImage));
                    String link = urlImage + "/api/app/image/download/" + file.getId();

                    // create a list of image links
                    imageLinks[i++] = link;

                    // save image of product
                    Image newImage = new Image();
                    newImage.setProduct(product);
                    newImage.setImageFile(file);
                    newImage.setLink(link);
                    newImage.setText(file.getFileName());
                    imageRepository.save(newImage);
                } catch (NumberFormatException e) {
                    log.error(e.getMessage());
                }

                // set thumbnail
                if (imageLinks.length > 0) {
                    product.setThumbnail(imageLinks[0]);
                    updateProductReq.setImageIds(imageLinks);
                }
            }
        } else {
            // set list of old image links to productReq
            List<Image> images = imageRepository.findByProduct(product);
            String[] imageLinks = new String[images.size()];
            for (int i = 0; i < images.size(); i++) {
                imageLinks[i] = images.get(i).getLink();
            }

            updateProductReq.setImageIds(imageLinks);
        }

        product = productRepository.save(product);
        productMapper.partialUpdate(updateProductReq, product);
        updateProductReq.setCityId(product.getCity().getId());

        // set category id to productReq
        List<ProductCategory> list = productCategoryRepository.findByProduct(product);
        updateProductReq.setCategoryId(list.get(0).getCategory().getId());

        return updateProductReq;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProductDTO> filterProducts(Pageable pageable, CriteriaDTO criteriaDTO) {
        Pageable newPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        List<Filter> filters = new ArrayList<>();
        //filters for search product
        List<Filter> searchFilters = new ArrayList<>();
        //filters for search product by type FEATURED
        List<Filter> featuredFilters = new ArrayList<>();

        // filter by status : AVAILABLE
        filters = filterByStatus(filters, Status.AVAILABLE);
        
        filters = filterByIsBlock(filters);

        // filter by search name
        if (criteriaDTO.getSearch() != null) {
            searchFilters = filterBySearchName(searchFilters, criteriaDTO.getSearch());
        }

        // filter by category
        if (criteriaDTO.getCategoryId() != null) {
            filters = filterByCategory(filters, criteriaDTO.getCategoryId());
        }

        // filter by product type: NEWEST, FAVORITE, EXPLORE, FEATURED
        if (criteriaDTO.getType() != null) {
            if (criteriaDTO.getType() == ProductType.FEATURED) {
                Map<String, Object> map = filterByType(featuredFilters, criteriaDTO.getType(), newPageable);
                featuredFilters = (ArrayList<Filter>) map.get("filters");
                newPageable = (Pageable) map.get("pageable");
            } else {
                Map<String, Object> map = filterByType(filters, criteriaDTO.getType(), newPageable);
                filters = (ArrayList<Filter>) map.get("filters");
                newPageable = (Pageable) map.get("pageable");
            }
        }

        // filter by condition : LIKE, LIKENEW, USED
        if (criteriaDTO.getCondition() != null) {
            filters = filterByCondition(filters, criteriaDTO.getCondition());
        }

        // filter by purpose type: SWAP, SELL, AUCTION, GIFT
        if (criteriaDTO.getPurposeType() != null) {
            filters = filterByPurpose(filters, criteriaDTO.getPurposeType());
        }

        // filter by location
        if (criteriaDTO.getLocation() != null) {
            filters = filterByLocation(filters, criteriaDTO.getLocation());
        }

        // ignore products of current user
        if (userService.getCurrentUser().isPresent()) {
            filters = filterToIgnoreProductOfUser(filters);
        }

        // get specification and find products
        Specification<Product> specification = getSpecification(filters, searchFilters, featuredFilters);
        List<Product> products = productRepository.findAll(specification, newPageable).getContent();

        // update category name and display name and union
        List<ProductDTO> productDtos = new ArrayList<>();
        Optional<User> currentUser = userService.getCurrentUser();
        List<Long> wishlish = null;
        if (currentUser.isPresent()) {
            wishlish = this.getWishlistFromUser(currentUser.get());
        }

        try {
            for (Product product : products) {
                ProductDTO productDTO = productMapper.toDto(product);
                productDTO.getProfile().setDisplayName(product.getProfile().getDisplayName());
                productDTO.getProfile().setAvatar(product.getProfile().getAvatar());

                // find category
                List<ProductCategory> productCategories = productCategoryRepository.findByProduct(product);
                List<Category> categorieProducts = new ArrayList<Category>();
                if (!productCategories.isEmpty()) {
                    for (ProductCategory productCategory : productCategories) {
                        categorieProducts.add(categoryRepository.findById(productCategory.getCategory().getId()).get());
                    }
                }
                productDTO.setCategoryName(categorieProducts.get(0).getName());

                // set is favorite
                productDTO.setIsFavorite(false); // set default
                if (wishlish != null && wishlish.contains(product.getId())) {
                    productDTO.setIsFavorite(true);
                }

                productDtos.add(productDTO);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return productDtos;
    }

    @Override
    public List<Filter> filterBySearchName(List<Filter> filters, String search) {
        // search in product title
        Filter filter = new Filter();
        filter.setField(Product_.NAME);
        filter.setOperator(QueryOperator.LIKE);
        filter.setValue(search);
        filters.add(filter);

        // search in product description
        filter = new Filter();
        filter.setField(Product_.DESCRIPTION);
        filter.setOperator(QueryOperator.LIKE);
        filter.setValue(search);
        filters.add(filter);

        // search in category name
        List<Category> categories = categoryRepository.findByNameContainingIgnoreCase(search);
        List<String> productIds = getProductIdsFromCategories(categories);
        filter = new Filter();
        filter.setField(Product_.ID);
        filter.setOperator(QueryOperator.IN);
        filter.setValues(productIds);
        filters.add(filter);

        return filters;
    }

    @Override
    public List<String> getProductIdsFromCategories(List<Category> categories) {
        List<String> productIds = new ArrayList<>();
        for (Category cate : categories) {
            List<ProductCategory> productCategories = productCategoryRepository.findByCategory(cate);

            for (ProductCategory productCategory : productCategories) {
                productIds.add(String.valueOf(productCategory.getProduct().getId()));
            }
        }
        return productIds;
    }

    @Override
    public List<Filter> filterByCategory(List<Filter> filters, Long categoryId) {
        // not found category
        if (!categoryRepository.findById(categoryId).isPresent()) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0066, categoryId), new Throwable());
        }

        List<String> productIds = new ArrayList<>();
        Category category = categoryRepository.findById(categoryId).get();
        List<ProductCategory> productCategories = productCategoryRepository.findByCategory(category);
        for (ProductCategory productCategory : productCategories) {
            productIds.add(String.valueOf(productCategory.getProduct().getId()));
        }

        Filter filter = new Filter();
        filter.setField(Product_.ID);
        filter.setOperator(QueryOperator.IN);
        filter.setValues(productIds);
        filters.add(filter);

        return filters;
    }

    // filter by type of product: NEWEST, FAVORITE, POPULAR, EXPLORE, FEATURED
    @Override
    public Map<String, Object> filterByType(List<Filter> filters, ProductType type, Pageable pageable) {
        Filter filter;
        Sort sortByCreatedDate;
        switch (type) {
            case NEWEST:
                sortByCreatedDate = Sort.by(Order.desc("createdDate"));
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortByCreatedDate);
                break;
            case FAVORITE:
                Sort sortByFavouriteCount = Sort.by(Order.desc("favoriteCount"));
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortByFavouriteCount);

                filter = Filter.builder().field(Product_.FAVORITE_COUNT).operator(QueryOperator.NOT_EQUALS).value("0").build();
                filters.add(filter);
                break;
            case EXPLORE:
                sortByCreatedDate = Sort.by(Order.desc("createdDate"));
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortByCreatedDate);
                filter = Filter.builder().field(Product_.REQUEST_COUNT).operator(QueryOperator.EQUALS).value("0").build();
                filters.add(filter);

                filter = Filter.builder().field(Product_.RECEIVE_COUNT).operator(QueryOperator.EQUALS).value("0").build();
                filters.add(filter);
                break;
            case FEATURED:
                sortByCreatedDate = Sort.by(Order.desc("createdDate"));
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortByCreatedDate);

                // get list of top category
                AppConfiguration topCategories = appConfigurationRepository.findByKey(FEATURED_KEY);
                String topCategoryName = topCategories.getValue();
                String[] listOfCategoriesName = topCategoryName.split(",");
                List<Category> categories = new ArrayList<>();
                for (String categoryName : listOfCategoriesName) {
                    categoryRepository
                        .findOneByNameIgnoreCase(categoryName.strip())
                        .ifPresent(existCategory -> {
                            categories.add(existCategory);
                        });
                }

                // append filters by categories
                for (Category category : categories) {
                    filters = this.filterByCategory(filters, category.getId());
                }
                break;
            default:
                break;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("filters", filters);
        map.put("pageable", pageable);
        return map;
    }

    // filter by status
    @Override
    public List<Filter> filterByStatus(List<Filter> filters, Status status) {
        Filter filter = new Filter();
        filter.setField(Product_.STATUS);
        filter.setOperator(QueryOperator.EQUALS);
        filter.setValue(Status.AVAILABLE.toString());

        filters.add(filter);
        return filters;
    }

    @Override
    public List<Filter> filterByIsBlock(List<Filter> filters) {
        Filter filter = new Filter();
        filter.setField(Product_.IS_BLOCK);
        filter.setOperator(QueryOperator.EQUALS);
        filter.setValue(String.valueOf(false));

        filters.add(filter);
        return filters;
    }

    // get specification from filters, searchFilters
    @Override
    public Specification<Product> getSpecification(List<Filter> filters, List<Filter> searchFilters, List<Filter> featuredFilters) {
        ProductSpecification productSpecification = new ProductSpecification();
        Specification<Product> specification = null;
        Specification<Product> searchSpecification = null;
        Specification<Product> featuredSpecification = null;

        // check filters is not empty and initiate specification from filter
        if (!filters.isEmpty()) {
            specification = productSpecification.getSpecificationFromFilters(filters);
        }
        if (!searchFilters.isEmpty()) {
            searchSpecification = productSpecification.getOrSpecificationFromFilters(searchFilters);
        }
        if (!featuredFilters.isEmpty()) {
            featuredSpecification = productSpecification.getOrSpecificationFromFilters(featuredFilters);
        }

        // combine all specifications into one
        if (searchSpecification != null) {
            specification = specification.and(searchSpecification);
        }
        if (featuredFilters != null) {
            specification = specification.and(featuredSpecification);
        }

        return specification;
    }

    // get specification from filters, searchFilters, categoryFilter, cityFilter, purposeFilter, conditionFilter
    @Override
    public Specification<Product> getSpecification(
        List<Filter> filters,
        List<Filter> searchFilters,
        List<Filter> categoryFilters,
        List<Filter> cityFilters,
        List<Filter> conditionFilter,
        List<Filter> purposeFilters
    ) {
        ProductSpecification productSpecification = new ProductSpecification(cityRepository);
        Specification<Product> specification = null;
        Specification<Product> searchSpecification = null;
        Specification<Product> categorySpecification = null;
        Specification<Product> citySpecification = null;
        Specification<Product> conditionSpecification = null;
        Specification<Product> purposeSpecification = null;

        // check filters is not empty and initiate specification from filter
        if (!filters.isEmpty()) {
            specification = productSpecification.getSpecificationFromFilters(filters);
        }
        if (!searchFilters.isEmpty()) {
            searchSpecification = productSpecification.getOrSpecificationFromFilters(searchFilters);
        }
        if (!categoryFilters.isEmpty()) {
            categorySpecification = productSpecification.getOrSpecificationFromFilters(categoryFilters);
        }
        if (!cityFilters.isEmpty()) {
            citySpecification = productSpecification.getOrSpecificationFromFilters(cityFilters);
        }
        if (!conditionFilter.isEmpty()) {
            conditionSpecification = productSpecification.getOrSpecificationFromFilters(conditionFilter);
        }
        if (!purposeFilters.isEmpty()) {
            purposeSpecification = productSpecification.getOrSpecificationFromFilters(purposeFilters);
        }

        // combine all specifications into one
        if (searchSpecification != null) {
            specification = specification.and(searchSpecification);
        }
        if (categorySpecification != null) {
            specification = specification.and(categorySpecification);
        }
        if (citySpecification != null) {
            specification = specification.and(citySpecification);
        }
        if (conditionSpecification != null) {
            specification = specification.and(conditionSpecification);
        }
        if (purposeSpecification != null) {
            specification = specification.and(purposeSpecification);
        }

        return specification;
    }

    @Override
    public List<String> getProductIdsFromCurrentUser() {
        List<String> productIds = new ArrayList<>();
        User currentUser = userService.getCurrentUser().get();
        Profile profile = profileRepository.findOneByUser(currentUser).get();

        List<Product> products = productRepository.findByProfile(profile);
        for (Product product : products) {
            productIds.add(String.valueOf(product.getId()));
        }
        if (productIds.isEmpty()) {
            productIds.add("-1");
        }
        return productIds;
    }

    @Override
    public List<Filter> filterToIgnoreProductOfUser(List<Filter> filters) {
        List<String> productIds = getProductIdsFromCurrentUser();
        Filter filter = new Filter();
        filter.setField(Product_.ID);
        filter.setOperator(QueryOperator.NOT_IN);
        filter.setValues(productIds);

        filters.add(filter);
        return filters;
    }

    @Override
    public List<Filter> filterByCondition(List<Filter> filters, Condition condition) {
        Filter filter = new Filter();
        filter.setField(Product_.CONDITION);
        filter.setOperator(QueryOperator.EQUALS);
        filter.setValue(condition.toString());

        filters.add(filter);
        return filters;
    }

    @Override
    public List<Filter> filterByLocation(List<Filter> filters, String location) {
        Filter filter = new Filter();
        filter.setField(Product_.LOCATION);
        filter.setOperator(QueryOperator.LIKE);
        filter.setValue(location);

        filters.add(filter);
        return filters;
    }

    @Override
    public List<Filter> filterByCity(List<Filter> filters, Long cityId) {
        Optional<City> cityOptional = cityRepository.findById(cityId);
        // not found city
        if (!cityOptional.isPresent()) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0085, cityId), new Throwable());
        }

        City city = cityOptional.get();

        Filter filter = new Filter();
        filter.setField(Product_.CITY);
        filter.setOperator(QueryOperator.EQUALS);
        filter.setValue(String.valueOf(cityId));
        filters.add(filter);

        return filters;
    }

    @Override
    public List<Filter> filterByPurpose(List<Filter> filters, PurposeType purpose) {
        Filter filter = new Filter();
        switch (purpose) {
            case EXCHANGE:
                filter.setField(Product_.IS_EXCHANGE);
                filter.setOperator(QueryOperator.EQUALS);
                break;
            case AUCTION:
                filter.setField(Product_.IS_AUCTION);
                filter.setOperator(QueryOperator.EQUALS);
                break;
            case SELL:
                filter.setField(Product_.IS_SELL);
                filter.setOperator(QueryOperator.EQUALS);
                break;
            case GIFT:
                filter.setField(Product_.IS_GIFT);
                filter.setOperator(QueryOperator.EQUALS);
                break;
            default:
                break;
        }
        filter.setValue(Boolean.TRUE.toString());
        filters.add(filter);
        return filters;
    }

    @Override
    public List<Long> getWishlistFromUser(User user) {
        Profile profile = profileRepository.findOneByUser(user).get();
        List<Favorite> favorites = favoriteRepository.findByProfile(profile);
        List<Long> wishlist = new ArrayList<>();

        for (Favorite favorite : favorites) {
            wishlist.add(favorite.getProduct().getId());
        }
        return wishlist;
    }

    @Override
    public List<String> getAllProductLocations() {
        List<String> locations = productRepository.findAllProductLocations();
        return locations;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FilterProductRes webFilterProducts(Pageable pageable, FilterDTO filterDTO) {
        log.debug("web filter product");
        Pageable newPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        List<Filter> filters = new ArrayList<>();
        //filters for search product
        List<Filter> searchFilters = new ArrayList<>();

        //filters for search product by type category
        List<Filter> categoryFilters = new ArrayList<>();

        //filters for search product by type condition
        List<Filter> conditionFilters = new ArrayList<>();

        //filters for search product by type purpose
        List<Filter> purposeFilters = new ArrayList<>();

        //filters for search product by type purpose
        List<Filter> cityFilters = new ArrayList<>();

        // filter by product type: NEWEST, FAVORITE, EXPLORE, FEATURED
        if (filterDTO.getType() != null) {
            Map<String, Object> map = filterByType(filters, filterDTO.getType(), newPageable);
            filters = (ArrayList<Filter>) map.get("filters");
            newPageable = (Pageable) map.get("pageable");
        }

        // filter by status : AVAILABLE
        filters = filterByStatus(filters, Status.AVAILABLE);
        filters = filterByIsBlock(filters);
        // filter by search name
        if (filterDTO.getSearch() != null) {
            searchFilters = filterBySearchName(searchFilters, filterDTO.getSearch());
        }

        // filter by categoryIds
        if (filterDTO.getCategoryIds() != null) {
            for (Long categoryId : filterDTO.getCategoryIds()) {
                categoryFilters = filterByCategory(categoryFilters, categoryId);
            }
        }

        // filter by conditions
        if (filterDTO.getProductConditions() != null) {
            for (Condition condition : filterDTO.getProductConditions()) {
                conditionFilters = filterByCondition(conditionFilters, condition);
            }
        }

        // filter by purpose
        if (filterDTO.getPurposeTypes() != null) {
            for (PurposeType purposeType : filterDTO.getPurposeTypes()) {
                purposeFilters = filterByPurpose(purposeFilters, purposeType);
            }
        }

        // filter by city
        if (filterDTO.getCityIds() != null) {
            for (Long cityId : filterDTO.getCityIds()) {
                cityFilters = filterByCity(cityFilters, cityId);
            }
        }

        // ignore products of current user
        if (userService.getCurrentUser().isPresent()) {
            filters = filterToIgnoreProductOfUser(filters);
        }

        // get specification and find products
        Specification<Product> specification = getSpecification(
            filters,
            searchFilters,
            categoryFilters,
            cityFilters,
            conditionFilters,
            purposeFilters
        );
        List<Product> products = productRepository.findAll(specification, newPageable).getContent();

        // update category name and display name and union
        List<ProductDTO> productDtos = new ArrayList<>();
        Optional<User> currentUser = userService.getCurrentUser();
        List<Long> wishlish = null;
        if (currentUser.isPresent()) {
            wishlish = this.getWishlistFromUser(currentUser.get());
        }

        try {
            for (Product product : products) {
                ProductDTO productDTO = productMapper.toDto(product);
                productDTO.getProfile().setDisplayName(product.getProfile().getDisplayName());
                productDTO.getProfile().setAvatar(product.getProfile().getAvatar());

                // find category
                List<ProductCategory> productCategories = productCategoryRepository.findByProduct(product);
                List<Category> categorieProducts = new ArrayList<Category>();
                if (!productCategories.isEmpty()) {
                    for (ProductCategory productCategory : productCategories) {
                        categorieProducts.add(categoryRepository.findById(productCategory.getCategory().getId()).get());
                    }
                }
                productDTO.setCategoryName(categorieProducts.get(0).getName());

                // set is favorite
                productDTO.setIsFavorite(false); // set default
                if (wishlish != null && wishlish.contains(product.getId())) {
                    productDTO.setIsFavorite(true);
                }

                productDtos.add(productDTO);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        // get pagination data
        int itemsPerPage = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int totalItems = (int) productRepository.count(specification);
        PaginateRes paginateRes = PaginateRes.builder().itemsPerPage(itemsPerPage).currentPage(currentPage).totalItems(totalItems).build();

        // create response object
        FilterProductRes filterProductRes = new FilterProductRes(productDtos, paginateRes);

        return filterProductRes;
    }

    @Override
    public GetProductRes getMyProductDetail(Long productId) {
        Profile profile = profileService.getCurrentProfile();
        if(!productRepository.existsByIdAndProfile(productId, profile)) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0100, productId), new Throwable());
        }
        
        return this.getProductDetail(productId);
    }

    
}
