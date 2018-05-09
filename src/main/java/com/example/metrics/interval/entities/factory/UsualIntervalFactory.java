package com.example.metrics.interval.entities.factory;


import com.example.metrics.interval.entities.Interval;
import com.example.metrics.interval.entities.Period;
import com.example.metrics.interval.entities.StorableInterval;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UsualIntervalFactory implements IntervalFactory {

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

        if (!param.getPeriodsWithTheSameValues().isEmpty()) {
            List<Interval> intervals = new ArrayList<>();
            for (IntervalFactoryParam fragParam : getFragmentedIntervalFactoryParams(param.getPeriodsWithTheSameValues(), param)) {
                intervals.add(createInterval(fragParam));
            }
            return FragmentedInterval.Builder.newInstance()
                    .startTimestamp(param.getStartTimestamp())
                    .endTimestamp(param.getEndTimestamp())
                    .secondsPerPoint(param.getSecondsPerPoint())
                    .intervals(intervals)
                    .build();
        }

        StorableInterval storableInterval = ArrayInterval.Builder.newInstance()
                .startTimestamp(param.getStartTimestamp())
                .endTimestamp(param.getEndTimestamp())
                .secondsPerPoint(param.getSecondsPerPoint())
                .values(param.getValues())
                .build();

        storableInterval.storeValues("c:\\temp\\tmp\\");//todo
        return storableInterval;
    }

    private List<IntervalFactoryParam> getFragmentedIntervalFactoryParams(Set<Period> periodsWithTheSameValues,
                                                                           IntervalFactoryParam param) {
        double[] values = param.getValues();
        int start = param.getStartTimestamp();
        int end = param.getEndTimestamp();
        int secondPerPoint = param.getSecondsPerPoint();

        List<Period> existPeriods = new ArrayList<>();
        existPeriods.addAll(periodsWithTheSameValues);

        Set<Period> allPeriods = new TreeSet<>((o1, o2) -> o1.getStartTimestamp() - o2.getStartTimestamp());
        allPeriods.addAll(periodsWithTheSameValues);

        if (start != existPeriods.get(0).getStartTimestamp()) {
            Period period = new Period(start, existPeriods.get(0).getStartTimestamp() - secondPerPoint, secondPerPoint);
            allPeriods.add(period);

        }
        if (end != existPeriods.get(existPeriods.size() - 1).getEndTimestamp()) {
            Period period = new Period(existPeriods.get(existPeriods.size() - 1).getEndTimestamp() + secondPerPoint, end, secondPerPoint);
            allPeriods.add(period);
        }

        for (int i = 0; i < existPeriods.size() - 1; i++) {
            if (existPeriods.get(i).getEndTimestamp() + secondPerPoint != existPeriods.get(i + 1).getStartTimestamp()) {
                Period period = new Period(existPeriods.get(i).getEndTimestamp() + secondPerPoint,
                        existPeriods.get(i + 1).getEndTimestamp() - secondPerPoint,
                        secondPerPoint);
                allPeriods.add(period);
            }
        }

        List<IntervalFactoryParam> fragParams = new ArrayList<>();
        for (Period period : allPeriods) {
            int startIndex = (period.getStartTimestamp() - param.getStartTimestamp()) / param.getSecondsPerPoint();
            int endIndex = (period.getEndTimestamp() - param.getStartTimestamp()) / param.getSecondsPerPoint();
            double[] newValues = Arrays.copyOfRange(values, startIndex, endIndex + 1);
            boolean isAllValuesAreTheSame = periodsWithTheSameValues.contains(period);
            IntervalFactoryParam fragParam = createNewParam(period, newValues, true);
            fragParams.add(fragParam);
        }


        return fragParams;
    }

    private IntervalFactoryParam createNewParam(Period period, double[] values, boolean isAllValuesAreTheSame) {
        return new IntervalFactoryParam() {
            @Override
            public void addValue(double value) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void reset(int startTimestamp) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int getStartTimestamp() {
                return period.getStartTimestamp();
            }

            @Override
            public int getEndTimestamp() {
                return period.getEndTimestamp();
            }

            @Override
            public double[] getValues() {
                return values;
            }

            @Override
            public int getSecondsPerPoint() {
                return period.getSecondsPerPoint();
            }

            @Override
            public boolean isAllValuesAreTheSame() {
                return isAllValuesAreTheSame;
            }

            @Override
            public Set<Period> getPeriodsWithTheSameValues() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
