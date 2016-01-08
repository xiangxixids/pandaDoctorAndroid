package com.chunyuAdvisory.bean;

import java.util.List;

/**
 * Created by Administrator on 15-6-23.
 */
public class Problem_List {
    private String result;
    private List<Problem_List_Item> problem_list;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<Problem_List_Item> getProblem_list() {
        return problem_list;
    }

    public void setProblem_list(List<Problem_List_Item> problem_list) {
        this.problem_list = problem_list;
    }
}
