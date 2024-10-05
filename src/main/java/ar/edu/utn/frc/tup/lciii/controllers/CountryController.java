package ar.edu.utn.frc.tup.lciii.controllers;
import ar.edu.utn.frc.tup.lciii.dtos.common.Country.CountryDTO;
import ar.edu.utn.frc.tup.lciii.model.Country;
import ar.edu.utn.frc.tup.lciii.service.Impl.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/api")
@RequiredArgsConstructor
public class CountryController {

    @Autowired
    private final CountryService countryService;

    @GetMapping("/countries")
    public List<CountryDTO> getAllCountries(){
        List<CountryDTO> countries = countryService.getAllCountriesDto();
        if (countries.isEmpty()) return null;
        else return countries;
    }

    @GetMapping("/countries/{data}")
    public CountryDTO getCountryDto(@PathVariable String data){
        return countryService.getCountryDto(data);
    }

    @GetMapping("/countries/{continent}/continent")
    public List<CountryDTO> getContinentCountries(@PathVariable String continent){
        return countryService.getContinentCountries(continent);
    }

    @GetMapping("/countries/{language}/language")
    public List<CountryDTO> getLanguageCountries(@PathVariable String language){
        return countryService.getLanguageCountries(language);
    }

    @GetMapping("/countries/most-borders")
    public CountryDTO getLanguageCountries(){
        return countryService.getMostBordersCountry();
    }
}