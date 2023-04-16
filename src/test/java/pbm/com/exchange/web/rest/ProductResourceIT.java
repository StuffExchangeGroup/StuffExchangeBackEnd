package pbm.com.exchange.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pbm.com.exchange.web.rest.TestUtil.sameInstant;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pbm.com.exchange.IntegrationTest;
import pbm.com.exchange.domain.Product;
import pbm.com.exchange.domain.enumeration.Condition;
import pbm.com.exchange.domain.enumeration.ProductStatus;
import pbm.com.exchange.repository.ProductRepository;
import pbm.com.exchange.service.ProductService;
import pbm.com.exchange.service.dto.ProductDTO;
import pbm.com.exchange.service.mapper.ProductMapper;

/**
 * Integration tests for the {@link ProductResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProductResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_NOTICE = "AAAAAAAAAA";
    private static final String UPDATED_NOTICE = "BBBBBBBBBB";

    private static final String DEFAULT_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION = "BBBBBBBBBB";

    private static final String DEFAULT_VERIFY_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_VERIFY_PHONE = "BBBBBBBBBB";

    private static final ProductStatus DEFAULT_STATUS = ProductStatus.SWAPPED;
    private static final ProductStatus UPDATED_STATUS = ProductStatus.AVAILABLE;

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String DEFAULT_THUMBNAIL = "AAAAAAAAAA";
    private static final String UPDATED_THUMBNAIL = "BBBBBBBBBB";

    private static final Integer DEFAULT_POINT = 1;
    private static final Integer UPDATED_POINT = 2;

    private static final Integer DEFAULT_FAVORITE_COUNT = 1;
    private static final Integer UPDATED_FAVORITE_COUNT = 2;

    private static final Double DEFAULT_LATITUDE = 1D;
    private static final Double UPDATED_LATITUDE = 2D;

    private static final Double DEFAULT_LONGITUDE = 1D;
    private static final Double UPDATED_LONGITUDE = 2D;

    private static final Condition DEFAULT_CONDITION = Condition.NEW;
    private static final Condition UPDATED_CONDITION = Condition.LIKENEW;

    private static final Integer DEFAULT_REQUEST_COUNT = 1;
    private static final Integer UPDATED_REQUEST_COUNT = 2;

    private static final Integer DEFAULT_RECEIVE_COUNT = 1;
    private static final Integer UPDATED_RECEIVE_COUNT = 2;

    private static final Boolean DEFAULT_IS_SWAP_AVAILABLE = false;
    private static final Boolean UPDATED_IS_SWAP_AVAILABLE = true;

    private static final ZonedDateTime DEFAULT_AUCTION_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_AUCTION_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductRepository productRepository;

    @Mock
    private ProductRepository productRepositoryMock;

    @Autowired
    private ProductMapper productMapper;

    @Mock
    private ProductService productServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductMockMvc;

    private Product product;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createEntity(EntityManager em) {
        Product product = new Product()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .notice(DEFAULT_NOTICE)
            .location(DEFAULT_LOCATION)
            .verifyPhone(DEFAULT_VERIFY_PHONE)
            .status(DEFAULT_STATUS)
            .active(DEFAULT_ACTIVE)
            .thumbnail(DEFAULT_THUMBNAIL)
            .point(DEFAULT_POINT)
            .favoriteCount(DEFAULT_FAVORITE_COUNT)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE)
            .condition(DEFAULT_CONDITION)
            .requestCount(DEFAULT_REQUEST_COUNT)
            .receiveCount(DEFAULT_RECEIVE_COUNT)
            .isAvailable(DEFAULT_IS_SWAP_AVAILABLE)
            .auctionTime(DEFAULT_AUCTION_TIME);
        return product;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createUpdatedEntity(EntityManager em) {
        Product product = new Product()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .notice(UPDATED_NOTICE)
            .location(UPDATED_LOCATION)
            .verifyPhone(UPDATED_VERIFY_PHONE)
            .status(UPDATED_STATUS)
            .active(UPDATED_ACTIVE)
            .thumbnail(UPDATED_THUMBNAIL)
            .point(UPDATED_POINT)
            .favoriteCount(UPDATED_FAVORITE_COUNT)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .condition(UPDATED_CONDITION)
            .requestCount(UPDATED_REQUEST_COUNT)
            .receiveCount(UPDATED_RECEIVE_COUNT)
            .isAvailable(UPDATED_IS_SWAP_AVAILABLE)
            .auctionTime(UPDATED_AUCTION_TIME);
        return product;
    }

    @BeforeEach
    public void initTest() {
        product = createEntity(em);
    }

    @Test
    @Transactional
    void createProduct() throws Exception {
        int databaseSizeBeforeCreate = productRepository.findAll().size();
        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);
        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isCreated());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate + 1);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProduct.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProduct.getNotice()).isEqualTo(DEFAULT_NOTICE);
        assertThat(testProduct.getLocation()).isEqualTo(DEFAULT_LOCATION);
        assertThat(testProduct.getVerifyPhone()).isEqualTo(DEFAULT_VERIFY_PHONE);
        assertThat(testProduct.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testProduct.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testProduct.getThumbnail()).isEqualTo(DEFAULT_THUMBNAIL);
        assertThat(testProduct.getPoint()).isEqualTo(DEFAULT_POINT);
        assertThat(testProduct.getFavoriteCount()).isEqualTo(DEFAULT_FAVORITE_COUNT);
        assertThat(testProduct.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testProduct.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testProduct.getCondition()).isEqualTo(DEFAULT_CONDITION);
        assertThat(testProduct.getRequestCount()).isEqualTo(DEFAULT_REQUEST_COUNT);
        assertThat(testProduct.getReceiveCount()).isEqualTo(DEFAULT_RECEIVE_COUNT);
        assertThat(testProduct.getIsSwapAvailable()).isEqualTo(DEFAULT_IS_SWAP_AVAILABLE);
        assertThat(testProduct.getAuctionTime()).isEqualTo(DEFAULT_AUCTION_TIME);
    }

    @Test
    @Transactional
    void createProductWithExistingId() throws Exception {
        // Create the Product with an existing ID
        product.setId(1L);
        ProductDTO productDTO = productMapper.toDto(product);

        int databaseSizeBeforeCreate = productRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllProducts() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].notice").value(hasItem(DEFAULT_NOTICE)))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION)))
            .andExpect(jsonPath("$.[*].verifyPhone").value(hasItem(DEFAULT_VERIFY_PHONE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].thumbnail").value(hasItem(DEFAULT_THUMBNAIL)))
            .andExpect(jsonPath("$.[*].point").value(hasItem(DEFAULT_POINT)))
            .andExpect(jsonPath("$.[*].favoriteCount").value(hasItem(DEFAULT_FAVORITE_COUNT)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].condition").value(hasItem(DEFAULT_CONDITION.toString())))
            .andExpect(jsonPath("$.[*].requestCount").value(hasItem(DEFAULT_REQUEST_COUNT)))
            .andExpect(jsonPath("$.[*].receiveCount").value(hasItem(DEFAULT_RECEIVE_COUNT)))
            .andExpect(jsonPath("$.[*].isSwapAvailable").value(hasItem(DEFAULT_IS_SWAP_AVAILABLE.booleanValue())))
            .andExpect(jsonPath("$.[*].auctionTime").value(hasItem(sameInstant(DEFAULT_AUCTION_TIME))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductsWithEagerRelationshipsIsEnabled() throws Exception {
        when(productServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(productServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(productServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(productServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get the product
        restProductMockMvc
            .perform(get(ENTITY_API_URL_ID, product.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(product.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.notice").value(DEFAULT_NOTICE))
            .andExpect(jsonPath("$.location").value(DEFAULT_LOCATION))
            .andExpect(jsonPath("$.verifyPhone").value(DEFAULT_VERIFY_PHONE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.thumbnail").value(DEFAULT_THUMBNAIL))
            .andExpect(jsonPath("$.point").value(DEFAULT_POINT))
            .andExpect(jsonPath("$.favoriteCount").value(DEFAULT_FAVORITE_COUNT))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.doubleValue()))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.doubleValue()))
            .andExpect(jsonPath("$.condition").value(DEFAULT_CONDITION.toString()))
            .andExpect(jsonPath("$.requestCount").value(DEFAULT_REQUEST_COUNT))
            .andExpect(jsonPath("$.receiveCount").value(DEFAULT_RECEIVE_COUNT))
            .andExpect(jsonPath("$.isSwapAvailable").value(DEFAULT_IS_SWAP_AVAILABLE.booleanValue()))
            .andExpect(jsonPath("$.auctionTime").value(sameInstant(DEFAULT_AUCTION_TIME)));
    }

    @Test
    @Transactional
    void getNonExistingProduct() throws Exception {
        // Get the product
        restProductMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product
        Product updatedProduct = productRepository.findById(product.getId()).get();
        // Disconnect from session so that the updates on updatedProduct are not directly saved in db
        em.detach(updatedProduct);
        updatedProduct
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .notice(UPDATED_NOTICE)
            .location(UPDATED_LOCATION)
            .verifyPhone(UPDATED_VERIFY_PHONE)
            .status(UPDATED_STATUS)
            .active(UPDATED_ACTIVE)
            .thumbnail(UPDATED_THUMBNAIL)
            .point(UPDATED_POINT)
            .favoriteCount(UPDATED_FAVORITE_COUNT)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .condition(UPDATED_CONDITION)
            .requestCount(UPDATED_REQUEST_COUNT)
            .receiveCount(UPDATED_RECEIVE_COUNT)
            .isAvailable(UPDATED_IS_SWAP_AVAILABLE)
            .auctionTime(UPDATED_AUCTION_TIME);
        ProductDTO productDTO = productMapper.toDto(updatedProduct);

        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProduct.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProduct.getNotice()).isEqualTo(UPDATED_NOTICE);
        assertThat(testProduct.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testProduct.getVerifyPhone()).isEqualTo(UPDATED_VERIFY_PHONE);
        assertThat(testProduct.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testProduct.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testProduct.getThumbnail()).isEqualTo(UPDATED_THUMBNAIL);
        assertThat(testProduct.getPoint()).isEqualTo(UPDATED_POINT);
        assertThat(testProduct.getFavoriteCount()).isEqualTo(UPDATED_FAVORITE_COUNT);
        assertThat(testProduct.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testProduct.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testProduct.getCondition()).isEqualTo(UPDATED_CONDITION);
        assertThat(testProduct.getRequestCount()).isEqualTo(UPDATED_REQUEST_COUNT);
        assertThat(testProduct.getReceiveCount()).isEqualTo(UPDATED_RECEIVE_COUNT);
        assertThat(testProduct.getIsSwapAvailable()).isEqualTo(UPDATED_IS_SWAP_AVAILABLE);
        assertThat(testProduct.getAuctionTime()).isEqualTo(UPDATED_AUCTION_TIME);
    }

    @Test
    @Transactional
    void putNonExistingProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductWithPatch() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product using partial update
        Product partialUpdatedProduct = new Product();
        partialUpdatedProduct.setId(product.getId());

        partialUpdatedProduct
            .verifyPhone(UPDATED_VERIFY_PHONE)
            .status(UPDATED_STATUS)
            .latitude(UPDATED_LATITUDE)
            .requestCount(UPDATED_REQUEST_COUNT);

        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProduct))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProduct.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProduct.getNotice()).isEqualTo(DEFAULT_NOTICE);
        assertThat(testProduct.getLocation()).isEqualTo(DEFAULT_LOCATION);
        assertThat(testProduct.getVerifyPhone()).isEqualTo(UPDATED_VERIFY_PHONE);
        assertThat(testProduct.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testProduct.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testProduct.getThumbnail()).isEqualTo(DEFAULT_THUMBNAIL);
        assertThat(testProduct.getPoint()).isEqualTo(DEFAULT_POINT);
        assertThat(testProduct.getFavoriteCount()).isEqualTo(DEFAULT_FAVORITE_COUNT);
        assertThat(testProduct.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testProduct.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testProduct.getCondition()).isEqualTo(DEFAULT_CONDITION);
        assertThat(testProduct.getRequestCount()).isEqualTo(UPDATED_REQUEST_COUNT);
        assertThat(testProduct.getReceiveCount()).isEqualTo(DEFAULT_RECEIVE_COUNT);
        assertThat(testProduct.getIsSwapAvailable()).isEqualTo(DEFAULT_IS_SWAP_AVAILABLE);
        assertThat(testProduct.getAuctionTime()).isEqualTo(DEFAULT_AUCTION_TIME);
    }

    @Test
    @Transactional
    void fullUpdateProductWithPatch() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product using partial update
        Product partialUpdatedProduct = new Product();
        partialUpdatedProduct.setId(product.getId());

        partialUpdatedProduct
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .notice(UPDATED_NOTICE)
            .location(UPDATED_LOCATION)
            .verifyPhone(UPDATED_VERIFY_PHONE)
            .status(UPDATED_STATUS)
            .active(UPDATED_ACTIVE)
            .thumbnail(UPDATED_THUMBNAIL)
            .point(UPDATED_POINT)
            .favoriteCount(UPDATED_FAVORITE_COUNT)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .condition(UPDATED_CONDITION)
            .requestCount(UPDATED_REQUEST_COUNT)
            .receiveCount(UPDATED_RECEIVE_COUNT)
            .isAvailable(UPDATED_IS_SWAP_AVAILABLE)
            .auctionTime(UPDATED_AUCTION_TIME);

        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProduct))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProduct.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProduct.getNotice()).isEqualTo(UPDATED_NOTICE);
        assertThat(testProduct.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testProduct.getVerifyPhone()).isEqualTo(UPDATED_VERIFY_PHONE);
        assertThat(testProduct.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testProduct.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testProduct.getThumbnail()).isEqualTo(UPDATED_THUMBNAIL);
        assertThat(testProduct.getPoint()).isEqualTo(UPDATED_POINT);
        assertThat(testProduct.getFavoriteCount()).isEqualTo(UPDATED_FAVORITE_COUNT);
        assertThat(testProduct.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testProduct.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testProduct.getCondition()).isEqualTo(UPDATED_CONDITION);
        assertThat(testProduct.getRequestCount()).isEqualTo(UPDATED_REQUEST_COUNT);
        assertThat(testProduct.getReceiveCount()).isEqualTo(UPDATED_RECEIVE_COUNT);
        assertThat(testProduct.getIsSwapAvailable()).isEqualTo(UPDATED_IS_SWAP_AVAILABLE);
        assertThat(testProduct.getAuctionTime()).isEqualTo(UPDATED_AUCTION_TIME);
    }

    @Test
    @Transactional
    void patchNonExistingProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeDelete = productRepository.findAll().size();

        // Delete the product
        restProductMockMvc
            .perform(delete(ENTITY_API_URL_ID, product.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
