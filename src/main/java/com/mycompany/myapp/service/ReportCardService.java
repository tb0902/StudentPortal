package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.ReportCard;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.ReportCardRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.dto.ReportCardDTO;
import com.mycompany.myapp.service.mapper.ReportCardMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.ReportCard}.
 */
@Service
@Transactional
public class ReportCardService {

    private final Logger log = LoggerFactory.getLogger(ReportCardService.class);

    private final ReportCardRepository reportCardRepository;

    private final ReportCardMapper reportCardMapper;

    private final UserRepository userRepository;

    public ReportCardService(ReportCardRepository reportCardRepository, ReportCardMapper reportCardMapper, UserRepository userRepository) {
        this.reportCardRepository = reportCardRepository;
        this.reportCardMapper = reportCardMapper;
        this.userRepository = userRepository;
    }

    /**
     * Save a reportCard.
     *
     * @param reportCardDTO the entity to save.
     * @return the persisted entity.
     */
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ReportCardDTO save(ReportCardDTO reportCardDTO) {
        log.debug("Request to save ReportCard : {}", reportCardDTO);
        ReportCard reportCard = reportCardMapper.toEntity(reportCardDTO);

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElseThrow();
        User currentUser = userRepository.findOneByLogin(currentUserLogin).orElseThrow();
        reportCard.setStudent(currentUser);

        reportCard = reportCardRepository.save(reportCard);
        return reportCardMapper.toDto(reportCard);
    }

    /**
     * Update a reportCard.
     *
     * @param reportCardDTO the entity to save.
     * @return the persisted entity.
     */
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ReportCardDTO update(ReportCardDTO reportCardDTO) {
        log.debug("Request to update ReportCard : {}", reportCardDTO);

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElseThrow();

        ReportCard reportCard = reportCardMapper.toEntity(reportCardDTO);
        if (!reportCard.getStudent().getLogin().equals(currentUserLogin)) {
            throw new SecurityException("Students can only update their own report card.");
        }
        reportCard = reportCardRepository.save(reportCard);
        return reportCardMapper.toDto(reportCard);
    }

    /**
     * Partially update a reportCard.
     *
     * @param reportCardDTO the entity to update partially.
     * @return the persisted entity.
     */
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public Optional<ReportCardDTO> partialUpdate(ReportCardDTO reportCardDTO) {
        log.debug("Request to partially update ReportCard : {}", reportCardDTO);

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElseThrow();

        return reportCardRepository
            .findById(reportCardDTO.getId())
            .map(existingReportCard -> {
                if (!existingReportCard.getStudent().getLogin().equals(currentUserLogin)) {
                    throw new SecurityException("Students can only partially update their own report cards");
                }
                reportCardMapper.partialUpdate(existingReportCard, reportCardDTO);

                return existingReportCard;
            })
            .map(reportCardRepository::save)
            .map(reportCardMapper::toDto);
    }

    /**
     * Get all the reportCards.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public Page<ReportCardDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ReportCards");
        return reportCardRepository.findAll(pageable).map(reportCardMapper::toDto);
    }

    //
    // a 'findAllByStudentLogin' would need to be created, but this may not be needed
    //    @Transactional(readOnly = true)
    //    @PreAuthorize("hasRole('ROLE_STUDENT')")
    //    public Page<ReportCardDTO> findAllForCurrentStudent(Pageable pageable) {
    //        log.debug("Request to get all ReportCards for current student");
    //        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElseThrow();
    //        return reportCardRepository.findAllByStudentLogin(currentUserLogin, pageable).map(reportCardMapper::toDto);
    //    }

    /**
     * Get all the reportCards with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ReportCardDTO> findAllWithEagerRelationships(Pageable pageable) {
        return reportCardRepository.findAllWithEagerRelationships(pageable).map(reportCardMapper::toDto);
    }

    /**
     * Get one reportCard by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_TEACHER') or (hasRole('ROLE_STUDENT') and @securityService.isCurrentUserStudent(#id))")
    public Optional<ReportCardDTO> findOne(Long id) {
        log.debug("Request to get ReportCard : {}", id);
        return reportCardRepository.findOneWithEagerRelationships(id).map(reportCardMapper::toDto);
    }

    /**
     * Delete the reportCard by id.
     *
     * @param id the id of the entity.
     */
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public void delete(Long id) {
        log.debug("Request to delete ReportCard : {}", id);
        reportCardRepository.deleteById(id);
    }
}
