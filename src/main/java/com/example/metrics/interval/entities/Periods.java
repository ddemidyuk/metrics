package com.example.metrics.interval.entities;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class Periods implements Iterable<Period> {
    private Deque<Period> periods = new LinkedList<>();

    private class Param {
        Deque<Period> periods = new LinkedList<>();
        Period period;
        boolean isNeedNext = true;
    }

    private enum IntersectionType {
        LESS(param -> {
            Period firstPeriod = param.periods.pollFirst();
            param.periods.addFirst(param.period);
            param.periods.addFirst(firstPeriod);
            param.period = null;
            param.isNeedNext = false;
            return param;
        }),
        MORE(param -> {
            param.isNeedNext = true;
            return param;
        }),
        RIGHT(param -> {
            Period firstPeriod = param.periods.pollFirst();
            Period period = new Period(param.period.getStartTimestamp(), firstPeriod.getEndTimestamp(), param.period.getSecondsPerPoint());
            param.periods.addFirst(period);
            param.period = null;
            param.isNeedNext = true;
            return param;
        }),
        LEFT(param -> {
            Period firstPeriod = param.periods.pollFirst();
            Period period = new Period(firstPeriod.getStartTimestamp(), param.period.getEndTimestamp(), param.period.getSecondsPerPoint());
            param.period = period;
            param.isNeedNext = true;
            return param;
        }),
        CONTAINS(param -> {
            param.period = null;
            param.isNeedNext = false;
            return param;
        }),
        WIDER(param -> {
            param.periods.removeFirst();
            param.isNeedNext = true;
            return param;
        });

        private Function<Param, Param> function;

        IntersectionType(Function<Param, Param> function) {
            this.function = function;
        }

    }

    public void unitePeriods(List<Period> newPeriods) {
        if(periods.size()== 0){
            periods.addAll(newPeriods);
            return;
        }
        Param param = new Param();
        for (Period newPeriod : newPeriods) {
            param.period = newPeriod;
            do {
                if(param.isNeedNext){
                    if(periods.peekLast() != null){
                        param.periods.addFirst(periods.pollLast());
                    }else {
                        param.periods.addFirst(param.period);
                        break;
                    }
                }
                param = ComparePeriods(param.periods.getFirst(), newPeriod).function.apply(param); //todo
            } while (param.period != null);
        }

        periods = param.periods;
    }


    private IntersectionType ComparePeriods(Period p1, Period p2) {
        //todo remove throw
        if (p1.getSecondsPerPoint() != p2.getSecondsPerPoint()) {
            throw new RuntimeException("SecondsPerPoint of compared periods must be equals");
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

    @Override
    public Iterator<Period> iterator() {
        return periods.iterator();
    }

}
