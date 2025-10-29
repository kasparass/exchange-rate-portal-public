package lt.baltic.exchangerates.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "currencies")
@Data
@NoArgsConstructor
public class Currency {
    @Id
    @Column(length = 3)
    private String code;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Column(name = "name_lt", nullable = false)
    private String nameLt;

    @Column(nullable = false)
    private String number;

    @Column(name = "decimal_places", nullable = false)
    private Integer decimalPlaces;
}