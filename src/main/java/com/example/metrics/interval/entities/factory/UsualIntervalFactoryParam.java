package com.example.metrics.interval.entities.factory;

import com.example.metrics.interval.entities.Period;

import java.util.*;

public class UsualIntervalFactoryParam implements IntervalFactoryParam {
    private int startTimestamp;
    private double[] buffer;
    private int countValues;
    private int secondsPerPoint;
    private boolean allValuesAreTheSame;
    private PeriodsWithTheSameValuesCreator periodsWithTheSameValuesCreator;

    public UsualIntervalFactoryParam(Builder builder) {
        this.buffer = new double[builder.bufferSize];
        this.secondsPerPoint = builder.secondsPerPoint;
        reset(builder.startTimestamp);
    }

    public void addValue(double value) {

        boolean isValueNotEqualPriorValue =  countValues != 0 && buffer[countValues - 1] != value;

        if (allValuesAreTheSame && isValueNotEqualPriorValue) {
            allValuesAreTheSame = false;
        }
        buffer[countValues++] = value;
        periodsWithTheSameValuesCreator.createPeriodsWithTheSameValuesIfCan(isValueNotEqualPriorValue);
    }

    public int getStartTimestamp() {
        return startTimestamp;
    }

    public int getEndTimestamp() {
        return startTimestamp + secondsPerPoint * (countValues - 1);
    }

    public double[] getValues() {
        return Arrays.copyOf(buffer, countValues);
    }

    public void reset(int startTimestamp) {
        this.startTimestamp = startTimestamp;
        this.countValues = 0;
        this.allValuesAreTheSame = true;
        this.periodsWithTheSameValuesCreator = new PeriodsWithTheSameValuesCreator(startTimestamp);
    }

    public int getSecondsPerPoint() {
        return secondsPerPoint;
    }

    public boolean isAllValuesAreTheSame() {
        return allValuesAreTheSame;
    }

    public Set<Period> getPeriodsWithTheSameValues() {
        return periodsWithTheSameValuesCreator.periodsWithTheSameValues;
    }


    private class PeriodsWithTheSameValuesCreator {
        private static final int MIN_COUNT_OF_THE_SAME_VALUE_TO_COMBINE_IN_SUBINTERVAL = 10;
        private Set<Period> periodsWithTheSameValues = new LinkedHashSet<>();
        private int countTheSameValues = 0;
        private int nextStartTimestamp;

        public PeriodsWithTheSameValuesCreator(int nextStartTimestamp) {
            this.nextStartTimestamp = nextStartTimestamp;
        }

        public void createPeriodsWithTheSameValuesIfCan(boolean isValueNotEqualPriorValue) {
            if (isValueNotEqualPriorValue && countTheSameValues >= MIN_COUNT_OF_THE_SAME_VALUE_TO_COMBINE_IN_SUBINTERVAL) {
                periodsWithTheSameValues.add(new Period(nextStartTimestamp, getEndTimestamp() - secondsPerPoint, secondsPerPoint));
            }
            if (isValueNotEqualPriorValue) {
                countTheSameValues = 0;
                nextStartTimestamp = getEndTimestamp();
            } else {
                countTheSameValues++;
            }
        }

    }

    public static final class Builder {
        private int startTimestamp;
        private int secondsPerPoint;
        private int bufferSize;

        private Builder() {
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder secondsPerPoint(int secondsPerPoint) {
            this.secondsPerPoint = secondsPerPoint;
            return this;
        }

        public Builder startTimestamp(int startTimestamp) {
            this.startTimestamp = startTimestamp;
            return this;
        }

        public Builder bufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        public UsualIntervalFactoryParam build() {
            return new UsualIntervalFactoryParam(this);
        }
    }
}
