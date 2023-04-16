package pbm.com.exchange.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pbm.com.exchange.IntegrationTest;
import pbm.com.exchange.domain.ProductPurpose;
import pbm.com.exchange.domain.enumeration.PurposeType;
import pbm.com.exchange.repository.ProductPurposeRepository;
import pbm.com.exchange.service.dto.ProductPurposeDTO;
import pbm.com.exchange.service.mapper.ProductPurposeMapper;

/**
 * Integration tests for the {@link ProductPurposeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductPurposeResourceIT {

    private static final PurposeType DEFAULT_NAME = PurposeType.EXCHANGE;
    private static final PurposeType UPDATED_NAME = PurposeType.SELL;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/product-purposes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductPurposeRepository productPurposeRepository;

    @Autowired
    private ProductPurposeMapper productPurposeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductPurposeMockMvc;

    private ProductPurpose productPurpose;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductPurpose createEntity(EntityManager em) {
        ProductPurpose productPurpose = new ProductPurpose().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
        return productPurpose;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductPurpose createUpdatedEntity(EntityManager em) {
        ProductPurpose productPurpose = new ProductPurpose().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        return productPurpose;
    }

    @BeforeEach
    public void initTest() {
        productPurpose = createEntity(em);
    }

    @Test
    @Transactional
    void createProductPurpose() throws Exception {
        int databaseSizeBeforeCreate = productPurposeRepository.findAll().size();
        // Create the ProductPurpose
        ProductPurposeDTO productPurposeDTO = productPurposeMapper.toDto(productPurpose);
        restProductPurposeMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productPurposeDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ProductPurpose in the database
        List<ProductPurpose> productPurposeList = productPurposeRepository.findAll();
        assertThat(productPurposeList).hasSize(databaseSizeBeforeCreate + 1);
        ProductPurpose testProductPurpose = productPurposeList.get(productPurposeList.size() - 1);
        assertThat(testProductPurpose.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProductPurpose.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createProductPurposeWithExistingId() throws Exception {
        // Create the ProductPurpose with an existing ID
        productPurpose.setId(1L);
        ProductPurposeDTO productPurposeDTO = productPurposeMapper.toDto(productPurpose);

        int databaseSizeBeforeCreate = productPurposeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductPurposeMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productPurposeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductPurpose in the database
        List<ProductPurpose> productPurposeList = productPurposeRepository.findAll();
        assertThat(productPurposeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllProductPurposes() throws Exception {
        // Initialize the database
        productPurposeRepository.saveAndFlush(productPurpose);

        // Get all the productPurposeList
        restProductPurposeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productPurpose.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getProductPurpose() throws Exception {
        // Initialize the database
        productPurposeRepository.saveAndFlush(productPurpose);

        // Get the productPurpose
        restProductPurposeMockMvc
            .perform(get(ENTITY_API_URL_ID, productPurpose.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productPurpose.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingProductPurpose() throws Exception {
        // Get the productPurpose
        restProductPurposeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProductPurpose() throws Exception {
        // Initialize the database
        productPurposeRepository.saveAndFlush(productPurpose);

        int databaseSizeBeforeUpdate = productPurposeRepository.findAll().size();

        // Update the productPurpose
        ProductPurpose updatedProductPurpose = productPurposeRepository.findById(productPurpose.getId()).get();
        // Disconnect from session so that the updates on updatedProductPurpose are not directly saved in db
        em.detach(updatedProductPurpose);
        updatedProductPurpose.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        ProductPurposeDTO productPurposeDTO = productPurposeMapper.toDto(updatedProductPurpose);

        restProductPurposeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productPurposeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productPurposeDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProductPurpose in the database
        List<ProductPurpose> productPurposeList = productPurposeRepository.findAll();
        assertThat(productPurposeList).hasSize(databaseSizeBeforeUpdate);
        ProductPurpose testProductPurpose = productPurposeList.get(productPurposeList.size() - 1);
        assertThat(testProductPurpose.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProductPurpose.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingProductPurpose() throws Exception {
        int databaseSizeBeforeUpdate = productPurposeRepository.findAll().size();
        productPurpose.setId(count.incrementAndGet());

        // Create the ProductPurpose
        ProductPurposeDTO productPurposeDTO = productPurposeMapper.toDto(productPurpose);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductPurposeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productPurposeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productPurposeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductPurpose in the database
        List<ProductPurpose> productPurposeList = productPurposeRepository.findAll();
        assertThat(productPurposeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProductPurpose() throws Exception {
        int databaseSizeBeforeUpdate = productPurposeRepository.findAll().size();
        productPurpose.setId(count.incrementAndGet());

        // Create the ProductPurpose
        ProductPurposeDTO productPurposeDTO = productPurposeMapper.toDto(productPurpose);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductPurposeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productPurposeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductPurpose in the database
        List<ProductPurpose> productPurposeList = productPurposeRepository.findAll();
        assertThat(productPurposeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProductPurpose() throws Exception {
        int databaseSizeBeforeUpdate = productPurposeRepository.findAll().size();
        productPurpose.setId(count.incrementAndGet());

        // Create the ProductPurpose
        ProductPurposeDTO productPurposeDTO = productPurposeMapper.toDto(productPurpose);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductPurposeMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productPurposeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductPurpose in the database
        List<ProductPurpose> productPurposeList = productPurposeRepository.findAll();
        assertThat(productPurposeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductPurposeWithPatch() throws Exception {
        // Initialize the database
        productPurposeRepository.saveAndFlush(productPurpose);

        int databaseSizeBeforeUpdate = productPurposeRepository.findAll().size();

        // Update the productPurpose using partial update
        ProductPurpose partialUpdatedProductPurpose = new ProductPurpose();
        partialUpdatedProductPurpose.setId(productPurpose.getId());

        restProductPurposeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductPurpose.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProductPurpose))
            )
            .andExpect(status().isOk());

        // Validate the ProductPurpose in the database
        List<ProductPurpose> productPurposeList = productPurposeRepository.findAll();
        assertThat(productPurposeList).hasSize(databaseSizeBeforeUpdate);
        ProductPurpose testProductPurpose = productPurposeList.get(productPurposeList.size() - 1);
        assertThat(testProductPurpose.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProductPurpose.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateProductPurposeWithPatch() throws Exception {
        // Initialize the database
        productPurposeRepository.saveAndFlush(productPurpose);

        int databaseSizeBeforeUpdate = productPurposeRepository.findAll().size();

        // Update the productPurpose using partial update
        ProductPurpose partialUpdatedProductPurpose = new ProductPurpose();
        partialUpdatedProductPurpose.setId(productPurpose.getId());

        partialUpdatedProductPurpose.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restProductPurposeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductPurpose.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProductPurpose))
            )
            .andExpect(status().isOk());

        // Validate the ProductPurpose in the database
        List<ProductPurpose> productPurposeList = productPurposeRepository.findAll();
        assertThat(productPurposeList).hasSize(databaseSizeBeforeUpdate);
        ProductPurpose testProductPurpose = productPurposeList.get(productPurposeList.size() - 1);
        assertThat(testProductPurpose.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProductPurpose.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingProductPurpose() throws Exception {
        int databaseSizeBeforeUpdate = productPurposeRepository.findAll().size();
        productPurpose.setId(count.incrementAndGet());

        // Create the ProductPurpose
        ProductPurposeDTO productPurposeDTO = productPurposeMapper.toDto(productPurpose);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductPurposeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productPurposeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productPurposeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductPurpose in the database
        List<ProductPurpose> productPurposeList = productPurposeRepository.findAll();
        assertThat(productPurposeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProductPurpose() throws Exception {
        int databaseSizeBeforeUpdate = productPurposeRepository.findAll().size();
        productPurpose.setId(count.incrementAndGet());

        // Create the ProductPurpose
        ProductPurposeDTO productPurposeDTO = productPurposeMapper.toDto(productPurpose);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductPurposeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productPurposeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductPurpose in the database
        List<ProductPurpose> productPurposeList = productPurposeRepository.findAll();
        assertThat(productPurposeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProductPurpose() throws Exception {
        int databaseSizeBeforeUpdate = productPurposeRepository.findAll().size();
        productPurpose.setId(count.incrementAndGet());

        // Create the ProductPurpose
        ProductPurposeDTO productPurposeDTO = productPurposeMapper.toDto(productPurpose);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductPurposeMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productPurposeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductPurpose in the database
        List<ProductPurpose> productPurposeList = productPurposeRepository.findAll();
        assertThat(productPurposeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProductPurpose() throws Exception {
        // Initialize the database
        productPurposeRepository.saveAndFlush(productPurpose);

        int databaseSizeBeforeDelete = productPurposeRepository.findAll().size();

        // Delete the productPurpose
        restProductPurposeMockMvc
            .perform(delete(ENTITY_API_URL_ID, productPurpose.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ProductPurpose> productPurposeList = productPurposeRepository.findAll();
        assertThat(productPurposeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
