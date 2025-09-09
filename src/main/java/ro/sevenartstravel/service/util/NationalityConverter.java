package ro.sevenartstravel.service.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ro.sevenartstravel.enums.Nationality;

@Component
public class NationalityConverter implements Converter<String, Nationality> {

    @Override
    public Nationality convert(String value) {

        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();

        if (trimmedValue.isEmpty()) {
            return null;
        }

        for (Nationality nationality : Nationality.values()) {
            if (nationality.name().equalsIgnoreCase(trimmedValue)) {
                return nationality;
            }
        }

        return null;
    }

}
