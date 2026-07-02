package gestion_hotel;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TypeChambre {
    COUPLE(70000f),
    FAMILIALE(120000f),
    SUITE(200000f);

    private final float PrixParNuit;
}
