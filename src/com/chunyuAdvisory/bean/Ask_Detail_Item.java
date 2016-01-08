package com.chunyuAdvisory.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 15-6-23.
 */
public class Ask_Detail_Item implements Serializable {

    private String status;
    private String start_time;
    private String problem_id;
    private List<Ask_Contents> contents;
    private String end_time;
    private String doctor_clinic;
    private String doctor_name;
    private String doctor_title;
    private String doctor_hospital;

    public String getDoctor_clinic() {
        return doctor_clinic;
    }

    public void setDoctor_clinic(String doctor_clinic) {
        this.doctor_clinic = doctor_clinic;
    }

    public String getDoctor_name() {
        return doctor_name;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public String getDoctor_title() {
        return doctor_title;
    }

    public void setDoctor_title(String doctor_title) {
        this.doctor_title = doctor_title;
    }

    public String getDoctor_hospital() {
        return doctor_hospital;
    }

    public void setDoctor_hospital(String doctor_hospital) {
        this.doctor_hospital = doctor_hospital;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getProblem_id() {
        return problem_id;
    }

    public void setProblem_id(String problem_id) {
        this.problem_id = problem_id;
    }

    public List<Ask_Contents> getContents() {
        return contents;
    }

    public void setContents(List<Ask_Contents> contents) {
        this.contents = contents;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
}
