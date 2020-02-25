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
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Controller
public class ConverterController {
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private ConvertationRepository convertationRepository;

    @GetMapping("/")
    public String getMainPage(Model model) {
        loadCurrencies();
        List<Currency> currencies1 = currencyRepository.findByDate(getActualDate());
        List<Currency> currencies2 = currencyRepository.findByDate(getActualDate());
        currencies1.remove(getCurrency("RUB"));
        currencies2.remove(getCurrency("USD"));
        model.addAttribute("currencies1", currencies1);
        model.addAttribute("currencies2", currencies2);
        model.addAttribute("currency1", getCurrency("RUB"));
        model.addAttribute("currency2", getCurrency("USD"));
        return "converter";
    }

    @GetMapping("/converter")
    public String getConverter(Model model) {
        loadCurrencies();
        List<Currency> currencies1 = currencyRepository.findByDate(getActualDate());
        List<Currency> currencies2 = currencyRepository.findByDate(getActualDate());
        currencies1.remove(getCurrency("RUB"));
        currencies2.remove(getCurrency("USD"));
        model.addAttribute("currencies1", currencies1);
        model.addAttribute("currencies2", currencies2);
        model.addAttribute("currency1", getCurrency("RUB"));
        model.addAttribute("currency2", getCurrency("USD"));
        return "converter";
    }

    @PostMapping("/converter")
    public String postConverter(Model model,
                                @RequestParam String charCode1,
                                @RequestParam String charCode2,
                                @RequestParam String quantity) {
        List<Currency> currencies1 = currencyRepository.findByDate(getActualDate());
        List<Currency> currencies2 = currencyRepository.findByDate(getActualDate());
        Currency currency1 = getCurrency(charCode1);
        Currency currency2 = getCurrency(charCode2);
        currencies1.remove(currency1);
        currencies2.remove(currency2);
        double result = Double.parseDouble(quantity) * (currency1.getValue() / currency2.getValue()) * (currency2.getNominal() / currency1.getNominal());
        result = new BigDecimal(result).setScale(4, RoundingMode.UP).doubleValue();
        model.addAttribute("currencies1", currencies1);
        model.addAttribute("currencies2", currencies2);
        model.addAttribute("result", result);
        model.addAttribute("quantity", quantity);
        model.addAttribute("currency1", currency1);
        model.addAttribute("currency2", currency2);
        model.addAttribute("currencyName", currency2.getName());
        Convertation convertation = new Convertation();
        convertation.setBefore(currency1.getCharCode());
        convertation.setAfter(currency2.getCharCode());
        convertation.setDate(getActualDate());
        convertation.setSummafter(result);
        convertation.setSummBefore(Double.parseDouble(quantity));
        convertation.setUser(ConverterApplication.getCurrentUsername());
        convertation.setCurrencyBefore(currency1);
        convertation.setCurrencyAfter(currency2);
        convertationRepository.save(convertation);
        return "converter";
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

    private void loadCurrencies() {
        ArrayList<Currency> currencies = null;
        Document document = null;
        String date = getActualDate();
        try {
            currencies = new ArrayList<>();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            StringBuilder urlSb = new StringBuilder("http://www.cbr.ru/scripts/XML_daily.asp?date_req=");
            urlSb.append(getActualDateForRequest());
            document = builder.parse(new URL(urlSb.toString()).openStream());

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (currencyRepository.findByDate(date).size() < 10) {

            NodeList valuteElements = document.getElementsByTagName("Valute");

            for (int i = 0; i < valuteElements.getLength(); i++) {
                Node valute = valuteElements.item(i);
                NamedNodeMap attributes = valute.getAttributes();
                Element element = (Element) valuteElements.item(i);
                Currency currency = new Currency();
                currency.setDate(date);
                currency.setCharCode(getTagValue("CharCode", element));
                currency.setName(getTagValue("Name", element));
                currency.setValue(Double.parseDouble(getTagValue("Value", element).replace(",", ".")));
                currency.setNominal(Double.parseDouble(getTagValue("Nominal", element)));
                currency.setNumCode(Integer.parseInt(getTagValue("NumCode", element)));
                currency.setValuteId(attributes.getNamedItem("ID").getNodeValue());
                currencies.add(currency);
            }
            currencies.add(new Currency("R000001", 1, "RUB", 1.0, "Российский рубль", 1.0, date));


            for (Currency currency : currencies) {
                if (!currencyRepository.findByDate(getActualDate()).contains(currency)) {
                    currencyRepository.save(currency);
                }
            }
        }
    }

    private String getTagValue(String tag, org.w3c.dom.Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }

    private String getActualDate() {
        Date actualDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormat.format(actualDate);
    }

    private String getActualDateForRequest() {
        Date actualDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(actualDate);
    }
}
