package com.poortorich.income.service;

import com.poortorich.income.entity.Income;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class IncomeService {

    @Transient
    public void modifyIncomeDate(Income income, LocalDate incomeDate) {
        income.updateAccountBookDate(incomeDate);
    }
}
