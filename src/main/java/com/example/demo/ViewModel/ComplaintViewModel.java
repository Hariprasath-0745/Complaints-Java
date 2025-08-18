package com.example.demo.ViewModel;
import java.time.OffsetDateTime;

public class ComplaintViewModel  {
    private ComplaintVisitPurpose complaintVisitPurpose;
    private Complaint complaint;
    private long authorId;
    private OffsetDateTime lastUpdatedDate;
    private List<Author> authors;
    private String authorDetails = "";
    private long id;
    private long peId;
}


