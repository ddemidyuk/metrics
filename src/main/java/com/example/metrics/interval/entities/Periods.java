package com.example.metrics.interval.entities;

import java.util.*;
import java.util.function.Function;

public class Periods implements Iterable<Integer> {
    private Deque<Period> periods = new LinkedList<>();

    private class Param {
        Deque<Period> resultPeriods = new LinkedList<>();
        Period newPeriod;
        boolean needNext = true;
    }

    private enum IntersectionType {
        LESS(param -> {
            Period lastPeriod = param.resultPeriods.pollLast();
            param.resultPeriods.addLast(param.newPeriod);
            param.resultPeriods.addLast(lastPeriod);
            param.newPeriod = null;
            param.needNext = false;
            return param;
        }),
        MORE(param -> {
            param.needNext = true;
            return param;
        }),
        RIGHT(param -> {
            Period lastPeriod = param.resultPeriods.pollLast();
            Period period = new Period(param.newPeriod.getStartTimestamp(), lastPeriod.getEndTimestamp(), param.newPeriod.getSecondsPerPoint());
            param.resultPeriods.addLast(period);
            param.newPeriod = null;
            param.needNext = true;
            return param;
        }),
        LEFT(param -> {
            Period lastPeriod = param.resultPeriods.pollLast();
            Period period = new Period(lastPeriod.getStartTimestamp(), param.newPeriod.getEndTimestamp(), param.newPeriod.getSecondsPerPoint());
            param.newPeriod = period;
            param.needNext = true;
            return param;
        }),
        CONTAINS(param -> {
            param.newPeriod = null;
            param.needNext = false;
            return param;
        }),
        WIDER(param -> {
            param.resultPeriods.removeLast();
            param.needNext = true;
            return param;
        });

        private Function<Param, Param> function;

        IntersectionType(Function<Param, Param> function) {
            this.function = function;
        }

    }

    public void unitePeriods(List<Period> newPeriods) {
        if (periods.size() == 0) {
            periods.addAll(newPeriods);
            return;
        }
        Param param = new Param();
        for (Period newPeriod : newPeriods) {
            param.newPeriod = newPeriod;
            do {
                if (param.needNext) {
                    if (periods.peekFirst() != null) {
                        param.resultPeriods.addLast(periods.pollFirst());
                    } else {
                        param.resultPeriods.addLast(param.newPeriod);
                        break;
                    }
                }
                param = ComparePeriods(param.resultPeriods.getLast(), newPeriod).function.apply(param); //todo
            } while (param.newPeriod != null);
        }

        periods = param.resultPeriods;
    }


    private IntersectionType ComparePeriods(Period p1, Period p2) {
        //todo remove throw
        if (p1.getSecondsPerPoint() != p2.getSecondsPerPoint()) {
            throw new RuntimeException("SecondsPerPoint of compared resultPeriods must be equals");
        }
        int start1 = p1.getStartTimestamp();
        int start2 = p2.getStartTimestamp();
        int end1 = p1.getEndTimestamp();
        int end2 = p2.getEndTimestamp();
        int secondsPerPoint = p1.getSecondsPerPoint();

        if (start2 > end1) {
            if (start2 - end1 == secondsPerPoint) return IntersectionType.LEFT;
            return IntersectionType.MORE;
        }
        if (start2 >= start1) {
            if (end2 > end1) return IntersectionType.LEFT;
            return IntersectionType.CONTAINS;
        }
        if (end2 > end1) return IntersectionType.WIDER;
        if (end2 >= start1) return IntersectionType.RIGHT;
        if (start1 - end2 == secondsPerPoint) return IntersectionType.RIGHT;

        return IntersectionType.LESS;
    }

    public int getCountOfTimestamp() {
        int countOfTimestamp = 0;
        for (Period period : periods) {
            countOfTimestamp += (period.getEndTimestamp() - period.getStartTimestamp()) / period.getSecondsPerPoint() + 1;
        }
        return countOfTimestamp;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new TimestampIterator();
    }

    final class TimestampIterator implements Iterator<Integer> {
        private int periodNumber;
        private int timestampNumber;
        private List<Period> arrPeriods = new ArrayList<>(periods);

        public boolean hasNext() {
            Period period = arrPeriods.get(periodNumber);
            if (periodNumber == arrPeriods.size() - 1 &&
                    period.getEndTimestamp() < period.getStartTimestamp() + timestampNumber * period.getSecondsPerPoint())
                return false;

            return true;
        }

        public final Integer next() {
            Period period = arrPeriods.get(periodNumber);
            if (period.getEndTimestamp() < period.getStartTimestamp() + timestampNumber * period.getSecondsPerPoint()) {
                periodNumber++;
                timestampNumber = 0;
            }
            period = arrPeriods.get(periodNumber);
            return period.getStartTimestamp() + timestampNumber++ * period.getSecondsPerPoint();
        }
    }

}
