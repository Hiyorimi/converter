package com.converter.converter.repositories;

import com.converter.converter.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface CurrencyRepository extends JpaRepository<Currency, Integer> {

    List<Currency> findByDate(String date);

}
