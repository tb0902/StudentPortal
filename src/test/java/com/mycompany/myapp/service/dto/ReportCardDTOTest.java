package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReportCardDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReportCardDTO.class);
        ReportCardDTO reportCardDTO1 = new ReportCardDTO();
        reportCardDTO1.setId(1L);
        ReportCardDTO reportCardDTO2 = new ReportCardDTO();
        assertThat(reportCardDTO1).isNotEqualTo(reportCardDTO2);
        reportCardDTO2.setId(reportCardDTO1.getId());
        assertThat(reportCardDTO1).isEqualTo(reportCardDTO2);
        reportCardDTO2.setId(2L);
        assertThat(reportCardDTO1).isNotEqualTo(reportCardDTO2);
        reportCardDTO1.setId(null);
        assertThat(reportCardDTO1).isNotEqualTo(reportCardDTO2);
    }
}
