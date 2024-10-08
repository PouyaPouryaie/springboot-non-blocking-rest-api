package ir.bigz.concurrency.restapi.common;

public enum LogSection {

    ASYNC("Async"),
    REACTIVE("Reactive"),
    NORMAL("Normal"),
    OTHER("other");

    public final String logName;

    LogSection(String logName) {
        this.logName = logName;
    }

    public String getLogName() {
        return logName;
    }
}
