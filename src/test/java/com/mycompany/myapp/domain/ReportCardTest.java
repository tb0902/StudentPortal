package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ReportCardTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReportCardTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReportCard.class);
        ReportCard reportCard1 = getReportCardSample1();
        ReportCard reportCard2 = new ReportCard();
        assertThat(reportCard1).isNotEqualTo(reportCard2);

        reportCard2.setId(reportCard1.getId());
        assertThat(reportCard1).isEqualTo(reportCard2);

        reportCard2 = getReportCardSample2();
        assertThat(reportCard1).isNotEqualTo(reportCard2);
    }
}
