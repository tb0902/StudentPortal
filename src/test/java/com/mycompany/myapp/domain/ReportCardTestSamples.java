package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ReportCardTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ReportCard getReportCardSample1() {
        return new ReportCard().id(1L).semester(1).classification("classification1").comments("comments1");
    }

    public static ReportCard getReportCardSample2() {
        return new ReportCard().id(2L).semester(2).classification("classification2").comments("comments2");
    }

    public static ReportCard getReportCardRandomSampleGenerator() {
        return new ReportCard()
            .id(longCount.incrementAndGet())
            .semester(intCount.incrementAndGet())
            .classification(UUID.randomUUID().toString())
            .comments(UUID.randomUUID().toString());
    }
}
