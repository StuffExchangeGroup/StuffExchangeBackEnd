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
import pbm.com.exchange.domain.Exchange;
import pbm.com.exchange.domain.enumeration.ExchangeStatus;
import pbm.com.exchange.repository.ExchangeRepository;
import pbm.com.exchange.service.dto.ExchangeDTO;
import pbm.com.exchange.service.mapper.ExchangeMapper;

/**
 * Integration tests for the {@link ExchangeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ExchangeResourceIT {

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final Boolean DEFAULT_OWNER_CONFIRM = false;
    private static final Boolean UPDATED_OWNER_CONFIRM = true;

    private static final Boolean DEFAULT_EXCHANGER_CONFIRM = false;
    private static final Boolean UPDATED_EXCHANGER_CONFIRM = true;

    private static final String DEFAULT_CONFIRM_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_CONFIRM_PHONE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_CHATTING = false;
    private static final Boolean UPDATED_CHATTING = true;

    private static final ExchangeStatus DEFAULT_STATUS = ExchangeStatus.WAITING;
    private static final ExchangeStatus UPDATED_STATUS = ExchangeStatus.SWAPPING;

    private static final String ENTITY_API_URL = "/api/exchanges";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ExchangeRepository exchangeRepository;

    @Autowired
    private ExchangeMapper exchangeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restExchangeMockMvc;

    private Exchange exchange;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Exchange createEntity(EntityManager em) {
        Exchange exchange = new Exchange()
            .active(DEFAULT_ACTIVE)
            .ownerConfirm(DEFAULT_OWNER_CONFIRM)
            .exchangerConfirm(DEFAULT_EXCHANGER_CONFIRM)
            .confirmPhone(DEFAULT_CONFIRM_PHONE)
            .chatting(DEFAULT_CHATTING)
            .status(DEFAULT_STATUS);
        return exchange;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Exchange createUpdatedEntity(EntityManager em) {
        Exchange exchange = new Exchange()
            .active(UPDATED_ACTIVE)
            .ownerConfirm(UPDATED_OWNER_CONFIRM)
            .exchangerConfirm(UPDATED_EXCHANGER_CONFIRM)
            .confirmPhone(UPDATED_CONFIRM_PHONE)
            .chatting(UPDATED_CHATTING)
            .status(UPDATED_STATUS);
        return exchange;
    }

    @BeforeEach
    public void initTest() {
        exchange = createEntity(em);
    }

    @Test
    @Transactional
    void createExchange() throws Exception {
        int databaseSizeBeforeCreate = exchangeRepository.findAll().size();
        // Create the Exchange
        ExchangeDTO exchangeDTO = exchangeMapper.toDto(exchange);
        restExchangeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(exchangeDTO)))
            .andExpect(status().isCreated());

        // Validate the Exchange in the database
        List<Exchange> exchangeList = exchangeRepository.findAll();
        assertThat(exchangeList).hasSize(databaseSizeBeforeCreate + 1);
        Exchange testExchange = exchangeList.get(exchangeList.size() - 1);
        assertThat(testExchange.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testExchange.getOwnerConfirm()).isEqualTo(DEFAULT_OWNER_CONFIRM);
        assertThat(testExchange.getExchangerConfirm()).isEqualTo(DEFAULT_EXCHANGER_CONFIRM);
        assertThat(testExchange.getConfirmPhone()).isEqualTo(DEFAULT_CONFIRM_PHONE);
        assertThat(testExchange.getChatting()).isEqualTo(DEFAULT_CHATTING);
        assertThat(testExchange.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createExchangeWithExistingId() throws Exception {
        // Create the Exchange with an existing ID
        exchange.setId(1L);
        ExchangeDTO exchangeDTO = exchangeMapper.toDto(exchange);

        int databaseSizeBeforeCreate = exchangeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restExchangeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(exchangeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Exchange in the database
        List<Exchange> exchangeList = exchangeRepository.findAll();
        assertThat(exchangeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllExchanges() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        // Get all the exchangeList
        restExchangeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(exchange.getId().intValue())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].ownerConfirm").value(hasItem(DEFAULT_OWNER_CONFIRM.booleanValue())))
            .andExpect(jsonPath("$.[*].exchangerConfirm").value(hasItem(DEFAULT_EXCHANGER_CONFIRM.booleanValue())))
            .andExpect(jsonPath("$.[*].confirmPhone").value(hasItem(DEFAULT_CONFIRM_PHONE)))
            .andExpect(jsonPath("$.[*].chatting").value(hasItem(DEFAULT_CHATTING.booleanValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getExchange() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        // Get the exchange
        restExchangeMockMvc
            .perform(get(ENTITY_API_URL_ID, exchange.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(exchange.getId().intValue()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.ownerConfirm").value(DEFAULT_OWNER_CONFIRM.booleanValue()))
            .andExpect(jsonPath("$.exchangerConfirm").value(DEFAULT_EXCHANGER_CONFIRM.booleanValue()))
            .andExpect(jsonPath("$.confirmPhone").value(DEFAULT_CONFIRM_PHONE))
            .andExpect(jsonPath("$.chatting").value(DEFAULT_CHATTING.booleanValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingExchange() throws Exception {
        // Get the exchange
        restExchangeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewExchange() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        int databaseSizeBeforeUpdate = exchangeRepository.findAll().size();

        // Update the exchange
        Exchange updatedExchange = exchangeRepository.findById(exchange.getId()).get();
        // Disconnect from session so that the updates on updatedExchange are not directly saved in db
        em.detach(updatedExchange);
        updatedExchange
            .active(UPDATED_ACTIVE)
            .ownerConfirm(UPDATED_OWNER_CONFIRM)
            .exchangerConfirm(UPDATED_EXCHANGER_CONFIRM)
            .confirmPhone(UPDATED_CONFIRM_PHONE)
            .chatting(UPDATED_CHATTING)
            .status(UPDATED_STATUS);
        ExchangeDTO exchangeDTO = exchangeMapper.toDto(updatedExchange);

        restExchangeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, exchangeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(exchangeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Exchange in the database
        List<Exchange> exchangeList = exchangeRepository.findAll();
        assertThat(exchangeList).hasSize(databaseSizeBeforeUpdate);
        Exchange testExchange = exchangeList.get(exchangeList.size() - 1);
        assertThat(testExchange.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testExchange.getOwnerConfirm()).isEqualTo(UPDATED_OWNER_CONFIRM);
        assertThat(testExchange.getExchangerConfirm()).isEqualTo(UPDATED_EXCHANGER_CONFIRM);
        assertThat(testExchange.getConfirmPhone()).isEqualTo(UPDATED_CONFIRM_PHONE);
        assertThat(testExchange.getChatting()).isEqualTo(UPDATED_CHATTING);
        assertThat(testExchange.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingExchange() throws Exception {
        int databaseSizeBeforeUpdate = exchangeRepository.findAll().size();
        exchange.setId(count.incrementAndGet());

        // Create the Exchange
        ExchangeDTO exchangeDTO = exchangeMapper.toDto(exchange);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExchangeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, exchangeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(exchangeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Exchange in the database
        List<Exchange> exchangeList = exchangeRepository.findAll();
        assertThat(exchangeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchExchange() throws Exception {
        int databaseSizeBeforeUpdate = exchangeRepository.findAll().size();
        exchange.setId(count.incrementAndGet());

        // Create the Exchange
        ExchangeDTO exchangeDTO = exchangeMapper.toDto(exchange);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExchangeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(exchangeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Exchange in the database
        List<Exchange> exchangeList = exchangeRepository.findAll();
        assertThat(exchangeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamExchange() throws Exception {
        int databaseSizeBeforeUpdate = exchangeRepository.findAll().size();
        exchange.setId(count.incrementAndGet());

        // Create the Exchange
        ExchangeDTO exchangeDTO = exchangeMapper.toDto(exchange);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExchangeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(exchangeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Exchange in the database
        List<Exchange> exchangeList = exchangeRepository.findAll();
        assertThat(exchangeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateExchangeWithPatch() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        int databaseSizeBeforeUpdate = exchangeRepository.findAll().size();

        // Update the exchange using partial update
        Exchange partialUpdatedExchange = new Exchange();
        partialUpdatedExchange.setId(exchange.getId());

        partialUpdatedExchange.ownerConfirm(UPDATED_OWNER_CONFIRM).confirmPhone(UPDATED_CONFIRM_PHONE).status(UPDATED_STATUS);

        restExchangeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExchange.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedExchange))
            )
            .andExpect(status().isOk());

        // Validate the Exchange in the database
        List<Exchange> exchangeList = exchangeRepository.findAll();
        assertThat(exchangeList).hasSize(databaseSizeBeforeUpdate);
        Exchange testExchange = exchangeList.get(exchangeList.size() - 1);
        assertThat(testExchange.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testExchange.getOwnerConfirm()).isEqualTo(UPDATED_OWNER_CONFIRM);
        assertThat(testExchange.getExchangerConfirm()).isEqualTo(DEFAULT_EXCHANGER_CONFIRM);
        assertThat(testExchange.getConfirmPhone()).isEqualTo(UPDATED_CONFIRM_PHONE);
        assertThat(testExchange.getChatting()).isEqualTo(DEFAULT_CHATTING);
        assertThat(testExchange.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateExchangeWithPatch() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        int databaseSizeBeforeUpdate = exchangeRepository.findAll().size();

        // Update the exchange using partial update
        Exchange partialUpdatedExchange = new Exchange();
        partialUpdatedExchange.setId(exchange.getId());

        partialUpdatedExchange
            .active(UPDATED_ACTIVE)
            .ownerConfirm(UPDATED_OWNER_CONFIRM)
            .exchangerConfirm(UPDATED_EXCHANGER_CONFIRM)
            .confirmPhone(UPDATED_CONFIRM_PHONE)
            .chatting(UPDATED_CHATTING)
            .status(UPDATED_STATUS);

        restExchangeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExchange.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedExchange))
            )
            .andExpect(status().isOk());

        // Validate the Exchange in the database
        List<Exchange> exchangeList = exchangeRepository.findAll();
        assertThat(exchangeList).hasSize(databaseSizeBeforeUpdate);
        Exchange testExchange = exchangeList.get(exchangeList.size() - 1);
        assertThat(testExchange.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testExchange.getOwnerConfirm()).isEqualTo(UPDATED_OWNER_CONFIRM);
        assertThat(testExchange.getExchangerConfirm()).isEqualTo(UPDATED_EXCHANGER_CONFIRM);
        assertThat(testExchange.getConfirmPhone()).isEqualTo(UPDATED_CONFIRM_PHONE);
        assertThat(testExchange.getChatting()).isEqualTo(UPDATED_CHATTING);
        assertThat(testExchange.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingExchange() throws Exception {
        int databaseSizeBeforeUpdate = exchangeRepository.findAll().size();
        exchange.setId(count.incrementAndGet());

        // Create the Exchange
        ExchangeDTO exchangeDTO = exchangeMapper.toDto(exchange);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExchangeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, exchangeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(exchangeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Exchange in the database
        List<Exchange> exchangeList = exchangeRepository.findAll();
        assertThat(exchangeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchExchange() throws Exception {
        int databaseSizeBeforeUpdate = exchangeRepository.findAll().size();
        exchange.setId(count.incrementAndGet());

        // Create the Exchange
        ExchangeDTO exchangeDTO = exchangeMapper.toDto(exchange);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExchangeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(exchangeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Exchange in the database
        List<Exchange> exchangeList = exchangeRepository.findAll();
        assertThat(exchangeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamExchange() throws Exception {
        int databaseSizeBeforeUpdate = exchangeRepository.findAll().size();
        exchange.setId(count.incrementAndGet());

        // Create the Exchange
        ExchangeDTO exchangeDTO = exchangeMapper.toDto(exchange);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExchangeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(exchangeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Exchange in the database
        List<Exchange> exchangeList = exchangeRepository.findAll();
        assertThat(exchangeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteExchange() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        int databaseSizeBeforeDelete = exchangeRepository.findAll().size();

        // Delete the exchange
        restExchangeMockMvc
            .perform(delete(ENTITY_API_URL_ID, exchange.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Exchange> exchangeList = exchangeRepository.findAll();
        assertThat(exchangeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
