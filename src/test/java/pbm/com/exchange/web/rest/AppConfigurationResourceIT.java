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
import pbm.com.exchange.domain.AppConfiguration;
import pbm.com.exchange.repository.AppConfigurationRepository;
import pbm.com.exchange.service.dto.AppConfigurationDTO;
import pbm.com.exchange.service.mapper.AppConfigurationMapper;

/**
 * Integration tests for the {@link AppConfigurationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AppConfigurationResourceIT {

    private static final String DEFAULT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/app-configurations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AppConfigurationRepository appConfigurationRepository;

    @Autowired
    private AppConfigurationMapper appConfigurationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAppConfigurationMockMvc;

    private AppConfiguration appConfiguration;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppConfiguration createEntity(EntityManager em) {
        AppConfiguration appConfiguration = new AppConfiguration().key(DEFAULT_KEY).value(DEFAULT_VALUE).note(DEFAULT_NOTE);
        return appConfiguration;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppConfiguration createUpdatedEntity(EntityManager em) {
        AppConfiguration appConfiguration = new AppConfiguration().key(UPDATED_KEY).value(UPDATED_VALUE).note(UPDATED_NOTE);
        return appConfiguration;
    }

    @BeforeEach
    public void initTest() {
        appConfiguration = createEntity(em);
    }

    @Test
    @Transactional
    void createAppConfiguration() throws Exception {
        int databaseSizeBeforeCreate = appConfigurationRepository.findAll().size();
        // Create the AppConfiguration
        AppConfigurationDTO appConfigurationDTO = appConfigurationMapper.toDto(appConfiguration);
        restAppConfigurationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appConfigurationDTO))
            )
            .andExpect(status().isCreated());

        // Validate the AppConfiguration in the database
        List<AppConfiguration> appConfigurationList = appConfigurationRepository.findAll();
        assertThat(appConfigurationList).hasSize(databaseSizeBeforeCreate + 1);
        AppConfiguration testAppConfiguration = appConfigurationList.get(appConfigurationList.size() - 1);
        assertThat(testAppConfiguration.getKey()).isEqualTo(DEFAULT_KEY);
        assertThat(testAppConfiguration.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testAppConfiguration.getNote()).isEqualTo(DEFAULT_NOTE);
    }

    @Test
    @Transactional
    void createAppConfigurationWithExistingId() throws Exception {
        // Create the AppConfiguration with an existing ID
        appConfiguration.setId(1L);
        AppConfigurationDTO appConfigurationDTO = appConfigurationMapper.toDto(appConfiguration);

        int databaseSizeBeforeCreate = appConfigurationRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAppConfigurationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appConfigurationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppConfiguration in the database
        List<AppConfiguration> appConfigurationList = appConfigurationRepository.findAll();
        assertThat(appConfigurationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAppConfigurations() throws Exception {
        // Initialize the database
        appConfigurationRepository.saveAndFlush(appConfiguration);

        // Get all the appConfigurationList
        restAppConfigurationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appConfiguration.getId().intValue())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @Test
    @Transactional
    void getAppConfiguration() throws Exception {
        // Initialize the database
        appConfigurationRepository.saveAndFlush(appConfiguration);

        // Get the appConfiguration
        restAppConfigurationMockMvc
            .perform(get(ENTITY_API_URL_ID, appConfiguration.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(appConfiguration.getId().intValue()))
            .andExpect(jsonPath("$.key").value(DEFAULT_KEY))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingAppConfiguration() throws Exception {
        // Get the appConfiguration
        restAppConfigurationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAppConfiguration() throws Exception {
        // Initialize the database
        appConfigurationRepository.saveAndFlush(appConfiguration);

        int databaseSizeBeforeUpdate = appConfigurationRepository.findAll().size();

        // Update the appConfiguration
        AppConfiguration updatedAppConfiguration = appConfigurationRepository.findById(appConfiguration.getId()).get();
        // Disconnect from session so that the updates on updatedAppConfiguration are not directly saved in db
        em.detach(updatedAppConfiguration);
        updatedAppConfiguration.key(UPDATED_KEY).value(UPDATED_VALUE).note(UPDATED_NOTE);
        AppConfigurationDTO appConfigurationDTO = appConfigurationMapper.toDto(updatedAppConfiguration);

        restAppConfigurationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, appConfigurationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(appConfigurationDTO))
            )
            .andExpect(status().isOk());

        // Validate the AppConfiguration in the database
        List<AppConfiguration> appConfigurationList = appConfigurationRepository.findAll();
        assertThat(appConfigurationList).hasSize(databaseSizeBeforeUpdate);
        AppConfiguration testAppConfiguration = appConfigurationList.get(appConfigurationList.size() - 1);
        assertThat(testAppConfiguration.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testAppConfiguration.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testAppConfiguration.getNote()).isEqualTo(UPDATED_NOTE);
    }

    @Test
    @Transactional
    void putNonExistingAppConfiguration() throws Exception {
        int databaseSizeBeforeUpdate = appConfigurationRepository.findAll().size();
        appConfiguration.setId(count.incrementAndGet());

        // Create the AppConfiguration
        AppConfigurationDTO appConfigurationDTO = appConfigurationMapper.toDto(appConfiguration);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppConfigurationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, appConfigurationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(appConfigurationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppConfiguration in the database
        List<AppConfiguration> appConfigurationList = appConfigurationRepository.findAll();
        assertThat(appConfigurationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAppConfiguration() throws Exception {
        int databaseSizeBeforeUpdate = appConfigurationRepository.findAll().size();
        appConfiguration.setId(count.incrementAndGet());

        // Create the AppConfiguration
        AppConfigurationDTO appConfigurationDTO = appConfigurationMapper.toDto(appConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppConfigurationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(appConfigurationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppConfiguration in the database
        List<AppConfiguration> appConfigurationList = appConfigurationRepository.findAll();
        assertThat(appConfigurationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAppConfiguration() throws Exception {
        int databaseSizeBeforeUpdate = appConfigurationRepository.findAll().size();
        appConfiguration.setId(count.incrementAndGet());

        // Create the AppConfiguration
        AppConfigurationDTO appConfigurationDTO = appConfigurationMapper.toDto(appConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppConfigurationMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appConfigurationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AppConfiguration in the database
        List<AppConfiguration> appConfigurationList = appConfigurationRepository.findAll();
        assertThat(appConfigurationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAppConfigurationWithPatch() throws Exception {
        // Initialize the database
        appConfigurationRepository.saveAndFlush(appConfiguration);

        int databaseSizeBeforeUpdate = appConfigurationRepository.findAll().size();

        // Update the appConfiguration using partial update
        AppConfiguration partialUpdatedAppConfiguration = new AppConfiguration();
        partialUpdatedAppConfiguration.setId(appConfiguration.getId());

        partialUpdatedAppConfiguration.key(UPDATED_KEY).value(UPDATED_VALUE);

        restAppConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppConfiguration.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAppConfiguration))
            )
            .andExpect(status().isOk());

        // Validate the AppConfiguration in the database
        List<AppConfiguration> appConfigurationList = appConfigurationRepository.findAll();
        assertThat(appConfigurationList).hasSize(databaseSizeBeforeUpdate);
        AppConfiguration testAppConfiguration = appConfigurationList.get(appConfigurationList.size() - 1);
        assertThat(testAppConfiguration.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testAppConfiguration.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testAppConfiguration.getNote()).isEqualTo(DEFAULT_NOTE);
    }

    @Test
    @Transactional
    void fullUpdateAppConfigurationWithPatch() throws Exception {
        // Initialize the database
        appConfigurationRepository.saveAndFlush(appConfiguration);

        int databaseSizeBeforeUpdate = appConfigurationRepository.findAll().size();

        // Update the appConfiguration using partial update
        AppConfiguration partialUpdatedAppConfiguration = new AppConfiguration();
        partialUpdatedAppConfiguration.setId(appConfiguration.getId());

        partialUpdatedAppConfiguration.key(UPDATED_KEY).value(UPDATED_VALUE).note(UPDATED_NOTE);

        restAppConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppConfiguration.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAppConfiguration))
            )
            .andExpect(status().isOk());

        // Validate the AppConfiguration in the database
        List<AppConfiguration> appConfigurationList = appConfigurationRepository.findAll();
        assertThat(appConfigurationList).hasSize(databaseSizeBeforeUpdate);
        AppConfiguration testAppConfiguration = appConfigurationList.get(appConfigurationList.size() - 1);
        assertThat(testAppConfiguration.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testAppConfiguration.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testAppConfiguration.getNote()).isEqualTo(UPDATED_NOTE);
    }

    @Test
    @Transactional
    void patchNonExistingAppConfiguration() throws Exception {
        int databaseSizeBeforeUpdate = appConfigurationRepository.findAll().size();
        appConfiguration.setId(count.incrementAndGet());

        // Create the AppConfiguration
        AppConfigurationDTO appConfigurationDTO = appConfigurationMapper.toDto(appConfiguration);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, appConfigurationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(appConfigurationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppConfiguration in the database
        List<AppConfiguration> appConfigurationList = appConfigurationRepository.findAll();
        assertThat(appConfigurationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAppConfiguration() throws Exception {
        int databaseSizeBeforeUpdate = appConfigurationRepository.findAll().size();
        appConfiguration.setId(count.incrementAndGet());

        // Create the AppConfiguration
        AppConfigurationDTO appConfigurationDTO = appConfigurationMapper.toDto(appConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(appConfigurationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppConfiguration in the database
        List<AppConfiguration> appConfigurationList = appConfigurationRepository.findAll();
        assertThat(appConfigurationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAppConfiguration() throws Exception {
        int databaseSizeBeforeUpdate = appConfigurationRepository.findAll().size();
        appConfiguration.setId(count.incrementAndGet());

        // Create the AppConfiguration
        AppConfigurationDTO appConfigurationDTO = appConfigurationMapper.toDto(appConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(appConfigurationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AppConfiguration in the database
        List<AppConfiguration> appConfigurationList = appConfigurationRepository.findAll();
        assertThat(appConfigurationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAppConfiguration() throws Exception {
        // Initialize the database
        appConfigurationRepository.saveAndFlush(appConfiguration);

        int databaseSizeBeforeDelete = appConfigurationRepository.findAll().size();

        // Delete the appConfiguration
        restAppConfigurationMockMvc
            .perform(delete(ENTITY_API_URL_ID, appConfiguration.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AppConfiguration> appConfigurationList = appConfigurationRepository.findAll();
        assertThat(appConfigurationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
