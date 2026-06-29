package gestion_hotel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class Client {
    private String nom;
    private String prenom;
    private String telephone;
}
