package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.buffer.BufferMetricReader;
import org.springframework.stereotype.Component;

@Component
public class TimeEntryHealthIndicator implements HealthIndicator {

    private BufferMetricReader metrics;

    public TimeEntryHealthIndicator(BufferMetricReader metrics) {
        this.metrics = metrics;
    }

    @Override
    public Health health() {

        Metric createdMetric = metrics.findOne(Metrics.ENTRY_CREATED.toString());
        Metric deletedMetric = metrics.findOne(Metrics.ENTRY_DELETED.toString());

        if (createdMetric != null && deletedMetric != null && createdMetric.getValue() != null && deletedMetric.getValue() != null) {
            if ((createdMetric.getValue().intValue() - deletedMetric.getValue().intValue()) < 5) {
                return  Health.up().build();
            } else {
                return Health.down().build();
            }

        }

        return Health.up().build();
    }

    public enum Metrics {
        ENTRY_CREATED,
        ENTRY_DELETED,
        ENTRY_UPDATED,
        ENTRY_READ,
        ENTRIES_LISTED
    }


}

