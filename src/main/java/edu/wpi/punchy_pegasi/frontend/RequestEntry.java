package edu.wpi.punchy_pegasi.frontend;

public class RequestEntry {
    protected final String patientName;
    protected final String roomNumber;
    protected final String staffAssignment;
    protected final String additionalNotes;
    protected enum Status {
        NONE,
        PROCESSING,
        DONE
    }
    protected final Status status;

    public RequestEntry(String patientName, String roomNumber, String staffAssignment, String additionalNotes) {
        this.patientName = patientName;
        this.roomNumber = roomNumber;
        this.staffAssignment = staffAssignment;
        this.additionalNotes = additionalNotes;
        this.status = Status.PROCESSING;
    }
}
