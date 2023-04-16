package pbm.com.exchange.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pbm.com.exchange.repository.NotificationTokenRepository;
import pbm.com.exchange.service.NotificationTokenService;
import pbm.com.exchange.service.dto.NotificationTokenDTO;
import pbm.com.exchange.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link pbm.com.exchange.domain.NotificationToken}.
 */
@RestController
@RequestMapping("/api")
public class NotificationTokenResource {

    private final Logger log = LoggerFactory.getLogger(NotificationTokenResource.class);

    private static final String ENTITY_NAME = "notificationToken";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NotificationTokenService notificationTokenService;

    private final NotificationTokenRepository notificationTokenRepository;

    public NotificationTokenResource(
        NotificationTokenService notificationTokenService,
        NotificationTokenRepository notificationTokenRepository
    ) {
        this.notificationTokenService = notificationTokenService;
        this.notificationTokenRepository = notificationTokenRepository;
    }

    /**
     * {@code POST  /notification-tokens} : Create a new notificationToken.
     *
     * @param notificationTokenDTO the notificationTokenDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new notificationTokenDTO, or with status {@code 400 (Bad Request)} if the notificationToken has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/notification-tokens")
    public ResponseEntity<NotificationTokenDTO> createNotificationToken(@RequestBody NotificationTokenDTO notificationTokenDTO)
        throws URISyntaxException {
        log.debug("REST request to save NotificationToken : {}", notificationTokenDTO);
        if (notificationTokenDTO.getId() != null) {
            throw new BadRequestAlertException("A new notificationToken cannot already have an ID", ENTITY_NAME, "idexists");
        }
        NotificationTokenDTO result = notificationTokenService.save(notificationTokenDTO);
        return ResponseEntity
            .created(new URI("/api/notification-tokens/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /notification-tokens/:id} : Updates an existing notificationToken.
     *
     * @param id the id of the notificationTokenDTO to save.
     * @param notificationTokenDTO the notificationTokenDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationTokenDTO,
     * or with status {@code 400 (Bad Request)} if the notificationTokenDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the notificationTokenDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/notification-tokens/{id}")
    public ResponseEntity<NotificationTokenDTO> updateNotificationToken(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody NotificationTokenDTO notificationTokenDTO
    ) throws URISyntaxException {
        log.debug("REST request to update NotificationToken : {}, {}", id, notificationTokenDTO);
        if (notificationTokenDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notificationTokenDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notificationTokenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        NotificationTokenDTO result = notificationTokenService.save(notificationTokenDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notificationTokenDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /notification-tokens/:id} : Partial updates given fields of an existing notificationToken, field will ignore if it is null
     *
     * @param id the id of the notificationTokenDTO to save.
     * @param notificationTokenDTO the notificationTokenDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationTokenDTO,
     * or with status {@code 400 (Bad Request)} if the notificationTokenDTO is not valid,
     * or with status {@code 404 (Not Found)} if the notificationTokenDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the notificationTokenDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/notification-tokens/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<NotificationTokenDTO> partialUpdateNotificationToken(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody NotificationTokenDTO notificationTokenDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update NotificationToken partially : {}, {}", id, notificationTokenDTO);
        if (notificationTokenDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notificationTokenDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notificationTokenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<NotificationTokenDTO> result = notificationTokenService.partialUpdate(notificationTokenDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notificationTokenDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /notification-tokens} : get all the notificationTokens.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notificationTokens in body.
     */
    @GetMapping("/notification-tokens")
    public ResponseEntity<List<NotificationTokenDTO>> getAllNotificationTokens(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of NotificationTokens");
        Page<NotificationTokenDTO> page = notificationTokenService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /notification-tokens/:id} : get the "id" notificationToken.
     *
     * @param id the id of the notificationTokenDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notificationTokenDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/notification-tokens/{id}")
    public ResponseEntity<NotificationTokenDTO> getNotificationToken(@PathVariable Long id) {
        log.debug("REST request to get NotificationToken : {}", id);
        Optional<NotificationTokenDTO> notificationTokenDTO = notificationTokenService.findOne(id);
        return ResponseUtil.wrapOrNotFound(notificationTokenDTO);
    }

    /**
     * {@code DELETE  /notification-tokens/:id} : delete the "id" notificationToken.
     *
     * @param id the id of the notificationTokenDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/notification-tokens/{id}")
    public ResponseEntity<Void> deleteNotificationToken(@PathVariable Long id) {
        log.debug("REST request to delete NotificationToken : {}", id);
        notificationTokenService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
