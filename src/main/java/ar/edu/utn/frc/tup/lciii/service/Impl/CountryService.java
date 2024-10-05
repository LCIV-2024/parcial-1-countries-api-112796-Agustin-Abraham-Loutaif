package ar.edu.utn.frc.tup.lciii.service.Impl;

import ar.edu.utn.frc.tup.lciii.entities.CountryDtoEntity;
import ar.edu.utn.frc.tup.lciii.model.Country;
import ar.edu.utn.frc.tup.lciii.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ar.edu.utn.frc.tup.lciii.dtos.common.Country.CountryDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountryService{

        @Autowired
        private final CountryRepository countryRepository;

        @Autowired
        private final RestTemplate restTemplate;

        public List<Country> getAllCountries() {
                String url = "https://restcountries.com/v3.1/all";
                List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);
                return response.stream().map(this::mapToCountry).collect(Collectors.toList());
        }

        public List<CountryDTO> getAllCountriesDto(){
                List<CountryDTO> dtoList = new ArrayList<>();
                List<Country> listCountries = getAllCountries();
                for (Country country: listCountries) {
                        dtoList.add(mapToDTO(country));
                }
                return dtoList;
        }

        public CountryDTO getCountryDto(String data){
                List<CountryDTO> listDto = getAllCountriesDto();
                for (CountryDTO dto : listDto){
                        if (dto.getCode().equals(data.toUpperCase()) || dto.getName().equals(data)) return dto;
                }
                return new CountryDTO();
        }

        public List<CountryDTO> getContinentCountries(String region){
                List<Country> listCountries = getAllCountries();
                List<CountryDTO> listContinent = new ArrayList<>();
                for (Country c : listCountries){
                        if (c.getRegion().equals(region)) {
                                listContinent.add(mapToDTO(c));
                        }
                }
                if (listContinent.isEmpty()) return new ArrayList<>();
                return listContinent;
        }



        public List<CountryDTO> getLanguageCountries(String language){
                List<Country> listCountries = getAllCountries();
                List<CountryDTO> listLanguage = new ArrayList<>();
                String keyValue = getLanguageKey(language);

                for (Country c : listCountries){
                        if(!keyValue.equals("null")){
                                if (!c.getLanguages().isEmpty()){
                                        if (!c.getLanguages().get(keyValue).isEmpty()) listLanguage.add(mapToDTO(c));
                                }
                        } else break;
                }
                return listLanguage;
        }

        public CountryDTO getMostBordersCountry(){
                List<Country> countryList = getAllCountries();
                CountryDTO dto = new CountryDTO();
                Integer borders = 0;

                for (Country c : countryList){
                        if (!c.getBorders().isEmpty()){
                                if (c.getBorders().size() > borders){
                                        borders = c.getBorders().size();
                                        dto = mapToDTO(c);
                                }
                        }
                }
                return dto;
        }

        public List<CountryDTO> postCountries(Integer amount){
                List<CountryDTO> countryDTOList = getAllCountriesDto();
                Integer totalNewCountries = 0;

                for (int i = 0; i < countryDTOList.size(); i++){
                        CountryDtoEntity entity = new CountryDtoEntity(countryDTOList.get(i).getName(),
                                countryDTOList.get(i).getCode());

                        CountryDtoEntity checkCountry = countryRepository.findById(entity.getName());
                        if (checkCountry != null){
                                countryRepository.save(entity);
                                totalNewCountries = totalNewCountries + 1;
                        }

                        if (totalNewCountries == amount) break;
                }
        }

        /**
         * Agregar mapeo de campo cca3 (String)
         * Agregar mapeo campos borders ((List<String>))
         */
        private Country mapToCountry(Map<String, Object> countryData) {
                Map<String, Object> nameData = (Map<String, Object>) countryData.get("name");
                return Country.builder()
                        .name((String) nameData.get("common"))
                        .population(((Number) countryData.get("population")).longValue())
                        .code((String) countryData.get("cca3"))
                        .area(((Number) countryData.get("area")).doubleValue())
                        .region((String) countryData.get("region"))
                        .languages((Map<String, String>) countryData.get("languages"))
                        .borders((List<String>) countryData.get("borders"))
                        .build();
        }

        private CountryDTO mapToDTO(Country country) {
                return new CountryDTO(country.getCode(), country.getName());
        }

        private String getLanguageKey(String language){
                switch (language){
                        case "English" : return "eng";
                        case "Spanish" : return "spa";
                        case "French" : return "fra";
                        case "German" : return "deu";
                        case "Portuguese" : return "por";
                        case "Chinese" : return "zho";
                        case "Arabic" : return "ara";
                        case "Russian" : return "rus";
                        case "Hindi" : return "hin";
                        case "Swahili" : return "swa";
                        default: return "null";
                }
        }
}