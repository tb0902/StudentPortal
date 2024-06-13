package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.ReportCardRepository;
import com.mycompany.myapp.service.ReportCardService;
import com.mycompany.myapp.service.dto.ReportCardDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.ReportCard}.
 */
@RestController
@RequestMapping("/api/report-cards")
public class ReportCardResource {

    private final Logger log = LoggerFactory.getLogger(ReportCardResource.class);

    private static final String ENTITY_NAME = "reportCard";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReportCardService reportCardService;

    private final ReportCardRepository reportCardRepository;

    public ReportCardResource(ReportCardService reportCardService, ReportCardRepository reportCardRepository) {
        this.reportCardService = reportCardService;
        this.reportCardRepository = reportCardRepository;
    }

    /**
     * {@code POST  /report-cards} : Create a new reportCard.
     *
     * @param reportCardDTO the reportCardDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new reportCardDTO, or with status {@code 400 (Bad Request)} if the reportCard has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ReportCardDTO> createReportCard(@Valid @RequestBody ReportCardDTO reportCardDTO) throws URISyntaxException {
        log.debug("REST request to save ReportCard : {}", reportCardDTO);
        if (reportCardDTO.getId() != null) {
            throw new BadRequestAlertException("A new reportCard cannot already have an ID", ENTITY_NAME, "idexists");
        }
        reportCardDTO = reportCardService.save(reportCardDTO);
        return ResponseEntity.created(new URI("/api/report-cards/" + reportCardDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, reportCardDTO.getId().toString()))
            .body(reportCardDTO);
    }

    /**
     * {@code PUT  /report-cards/:id} : Updates an existing reportCard.
     *
     * @param id the id of the reportCardDTO to save.
     * @param reportCardDTO the reportCardDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reportCardDTO,
     * or with status {@code 400 (Bad Request)} if the reportCardDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the reportCardDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReportCardDTO> updateReportCard(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ReportCardDTO reportCardDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ReportCard : {}, {}", id, reportCardDTO);
        if (reportCardDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reportCardDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reportCardRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        reportCardDTO = reportCardService.update(reportCardDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reportCardDTO.getId().toString()))
            .body(reportCardDTO);
    }

    /**
     * {@code PATCH  /report-cards/:id} : Partial updates given fields of an existing reportCard, field will ignore if it is null
     *
     * @param id the id of the reportCardDTO to save.
     * @param reportCardDTO the reportCardDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reportCardDTO,
     * or with status {@code 400 (Bad Request)} if the reportCardDTO is not valid,
     * or with status {@code 404 (Not Found)} if the reportCardDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the reportCardDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ReportCardDTO> partialUpdateReportCard(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ReportCardDTO reportCardDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ReportCard partially : {}, {}", id, reportCardDTO);
        if (reportCardDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reportCardDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reportCardRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ReportCardDTO> result = reportCardService.partialUpdate(reportCardDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reportCardDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /report-cards} : get all the reportCards.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of reportCards in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ReportCardDTO>> getAllReportCards(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of ReportCards");
        Page<ReportCardDTO> page;
        if (eagerload) {
            page = reportCardService.findAllWithEagerRelationships(pageable);
        } else {
            page = reportCardService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /report-cards/:id} : get the "id" reportCard.
     *
     * @param id the id of the reportCardDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the reportCardDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReportCardDTO> getReportCard(@PathVariable("id") Long id) {
        log.debug("REST request to get ReportCard : {}", id);
        Optional<ReportCardDTO> reportCardDTO = reportCardService.findOne(id);
        return ResponseUtil.wrapOrNotFound(reportCardDTO);
    }

    /**
     * {@code DELETE  /report-cards/:id} : delete the "id" reportCard.
     *
     * @param id the id of the reportCardDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReportCard(@PathVariable("id") Long id) {
        log.debug("REST request to delete ReportCard : {}", id);
        reportCardService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
