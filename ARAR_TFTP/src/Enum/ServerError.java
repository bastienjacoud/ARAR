package Enum;

/**
 * Created by Brandon on 15/05/2017.
 */
public enum ServerError {

    ERR0(0, "Erreur inconnu"),
    ERR1(1, "Fichier non trouvé"),
    ERR2(2, "Violation de l'accès"),
    ERR3(3, "Disque plein"),
    ERR4(4, "Opération TFTP illégale"),
    ERR5(5, "Transfert ID inconnu"),
    ERR6(6, "Le fichier existe déjà "),
    ERR7(7, "Utilisateur inconnu");

    public final String libelle;
    public final int code;

    ServerError(int p_code, String p_libelle)
    {
        this.libelle = p_libelle;
        this.code = p_code;
    }

    public static ServerError getStringFromValue(int valeur) {
        switch (valeur) {
            case 0 :
                return ERR0;
            case 1 :
                return ERR1;
            case 2 :
                return ERR2;
            case 3 :
                return ERR3;
            case 4 :
                return ERR4;
            case 5 :
                return ERR5;
            case 6 :
                return ERR6;
            case 7 :
                return ERR7;
            default :
                return ERR0;
        }
    }
}