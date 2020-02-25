package com.converter.converter.controllers;

import com.converter.converter.ConverterApplication;
import com.converter.converter.model.Convertation;
import com.converter.converter.model.Currency;
import com.converter.converter.repositories.ConvertationRepository;
import com.converter.converter.repositories.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class HistoryController {
    @Autowired
    ConvertationRepository convertationRepository;
    @Autowired
    private CurrencyRepository currencyRepository;

    @GetMapping("/history")
    public String getHistory(Model model) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        List<Currency> currencies1 = currencyRepository.findByDate(getActualDate());
        List<Currency> currencies2 = currencyRepository.findByDate(getActualDate());
        List<Convertation> convertations = convertationRepository.findByUsername(ConverterApplication.getCurrentUsername());
        model.addAttribute("convertations", convertations);
        model.addAttribute("currencies1", currencies1);
        model.addAttribute("currencies2", currencies2);
        model.addAttribute("date", date);
        return "history";
    }

    @PostMapping("/history")
    public String postFilterHistory(Model model,
                                    @RequestParam String charCode1,
                                    @RequestParam String charCode2,
                                    @RequestParam String date) {
        List<Currency> currencies1 = currencyRepository.findByDate(getActualDate());
        List<Currency> currencies2 = currencyRepository.findByDate(getActualDate());
        List<Convertation> convertations = convertationRepository.findByUsername(ConverterApplication.getCurrentUsername());
        model.addAttribute("convertations", filterConvertations(convertations, charCode1, charCode2, date));
        Currency currency1;
        Currency currency2;
        currency1 = getCurrency(charCode1);
        currency2 = getCurrency(charCode2);
        if (currency1 != null) {
            currencies1.remove(currency1);
            model.addAttribute("currency1", currency1);
        }
        if (currency2 != null) {
            currencies2.remove(currency2);
            model.addAttribute("currency2", currency2);
        }
        model.addAttribute("currencies1", currencies1);
        model.addAttribute("currencies2", currencies1);
        model.addAttribute("date", date);
        return "history";
    }

    private String getActualDate() {
        Date actualDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormat.format(actualDate);
    }

    private List<Convertation> filterConvertations(List<Convertation> convertations, String filter1Value, String filter2Value, String dateValue) {
        List<Convertation> result = new ArrayList<>();
        if (filter1Value.contains("empty") && (filter2Value.contains("empty"))) {
            result = convertations;
        } else if (filter1Value.contains("empty")) {
            for (Convertation convertation :
                    convertations) {
                if (convertation.getAfter().equals(filter2Value)) {
                    result.add(convertation);
                }
            }
        } else if (filter2Value.contains("empty")) {
            for (Convertation convertation :
                    convertations) {
                if (convertation.getBefore().equals(filter1Value)) {
                    result.add(convertation);
                }
            }
        } else {
            for (Convertation convertation :
                    convertations) {
                if (convertation.getAfter().equals(filter2Value) && convertation.getBefore().equals(filter1Value)) {
                    result.add(convertation);
                }
            }
        }
        if (!dateValue.equals("")) {
            String date = null;
            try {
                Date filterDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                date = format.format(filterDate);
            } catch (ParseException e) {
            }
            List<Convertation> tempList = new ArrayList<>(result);
            for (Convertation convertation :
                    tempList) {
                if (!date.equals(convertation.getDate())) {
                    result.remove(convertation);
                }
            }
        }
        return result;
    }

    public Currency getCurrency(String charCode) {
        List<Currency> currencies = currencyRepository.findByDate(getActualDate());
        Currency currency = null;
        for (Currency element : currencies) {
            if (element.getCharCode().equals(charCode)) {
                currency = element;
            }
        }
        return currency;
    }
}
