package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.ReportCardAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.ReportCard;
import com.mycompany.myapp.repository.ReportCardRepository;
import com.mycompany.myapp.service.ReportCardService;
import com.mycompany.myapp.service.dto.ReportCardDTO;
import com.mycompany.myapp.service.mapper.ReportCardMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ReportCardResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ReportCardResourceIT {

    private static final Integer DEFAULT_SEMESTER = 1;
    private static final Integer UPDATED_SEMESTER = 2;

    private static final String DEFAULT_CLASSIFICATION = "AAAAAAAAAA";
    private static final String UPDATED_CLASSIFICATION = "BBBBBBBBBB";

    private static final byte[] DEFAULT_PDF_FILE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PDF_FILE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PDF_FILE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PDF_FILE_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_COMMENTS = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/report-cards";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReportCardRepository reportCardRepository;

    @Mock
    private ReportCardRepository reportCardRepositoryMock;

    @Autowired
    private ReportCardMapper reportCardMapper;

    @Mock
    private ReportCardService reportCardServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReportCardMockMvc;

    private ReportCard reportCard;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReportCard createEntity(EntityManager em) {
        ReportCard reportCard = new ReportCard()
            .semester(DEFAULT_SEMESTER)
            .classification(DEFAULT_CLASSIFICATION)
            .pdfFile(DEFAULT_PDF_FILE)
            .pdfFileContentType(DEFAULT_PDF_FILE_CONTENT_TYPE)
            .comments(DEFAULT_COMMENTS);
        return reportCard;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReportCard createUpdatedEntity(EntityManager em) {
        ReportCard reportCard = new ReportCard()
            .semester(UPDATED_SEMESTER)
            .classification(UPDATED_CLASSIFICATION)
            .pdfFile(UPDATED_PDF_FILE)
            .pdfFileContentType(UPDATED_PDF_FILE_CONTENT_TYPE)
            .comments(UPDATED_COMMENTS);
        return reportCard;
    }

    @BeforeEach
    public void initTest() {
        reportCard = createEntity(em);
    }

    @Test
    @Transactional
    void createReportCard() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ReportCard
        ReportCardDTO reportCardDTO = reportCardMapper.toDto(reportCard);
        var returnedReportCardDTO = om.readValue(
            restReportCardMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reportCardDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReportCardDTO.class
        );

        // Validate the ReportCard in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedReportCard = reportCardMapper.toEntity(returnedReportCardDTO);
        assertReportCardUpdatableFieldsEquals(returnedReportCard, getPersistedReportCard(returnedReportCard));
    }

    @Test
    @Transactional
    void createReportCardWithExistingId() throws Exception {
        // Create the ReportCard with an existing ID
        reportCard.setId(1L);
        ReportCardDTO reportCardDTO = reportCardMapper.toDto(reportCard);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReportCardMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reportCardDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ReportCard in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSemesterIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reportCard.setSemester(null);

        // Create the ReportCard, which fails.
        ReportCardDTO reportCardDTO = reportCardMapper.toDto(reportCard);

        restReportCardMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reportCardDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkClassificationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reportCard.setClassification(null);

        // Create the ReportCard, which fails.
        ReportCardDTO reportCardDTO = reportCardMapper.toDto(reportCard);

        restReportCardMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reportCardDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllReportCards() throws Exception {
        // Initialize the database
        reportCardRepository.saveAndFlush(reportCard);

        // Get all the reportCardList
        restReportCardMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reportCard.getId().intValue())))
            .andExpect(jsonPath("$.[*].semester").value(hasItem(DEFAULT_SEMESTER)))
            .andExpect(jsonPath("$.[*].classification").value(hasItem(DEFAULT_CLASSIFICATION)))
            .andExpect(jsonPath("$.[*].pdfFileContentType").value(hasItem(DEFAULT_PDF_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].pdfFile").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_PDF_FILE))))
            .andExpect(jsonPath("$.[*].comments").value(hasItem(DEFAULT_COMMENTS)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReportCardsWithEagerRelationshipsIsEnabled() throws Exception {
        when(reportCardServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReportCardMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(reportCardServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReportCardsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(reportCardServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReportCardMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(reportCardRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getReportCard() throws Exception {
        // Initialize the database
        reportCardRepository.saveAndFlush(reportCard);

        // Get the reportCard
        restReportCardMockMvc
            .perform(get(ENTITY_API_URL_ID, reportCard.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(reportCard.getId().intValue()))
            .andExpect(jsonPath("$.semester").value(DEFAULT_SEMESTER))
            .andExpect(jsonPath("$.classification").value(DEFAULT_CLASSIFICATION))
            .andExpect(jsonPath("$.pdfFileContentType").value(DEFAULT_PDF_FILE_CONTENT_TYPE))
            .andExpect(jsonPath("$.pdfFile").value(Base64.getEncoder().encodeToString(DEFAULT_PDF_FILE)))
            .andExpect(jsonPath("$.comments").value(DEFAULT_COMMENTS));
    }

    @Test
    @Transactional
    void getNonExistingReportCard() throws Exception {
        // Get the reportCard
        restReportCardMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReportCard() throws Exception {
        // Initialize the database
        reportCardRepository.saveAndFlush(reportCard);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the reportCard
        ReportCard updatedReportCard = reportCardRepository.findById(reportCard.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReportCard are not directly saved in db
        em.detach(updatedReportCard);
        updatedReportCard
            .semester(UPDATED_SEMESTER)
            .classification(UPDATED_CLASSIFICATION)
            .pdfFile(UPDATED_PDF_FILE)
            .pdfFileContentType(UPDATED_PDF_FILE_CONTENT_TYPE)
            .comments(UPDATED_COMMENTS);
        ReportCardDTO reportCardDTO = reportCardMapper.toDto(updatedReportCard);

        restReportCardMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reportCardDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reportCardDTO))
            )
            .andExpect(status().isOk());

        // Validate the ReportCard in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReportCardToMatchAllProperties(updatedReportCard);
    }

    @Test
    @Transactional
    void putNonExistingReportCard() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reportCard.setId(longCount.incrementAndGet());

        // Create the ReportCard
        ReportCardDTO reportCardDTO = reportCardMapper.toDto(reportCard);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReportCardMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reportCardDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reportCardDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReportCard in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReportCard() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reportCard.setId(longCount.incrementAndGet());

        // Create the ReportCard
        ReportCardDTO reportCardDTO = reportCardMapper.toDto(reportCard);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReportCardMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reportCardDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReportCard in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReportCard() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reportCard.setId(longCount.incrementAndGet());

        // Create the ReportCard
        ReportCardDTO reportCardDTO = reportCardMapper.toDto(reportCard);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReportCardMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reportCardDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReportCard in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReportCardWithPatch() throws Exception {
        // Initialize the database
        reportCardRepository.saveAndFlush(reportCard);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the reportCard using partial update
        ReportCard partialUpdatedReportCard = new ReportCard();
        partialUpdatedReportCard.setId(reportCard.getId());

        partialUpdatedReportCard.classification(UPDATED_CLASSIFICATION).comments(UPDATED_COMMENTS);

        restReportCardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReportCard.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReportCard))
            )
            .andExpect(status().isOk());

        // Validate the ReportCard in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReportCardUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedReportCard, reportCard),
            getPersistedReportCard(reportCard)
        );
    }

    @Test
    @Transactional
    void fullUpdateReportCardWithPatch() throws Exception {
        // Initialize the database
        reportCardRepository.saveAndFlush(reportCard);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the reportCard using partial update
        ReportCard partialUpdatedReportCard = new ReportCard();
        partialUpdatedReportCard.setId(reportCard.getId());

        partialUpdatedReportCard
            .semester(UPDATED_SEMESTER)
            .classification(UPDATED_CLASSIFICATION)
            .pdfFile(UPDATED_PDF_FILE)
            .pdfFileContentType(UPDATED_PDF_FILE_CONTENT_TYPE)
            .comments(UPDATED_COMMENTS);

        restReportCardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReportCard.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReportCard))
            )
            .andExpect(status().isOk());

        // Validate the ReportCard in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReportCardUpdatableFieldsEquals(partialUpdatedReportCard, getPersistedReportCard(partialUpdatedReportCard));
    }

    @Test
    @Transactional
    void patchNonExistingReportCard() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reportCard.setId(longCount.incrementAndGet());

        // Create the ReportCard
        ReportCardDTO reportCardDTO = reportCardMapper.toDto(reportCard);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReportCardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, reportCardDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(reportCardDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReportCard in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReportCard() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reportCard.setId(longCount.incrementAndGet());

        // Create the ReportCard
        ReportCardDTO reportCardDTO = reportCardMapper.toDto(reportCard);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReportCardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(reportCardDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReportCard in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReportCard() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reportCard.setId(longCount.incrementAndGet());

        // Create the ReportCard
        ReportCardDTO reportCardDTO = reportCardMapper.toDto(reportCard);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReportCardMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(reportCardDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReportCard in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReportCard() throws Exception {
        // Initialize the database
        reportCardRepository.saveAndFlush(reportCard);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the reportCard
        restReportCardMockMvc
            .perform(delete(ENTITY_API_URL_ID, reportCard.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return reportCardRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected ReportCard getPersistedReportCard(ReportCard reportCard) {
        return reportCardRepository.findById(reportCard.getId()).orElseThrow();
    }

    protected void assertPersistedReportCardToMatchAllProperties(ReportCard expectedReportCard) {
        assertReportCardAllPropertiesEquals(expectedReportCard, getPersistedReportCard(expectedReportCard));
    }

    protected void assertPersistedReportCardToMatchUpdatableProperties(ReportCard expectedReportCard) {
        assertReportCardAllUpdatablePropertiesEquals(expectedReportCard, getPersistedReportCard(expectedReportCard));
    }
}
