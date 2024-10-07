package com.hey.givumethemoney.controller;

import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
public class DonationForm {

    private String title;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private int goal;
    private String enterName;
    private String descript;

    public DonationForm(String title, LocalDate startDate, LocalDate endDate, int goal, String enterName, String descript) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.goal = goal;
        this.enterName = enterName;
        this.descript = descript;
    }
}
