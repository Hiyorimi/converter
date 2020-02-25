package com.converter.converter.repositories;

import com.converter.converter.model.Convertation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConvertationRepository extends JpaRepository<Convertation, Integer> {

    List<Convertation> findByUsername(String username);

}
