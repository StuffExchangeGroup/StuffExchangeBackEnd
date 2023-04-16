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
import pbm.com.exchange.domain.Nationality;
import pbm.com.exchange.repository.NationalityRepository;
import pbm.com.exchange.service.dto.NationalityDTO;
import pbm.com.exchange.service.mapper.NationalityMapper;

/**
 * Integration tests for the {@link NationalityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NationalityResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/nationalities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private NationalityRepository nationalityRepository;

    @Autowired
    private NationalityMapper nationalityMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNationalityMockMvc;

    private Nationality nationality;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Nationality createEntity(EntityManager em) {
        Nationality nationality = new Nationality().name(DEFAULT_NAME).code(DEFAULT_CODE);
        return nationality;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Nationality createUpdatedEntity(EntityManager em) {
        Nationality nationality = new Nationality().name(UPDATED_NAME).code(UPDATED_CODE);
        return nationality;
    }

    @BeforeEach
    public void initTest() {
        nationality = createEntity(em);
    }

    @Test
    @Transactional
    void createNationality() throws Exception {
        int databaseSizeBeforeCreate = nationalityRepository.findAll().size();
        // Create the Nationality
        NationalityDTO nationalityDTO = nationalityMapper.toDto(nationality);
        restNationalityMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(nationalityDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Nationality in the database
        List<Nationality> nationalityList = nationalityRepository.findAll();
        assertThat(nationalityList).hasSize(databaseSizeBeforeCreate + 1);
        Nationality testNationality = nationalityList.get(nationalityList.size() - 1);
        assertThat(testNationality.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testNationality.getCode()).isEqualTo(DEFAULT_CODE);
    }

    @Test
    @Transactional
    void createNationalityWithExistingId() throws Exception {
        // Create the Nationality with an existing ID
        nationality.setId(1L);
        NationalityDTO nationalityDTO = nationalityMapper.toDto(nationality);

        int databaseSizeBeforeCreate = nationalityRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNationalityMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(nationalityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Nationality in the database
        List<Nationality> nationalityList = nationalityRepository.findAll();
        assertThat(nationalityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllNationalities() throws Exception {
        // Initialize the database
        nationalityRepository.saveAndFlush(nationality);

        // Get all the nationalityList
        restNationalityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(nationality.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)));
    }

    @Test
    @Transactional
    void getNationality() throws Exception {
        // Initialize the database
        nationalityRepository.saveAndFlush(nationality);

        // Get the nationality
        restNationalityMockMvc
            .perform(get(ENTITY_API_URL_ID, nationality.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(nationality.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE));
    }

    @Test
    @Transactional
    void getNonExistingNationality() throws Exception {
        // Get the nationality
        restNationalityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewNationality() throws Exception {
        // Initialize the database
        nationalityRepository.saveAndFlush(nationality);

        int databaseSizeBeforeUpdate = nationalityRepository.findAll().size();

        // Update the nationality
        Nationality updatedNationality = nationalityRepository.findById(nationality.getId()).get();
        // Disconnect from session so that the updates on updatedNationality are not directly saved in db
        em.detach(updatedNationality);
        updatedNationality.name(UPDATED_NAME).code(UPDATED_CODE);
        NationalityDTO nationalityDTO = nationalityMapper.toDto(updatedNationality);

        restNationalityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, nationalityDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(nationalityDTO))
            )
            .andExpect(status().isOk());

        // Validate the Nationality in the database
        List<Nationality> nationalityList = nationalityRepository.findAll();
        assertThat(nationalityList).hasSize(databaseSizeBeforeUpdate);
        Nationality testNationality = nationalityList.get(nationalityList.size() - 1);
        assertThat(testNationality.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testNationality.getCode()).isEqualTo(UPDATED_CODE);
    }

    @Test
    @Transactional
    void putNonExistingNationality() throws Exception {
        int databaseSizeBeforeUpdate = nationalityRepository.findAll().size();
        nationality.setId(count.incrementAndGet());

        // Create the Nationality
        NationalityDTO nationalityDTO = nationalityMapper.toDto(nationality);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNationalityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, nationalityDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(nationalityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Nationality in the database
        List<Nationality> nationalityList = nationalityRepository.findAll();
        assertThat(nationalityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNationality() throws Exception {
        int databaseSizeBeforeUpdate = nationalityRepository.findAll().size();
        nationality.setId(count.incrementAndGet());

        // Create the Nationality
        NationalityDTO nationalityDTO = nationalityMapper.toDto(nationality);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNationalityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(nationalityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Nationality in the database
        List<Nationality> nationalityList = nationalityRepository.findAll();
        assertThat(nationalityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNationality() throws Exception {
        int databaseSizeBeforeUpdate = nationalityRepository.findAll().size();
        nationality.setId(count.incrementAndGet());

        // Create the Nationality
        NationalityDTO nationalityDTO = nationalityMapper.toDto(nationality);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNationalityMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(nationalityDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Nationality in the database
        List<Nationality> nationalityList = nationalityRepository.findAll();
        assertThat(nationalityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNationalityWithPatch() throws Exception {
        // Initialize the database
        nationalityRepository.saveAndFlush(nationality);

        int databaseSizeBeforeUpdate = nationalityRepository.findAll().size();

        // Update the nationality using partial update
        Nationality partialUpdatedNationality = new Nationality();
        partialUpdatedNationality.setId(nationality.getId());

        partialUpdatedNationality.name(UPDATED_NAME).code(UPDATED_CODE);

        restNationalityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNationality.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNationality))
            )
            .andExpect(status().isOk());

        // Validate the Nationality in the database
        List<Nationality> nationalityList = nationalityRepository.findAll();
        assertThat(nationalityList).hasSize(databaseSizeBeforeUpdate);
        Nationality testNationality = nationalityList.get(nationalityList.size() - 1);
        assertThat(testNationality.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testNationality.getCode()).isEqualTo(UPDATED_CODE);
    }

    @Test
    @Transactional
    void fullUpdateNationalityWithPatch() throws Exception {
        // Initialize the database
        nationalityRepository.saveAndFlush(nationality);

        int databaseSizeBeforeUpdate = nationalityRepository.findAll().size();

        // Update the nationality using partial update
        Nationality partialUpdatedNationality = new Nationality();
        partialUpdatedNationality.setId(nationality.getId());

        partialUpdatedNationality.name(UPDATED_NAME).code(UPDATED_CODE);

        restNationalityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNationality.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNationality))
            )
            .andExpect(status().isOk());

        // Validate the Nationality in the database
        List<Nationality> nationalityList = nationalityRepository.findAll();
        assertThat(nationalityList).hasSize(databaseSizeBeforeUpdate);
        Nationality testNationality = nationalityList.get(nationalityList.size() - 1);
        assertThat(testNationality.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testNationality.getCode()).isEqualTo(UPDATED_CODE);
    }

    @Test
    @Transactional
    void patchNonExistingNationality() throws Exception {
        int databaseSizeBeforeUpdate = nationalityRepository.findAll().size();
        nationality.setId(count.incrementAndGet());

        // Create the Nationality
        NationalityDTO nationalityDTO = nationalityMapper.toDto(nationality);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNationalityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, nationalityDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(nationalityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Nationality in the database
        List<Nationality> nationalityList = nationalityRepository.findAll();
        assertThat(nationalityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNationality() throws Exception {
        int databaseSizeBeforeUpdate = nationalityRepository.findAll().size();
        nationality.setId(count.incrementAndGet());

        // Create the Nationality
        NationalityDTO nationalityDTO = nationalityMapper.toDto(nationality);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNationalityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(nationalityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Nationality in the database
        List<Nationality> nationalityList = nationalityRepository.findAll();
        assertThat(nationalityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNationality() throws Exception {
        int databaseSizeBeforeUpdate = nationalityRepository.findAll().size();
        nationality.setId(count.incrementAndGet());

        // Create the Nationality
        NationalityDTO nationalityDTO = nationalityMapper.toDto(nationality);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNationalityMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(nationalityDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Nationality in the database
        List<Nationality> nationalityList = nationalityRepository.findAll();
        assertThat(nationalityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNationality() throws Exception {
        // Initialize the database
        nationalityRepository.saveAndFlush(nationality);

        int databaseSizeBeforeDelete = nationalityRepository.findAll().size();

        // Delete the nationality
        restNationalityMockMvc
            .perform(delete(ENTITY_API_URL_ID, nationality.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Nationality> nationalityList = nationalityRepository.findAll();
        assertThat(nationalityList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
