package com.mycompany.myapp.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.ReportCard} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReportCardDTO implements Serializable {

    private Long id;

    @NotNull
    private Integer semester;

    @NotNull
    private String classification;

    @Lob
    private byte[] pdfFile;

    private String pdfFileContentType;

    private String comments;

    private UserDTO student;

    private UserDTO teacher;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public byte[] getPdfFile() {
        return pdfFile;
    }

    public void setPdfFile(byte[] pdfFile) {
        this.pdfFile = pdfFile;
    }

    public String getPdfFileContentType() {
        return pdfFileContentType;
    }

    public void setPdfFileContentType(String pdfFileContentType) {
        this.pdfFileContentType = pdfFileContentType;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public UserDTO getStudent() {
        return student;
    }

    public void setStudent(UserDTO student) {
        this.student = student;
    }

    public UserDTO getTeacher() {
        return teacher;
    }

    public void setTeacher(UserDTO teacher) {
        this.teacher = teacher;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReportCardDTO)) {
            return false;
        }

        ReportCardDTO reportCardDTO = (ReportCardDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, reportCardDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReportCardDTO{" +
            "id=" + getId() +
            ", semester=" + getSemester() +
            ", classification='" + getClassification() + "'" +
            ", pdfFile='" + getPdfFile() + "'" +
            ", comments='" + getComments() + "'" +
            ", student=" + getStudent() +
            ", teacher=" + getTeacher() +
            "}";
    }
}
