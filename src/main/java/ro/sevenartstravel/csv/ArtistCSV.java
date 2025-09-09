package ro.sevenartstravel.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class ArtistCSV {

    @CsvBindByName
    private String name;
    @CsvBindByName
    private String biography;
    @CsvBindByName
    private String imageUrl;
    @CsvBindByName
    private Integer birthYear;
    @CsvBindByName
    private Integer deathYear;
    @CsvBindByName
    private String nationality;

}