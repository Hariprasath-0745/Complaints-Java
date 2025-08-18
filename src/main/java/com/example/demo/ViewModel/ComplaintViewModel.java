package com.example.demo.ViewModel;
import java.time.OffsetDateTime;
import java.util.List;

public class ComplaintViewModel  {
    private ComplaintVisitPurpose complaintVisitPurpose;
    private Complaint complaint;
    private long authorId;
    private OffsetDateTime lastUpdatedDate;
    private List<Author> authors;
    private String authorDetails = "";
    private long id;
    private long peId;

    public ComplaintVisitPurpose getComplaintVisitPurpose() {
        return complaintVisitPurpose;
    }

    public void setComplaintVisitPurpose(ComplaintVisitPurpose complaintVisitPurpose) {
        this.complaintVisitPurpose = complaintVisitPurpose;
    }

    public Complaint getComplaint() {
        return complaint;
    }

    public void setComplaint(Complaint complaint) {
        this.complaint = complaint;
    }

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public OffsetDateTime getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(OffsetDateTime lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public String getAuthorDetails() {
        return authorDetails;
    }

    public void setAuthorDetails(String authorDetails) {
        this.authorDetails = authorDetails;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPeId() {
        return peId;
    }

    public void setPeId(long peId) {
        this.peId = peId;
    }
}


