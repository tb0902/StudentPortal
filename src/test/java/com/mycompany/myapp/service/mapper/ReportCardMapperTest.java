package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.ReportCardAsserts.*;
import static com.mycompany.myapp.domain.ReportCardTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReportCardMapperTest {

    private ReportCardMapper reportCardMapper;

    @BeforeEach
    void setUp() {
        reportCardMapper = new ReportCardMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getReportCardSample1();
        var actual = reportCardMapper.toEntity(reportCardMapper.toDto(expected));
        assertReportCardAllPropertiesEquals(expected, actual);
    }
}
