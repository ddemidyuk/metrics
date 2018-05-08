package com.example.metrics.interval.entities;

public abstract class AbstractInterval implements Interval {

    Period period;

    public AbstractInterval(AbstractIntervalBuilder builder) {
        this.period = new Period(builder.startTimestamp, builder.endTimestamp, builder.secondsPerPoint);
    }

    protected int getCountOfTimestamps(){
        return (period.getStartTimestamp() - period.getEndTimestamp()) / period.getSecondsPerPoint() + 1;
    }

    protected boolean isContainsTimestamp(int timestamp) {
        if (timestamp < period.getStartTimestamp() || timestamp > period.getEndTimestamp()) {
            return false;
        }
        return true;
    }

    protected int getTimestampPositionInPeriod(int timestamp){
        return (timestamp - period.getStartTimestamp()) / period.getSecondsPerPoint();
    }

    public Period getPeriod(){
        return period;
    }

    public int getStartTimestamp() {
        return period.getStartTimestamp();
    }

    public int getSecondsPerPoint() {
        return period.getSecondsPerPoint();
    }

    public int getEndTimestamp() {
        return period.getEndTimestamp();
    }

    public static  abstract class  AbstractIntervalBuilder<T extends AbstractIntervalBuilder<T>> {
        private int startTimestamp;
        private int endTimestamp;
        private int secondsPerPoint;

        protected abstract T getThis();

        public T startTimestamp(int startTimestamp) {
            this.startTimestamp = startTimestamp;
            return getThis();
        }

        public T endTimestamp(int endTimestamp) {
            this.endTimestamp = endTimestamp;
            return getThis();
        }

        public T secondsPerPoint(int secondsPerPoint) {
            this.secondsPerPoint = secondsPerPoint;
            return getThis();
        }
    }
}
