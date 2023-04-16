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
import pbm.com.exchange.repository.ProductPurposeRepository;
import pbm.com.exchange.service.ProductPurposeService;
import pbm.com.exchange.service.dto.ProductPurposeDTO;
import pbm.com.exchange.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link pbm.com.exchange.domain.ProductPurpose}.
 */
@RestController
@RequestMapping("/api")
public class ProductPurposeResource {

    private final Logger log = LoggerFactory.getLogger(ProductPurposeResource.class);

    private static final String ENTITY_NAME = "productPurpose";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductPurposeService productPurposeService;

    private final ProductPurposeRepository productPurposeRepository;

    public ProductPurposeResource(ProductPurposeService productPurposeService, ProductPurposeRepository productPurposeRepository) {
        this.productPurposeService = productPurposeService;
        this.productPurposeRepository = productPurposeRepository;
    }

    /**
     * {@code POST  /product-purposes} : Create a new productPurpose.
     *
     * @param productPurposeDTO the productPurposeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productPurposeDTO, or with status {@code 400 (Bad Request)} if the productPurpose has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-purposes")
    public ResponseEntity<ProductPurposeDTO> createProductPurpose(@RequestBody ProductPurposeDTO productPurposeDTO)
        throws URISyntaxException {
        log.debug("REST request to save ProductPurpose : {}", productPurposeDTO);
        if (productPurposeDTO.getId() != null) {
            throw new BadRequestAlertException("A new productPurpose cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProductPurposeDTO result = productPurposeService.save(productPurposeDTO);
        return ResponseEntity
            .created(new URI("/api/product-purposes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /product-purposes/:id} : Updates an existing productPurpose.
     *
     * @param id the id of the productPurposeDTO to save.
     * @param productPurposeDTO the productPurposeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productPurposeDTO,
     * or with status {@code 400 (Bad Request)} if the productPurposeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productPurposeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/product-purposes/{id}")
    public ResponseEntity<ProductPurposeDTO> updateProductPurpose(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProductPurposeDTO productPurposeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProductPurpose : {}, {}", id, productPurposeDTO);
        if (productPurposeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productPurposeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productPurposeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ProductPurposeDTO result = productPurposeService.save(productPurposeDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productPurposeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /product-purposes/:id} : Partial updates given fields of an existing productPurpose, field will ignore if it is null
     *
     * @param id the id of the productPurposeDTO to save.
     * @param productPurposeDTO the productPurposeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productPurposeDTO,
     * or with status {@code 400 (Bad Request)} if the productPurposeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the productPurposeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the productPurposeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/product-purposes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProductPurposeDTO> partialUpdateProductPurpose(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProductPurposeDTO productPurposeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProductPurpose partially : {}, {}", id, productPurposeDTO);
        if (productPurposeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productPurposeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productPurposeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProductPurposeDTO> result = productPurposeService.partialUpdate(productPurposeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productPurposeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /product-purposes} : get all the productPurposes.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productPurposes in body.
     */
    @GetMapping("/product-purposes")
    public ResponseEntity<List<ProductPurposeDTO>> getAllProductPurposes(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of ProductPurposes");
        Page<ProductPurposeDTO> page = productPurposeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /product-purposes/:id} : get the "id" productPurpose.
     *
     * @param id the id of the productPurposeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productPurposeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/product-purposes/{id}")
    public ResponseEntity<ProductPurposeDTO> getProductPurpose(@PathVariable Long id) {
        log.debug("REST request to get ProductPurpose : {}", id);
        Optional<ProductPurposeDTO> productPurposeDTO = productPurposeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productPurposeDTO);
    }

    /**
     * {@code DELETE  /product-purposes/:id} : delete the "id" productPurpose.
     *
     * @param id the id of the productPurposeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/product-purposes/{id}")
    public ResponseEntity<Void> deleteProductPurpose(@PathVariable Long id) {
        log.debug("REST request to delete ProductPurpose : {}", id);
        productPurposeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
