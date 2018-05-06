package com.example.metrics.interval.entities.factory;

import com.example.metrics.interval.entities.Interval;
import org.springframework.stereotype.Component;

@Component
public class IntervalFactoryImpl implements IntervalFactory {

    public Interval createInterval(IntervalFactoryParam param) {
        if (param.isAllValuesAreTheSame()) {
            if (param.getValues()[0] == 0d) {
                return ZeroInterval.Builder.newInstance()
                        .startTimestamp(param.getStartTimestamp())
                        .endTimestamp(param.getEndTimestamp())
                        .secondsPerPoint(param.getSecondsPerPoint())
                        .build();
            }
            return ConstInterval.Builder.newInstance()
                    .startTimestamp(param.getStartTimestamp())
                    .endTimestamp(param.getEndTimestamp())
                    .secondsPerPoint(param.getSecondsPerPoint())
                    .value(param.getValues()[0])//todo
                    .build();
        }

        return SimpleInterval.Builder.newInstance()
                .startTimestamp(param.getStartTimestamp())
                .secondsPerPoint(param.getSecondsPerPoint())
                .values(param.getValues())
                .build();
    }
}