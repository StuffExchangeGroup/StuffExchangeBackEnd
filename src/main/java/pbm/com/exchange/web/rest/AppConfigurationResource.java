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
import pbm.com.exchange.repository.AppConfigurationRepository;
import pbm.com.exchange.service.AppConfigurationService;
import pbm.com.exchange.service.dto.AppConfigurationDTO;
import pbm.com.exchange.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link pbm.com.exchange.domain.AppConfiguration}.
 */
@RestController
@RequestMapping("/api")
public class AppConfigurationResource {

    private final Logger log = LoggerFactory.getLogger(AppConfigurationResource.class);

    private static final String ENTITY_NAME = "appConfiguration";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AppConfigurationService appConfigurationService;

    private final AppConfigurationRepository appConfigurationRepository;

    public AppConfigurationResource(
        AppConfigurationService appConfigurationService,
        AppConfigurationRepository appConfigurationRepository
    ) {
        this.appConfigurationService = appConfigurationService;
        this.appConfigurationRepository = appConfigurationRepository;
    }

    /**
     * {@code POST  /app-configurations} : Create a new appConfiguration.
     *
     * @param appConfigurationDTO the appConfigurationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new appConfigurationDTO, or with status {@code 400 (Bad Request)} if the appConfiguration has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/app-configurations")
    public ResponseEntity<AppConfigurationDTO> createAppConfiguration(@RequestBody AppConfigurationDTO appConfigurationDTO)
        throws URISyntaxException {
        log.debug("REST request to save AppConfiguration : {}", appConfigurationDTO);
        if (appConfigurationDTO.getId() != null) {
            throw new BadRequestAlertException("A new appConfiguration cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AppConfigurationDTO result = appConfigurationService.save(appConfigurationDTO);
        return ResponseEntity
            .created(new URI("/api/app-configurations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /app-configurations/:id} : Updates an existing appConfiguration.
     *
     * @param id the id of the appConfigurationDTO to save.
     * @param appConfigurationDTO the appConfigurationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appConfigurationDTO,
     * or with status {@code 400 (Bad Request)} if the appConfigurationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the appConfigurationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/app-configurations/{id}")
    public ResponseEntity<AppConfigurationDTO> updateAppConfiguration(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AppConfigurationDTO appConfigurationDTO
    ) throws URISyntaxException {
        log.debug("REST request to update AppConfiguration : {}, {}", id, appConfigurationDTO);
        if (appConfigurationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appConfigurationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!appConfigurationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AppConfigurationDTO result = appConfigurationService.save(appConfigurationDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, appConfigurationDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /app-configurations/:id} : Partial updates given fields of an existing appConfiguration, field will ignore if it is null
     *
     * @param id the id of the appConfigurationDTO to save.
     * @param appConfigurationDTO the appConfigurationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appConfigurationDTO,
     * or with status {@code 400 (Bad Request)} if the appConfigurationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the appConfigurationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the appConfigurationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/app-configurations/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AppConfigurationDTO> partialUpdateAppConfiguration(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AppConfigurationDTO appConfigurationDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update AppConfiguration partially : {}, {}", id, appConfigurationDTO);
        if (appConfigurationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appConfigurationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!appConfigurationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AppConfigurationDTO> result = appConfigurationService.partialUpdate(appConfigurationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, appConfigurationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /app-configurations} : get all the appConfigurations.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of appConfigurations in body.
     */
    @GetMapping("/app-configurations")
    public ResponseEntity<List<AppConfigurationDTO>> getAllAppConfigurations(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of AppConfigurations");
        Page<AppConfigurationDTO> page = appConfigurationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /app-configurations/:id} : get the "id" appConfiguration.
     *
     * @param id the id of the appConfigurationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the appConfigurationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/app-configurations/{id}")
    public ResponseEntity<AppConfigurationDTO> getAppConfiguration(@PathVariable Long id) {
        log.debug("REST request to get AppConfiguration : {}", id);
        Optional<AppConfigurationDTO> appConfigurationDTO = appConfigurationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(appConfigurationDTO);
    }

    /**
     * {@code DELETE  /app-configurations/:id} : delete the "id" appConfiguration.
     *
     * @param id the id of the appConfigurationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/app-configurations/{id}")
    public ResponseEntity<Void> deleteAppConfiguration(@PathVariable Long id) {
        log.debug("REST request to delete AppConfiguration : {}", id);
        appConfigurationService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
