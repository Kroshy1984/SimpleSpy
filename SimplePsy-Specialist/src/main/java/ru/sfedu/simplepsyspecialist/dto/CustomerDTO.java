package ru.sfedu.simplepsyspecialist.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerDTO {

    private String id;
    private String name;
    private String surname;
    private LocalDate dateOfBirth;
    private List<String> problemsId;
    private String gender;
    private Contact contact;

    public CustomerDTO() {}

    public CustomerDTO(String name, String surname, LocalDate dateOfBirth, String gender, Contact contact) {
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.contact = contact;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public List<String> getProblemsId() {
        return problemsId;
    }
    public void addProblem(String problemId) {
        if (problemsId == null)
        {
            problemsId = new ArrayList<>();
        }
        problemsId.add(problemId);
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
