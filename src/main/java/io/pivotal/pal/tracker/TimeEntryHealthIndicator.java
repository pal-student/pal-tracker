package io.pivotal.pal.tracker;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TimeEntryHealthIndicator implements HealthIndicator {


    private TimeEntryRepository timeEntriesRepo;

    public TimeEntryHealthIndicator(TimeEntryRepository timeEntriesRepo) {
        this.timeEntriesRepo = timeEntriesRepo;
    }

    @Override
    public Health health() {

        if (timeEntriesRepo.list() != null && timeEntriesRepo.list().size() < 5) {
            return  Health.up().build();
        }

        return Health.down().build();
    }

    public enum Metrics {
        TIME_ENTRY_CREATED,
        TIME_ENTRY_DELETED,
        TIME_ENTRY_UPDATED,
        TIME_ENTRY_READ,
        TIME_ENTRIES_LISTED
    }
}

