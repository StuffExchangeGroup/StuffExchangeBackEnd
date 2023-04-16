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
import pbm.com.exchange.domain.Level;
import pbm.com.exchange.repository.LevelRepository;
import pbm.com.exchange.service.dto.LevelDTO;
import pbm.com.exchange.service.mapper.LevelMapper;

/**
 * Integration tests for the {@link LevelResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LevelResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_SWAP_LIMIT = 1;
    private static final Integer UPDATED_SWAP_LIMIT = 2;

    private static final String ENTITY_API_URL = "/api/levels";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LevelRepository levelRepository;

    @Autowired
    private LevelMapper levelMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLevelMockMvc;

    private Level level;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Level createEntity(EntityManager em) {
        Level level = new Level().name(DEFAULT_NAME).swapLimit(DEFAULT_SWAP_LIMIT);
        return level;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Level createUpdatedEntity(EntityManager em) {
        Level level = new Level().name(UPDATED_NAME).swapLimit(UPDATED_SWAP_LIMIT);
        return level;
    }

    @BeforeEach
    public void initTest() {
        level = createEntity(em);
    }

    @Test
    @Transactional
    void createLevel() throws Exception {
        int databaseSizeBeforeCreate = levelRepository.findAll().size();
        // Create the Level
        LevelDTO levelDTO = levelMapper.toDto(level);
        restLevelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(levelDTO)))
            .andExpect(status().isCreated());

        // Validate the Level in the database
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeCreate + 1);
        Level testLevel = levelList.get(levelList.size() - 1);
        assertThat(testLevel.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testLevel.getSwapLimit()).isEqualTo(DEFAULT_SWAP_LIMIT);
    }

    @Test
    @Transactional
    void createLevelWithExistingId() throws Exception {
        // Create the Level with an existing ID
        level.setId(1L);
        LevelDTO levelDTO = levelMapper.toDto(level);

        int databaseSizeBeforeCreate = levelRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLevelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(levelDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Level in the database
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllLevels() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        // Get all the levelList
        restLevelMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(level.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].swapLimit").value(hasItem(DEFAULT_SWAP_LIMIT)));
    }

    @Test
    @Transactional
    void getLevel() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        // Get the level
        restLevelMockMvc
            .perform(get(ENTITY_API_URL_ID, level.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(level.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.swapLimit").value(DEFAULT_SWAP_LIMIT));
    }

    @Test
    @Transactional
    void getNonExistingLevel() throws Exception {
        // Get the level
        restLevelMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewLevel() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        int databaseSizeBeforeUpdate = levelRepository.findAll().size();

        // Update the level
        Level updatedLevel = levelRepository.findById(level.getId()).get();
        // Disconnect from session so that the updates on updatedLevel are not directly saved in db
        em.detach(updatedLevel);
        updatedLevel.name(UPDATED_NAME).swapLimit(UPDATED_SWAP_LIMIT);
        LevelDTO levelDTO = levelMapper.toDto(updatedLevel);

        restLevelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, levelDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(levelDTO))
            )
            .andExpect(status().isOk());

        // Validate the Level in the database
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeUpdate);
        Level testLevel = levelList.get(levelList.size() - 1);
        assertThat(testLevel.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLevel.getSwapLimit()).isEqualTo(UPDATED_SWAP_LIMIT);
    }

    @Test
    @Transactional
    void putNonExistingLevel() throws Exception {
        int databaseSizeBeforeUpdate = levelRepository.findAll().size();
        level.setId(count.incrementAndGet());

        // Create the Level
        LevelDTO levelDTO = levelMapper.toDto(level);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLevelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, levelDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(levelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Level in the database
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLevel() throws Exception {
        int databaseSizeBeforeUpdate = levelRepository.findAll().size();
        level.setId(count.incrementAndGet());

        // Create the Level
        LevelDTO levelDTO = levelMapper.toDto(level);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLevelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(levelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Level in the database
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLevel() throws Exception {
        int databaseSizeBeforeUpdate = levelRepository.findAll().size();
        level.setId(count.incrementAndGet());

        // Create the Level
        LevelDTO levelDTO = levelMapper.toDto(level);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLevelMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(levelDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Level in the database
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLevelWithPatch() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        int databaseSizeBeforeUpdate = levelRepository.findAll().size();

        // Update the level using partial update
        Level partialUpdatedLevel = new Level();
        partialUpdatedLevel.setId(level.getId());

        restLevelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLevel.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLevel))
            )
            .andExpect(status().isOk());

        // Validate the Level in the database
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeUpdate);
        Level testLevel = levelList.get(levelList.size() - 1);
        assertThat(testLevel.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testLevel.getSwapLimit()).isEqualTo(DEFAULT_SWAP_LIMIT);
    }

    @Test
    @Transactional
    void fullUpdateLevelWithPatch() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        int databaseSizeBeforeUpdate = levelRepository.findAll().size();

        // Update the level using partial update
        Level partialUpdatedLevel = new Level();
        partialUpdatedLevel.setId(level.getId());

        partialUpdatedLevel.name(UPDATED_NAME).swapLimit(UPDATED_SWAP_LIMIT);

        restLevelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLevel.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLevel))
            )
            .andExpect(status().isOk());

        // Validate the Level in the database
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeUpdate);
        Level testLevel = levelList.get(levelList.size() - 1);
        assertThat(testLevel.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLevel.getSwapLimit()).isEqualTo(UPDATED_SWAP_LIMIT);
    }

    @Test
    @Transactional
    void patchNonExistingLevel() throws Exception {
        int databaseSizeBeforeUpdate = levelRepository.findAll().size();
        level.setId(count.incrementAndGet());

        // Create the Level
        LevelDTO levelDTO = levelMapper.toDto(level);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLevelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, levelDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(levelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Level in the database
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLevel() throws Exception {
        int databaseSizeBeforeUpdate = levelRepository.findAll().size();
        level.setId(count.incrementAndGet());

        // Create the Level
        LevelDTO levelDTO = levelMapper.toDto(level);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLevelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(levelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Level in the database
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLevel() throws Exception {
        int databaseSizeBeforeUpdate = levelRepository.findAll().size();
        level.setId(count.incrementAndGet());

        // Create the Level
        LevelDTO levelDTO = levelMapper.toDto(level);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLevelMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(levelDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Level in the database
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLevel() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        int databaseSizeBeforeDelete = levelRepository.findAll().size();

        // Delete the level
        restLevelMockMvc
            .perform(delete(ENTITY_API_URL_ID, level.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
