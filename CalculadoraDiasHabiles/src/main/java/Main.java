import Excepciones.FechasErroneas;
import Excepciones.FechasFueraDeRango;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws ParseException {
        Scanner teclado = new Scanner(System.in);
        SimpleDateFormat FormatoFecha = new SimpleDateFormat("dd-MM-yy");
        CalculadoraDiasHabiles calculadora = new CalculadoraDiasHabiles();
        String fechaAString;
        String fechaBString;
        int TotalDiasHabiles = 0;
        //Definimos las dos fechas iniciales.
        Date fechaA = new Date();
        Date fechaB = new Date();
        //fechaA = FormatoFecha.parse("01-01-2020");
        //fechaB = FormatoFecha.parse("31-08-2022");
        //LLamamos la funcion
        try{
            System.out.println("Ingrese la fecha inicial en el siguiente formato: dd-MM-yyyy");
            System.out.println("\t Ejemplo: 01-08-2020");
            fechaAString = teclado.nextLine();
            System.out.println("Ingrese la fecha final en el siguiente formato: dd-MM-yyyy");
            System.out.println("\t Ejemplo: 01-08-2021");
            fechaBString = teclado.nextLine();
            fechaA = FormatoFecha.parse(fechaAString);
            fechaB = FormatoFecha.parse(fechaBString);
           TotalDiasHabiles = calculadora.CalcularDiasHabiles(fechaA, fechaB);
           System.out.println("Entre el dia "+FormatoFecha.format(fechaA)+" y el dia "+FormatoFecha.format(fechaB)+" hay en total "+TotalDiasHabiles+" dias habiles.");
        }catch(FechasErroneas e){
            System.out.println(e.getMessage());
        }
        catch (FechasFueraDeRango e){
            System.out.println(e.getMessage());
        }catch (Exception e){
            System.out.println("Exception: "+e.getMessage());
        }

    }
}