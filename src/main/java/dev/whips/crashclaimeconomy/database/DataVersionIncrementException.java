package dev.whips.crashclaimeconomy.database;

public class DataVersionIncrementException extends RuntimeException {
    public DataVersionIncrementException(int fromRevision, int toRevision) {
        super("Database schema conversion failed: [" + fromRevision + " -> " + toRevision + "]");
    }
}
