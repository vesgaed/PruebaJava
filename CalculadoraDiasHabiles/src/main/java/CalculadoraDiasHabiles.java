//Edwin Fabian Vesga Escobar.
import Excepciones.FechasErroneas;
import Excepciones.FechasFueraDeRango;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CalculadoraDiasHabiles {
    private SimpleDateFormat Formato = new SimpleDateFormat("yyyy-MM-dd");
    private ArrayList<String> festivosDeEseAño;
    public int CalcularDiasHabiles(Date fechaInicial, Date fechaFinal) throws IOException, InterruptedException, JSONException, ParseException, FechasErroneas, FechasFueraDeRango {
        Date fechaContador = new Date();
        fechaContador = Formato.parse(Formato.format(fechaInicial));
        int contadorDias = 0;
        int añoActualDeConteo = fechaContador.getYear();
        ArrayList<String> limitesFechas = new ArrayList<>();
        limitesFechas = limitesFechas();
        if(limitesFechas!=null){
            Date limiteInferior = Formato.parse(limitesFechas.get(0));
            Date limiteSuperior = Formato.parse(limitesFechas.get(1));
            if(fechaInicial.getTime()<limiteInferior.getTime()
                    || fechaFinal.getTime()>limiteSuperior.getTime()) throw new FechasFueraDeRango("Las fechas estan fuera de los limites.\nVerifique que estas esten dentro o configure los limites en el archivo.\n La primera linea es el limite inferior y la segunda, el superior.\nEn caso de no necesitar limite, elimine el archivo.");
        }
        festivosDeEseAño = Festivos(añoActualDeConteo+1900);
        if(fechaFinal.getTime()<fechaInicial.getTime()) throw new FechasErroneas("La fecha final debe ser posterior a la fecha inicial.");
        else if(fechaContador.equals(fechaFinal)){
            if(diaHabil(fechaContador)) contadorDias+=1;
        }
        else{
            do{
                if(fechaContador.getYear()!=añoActualDeConteo) {
                    añoActualDeConteo+=1;
                    festivosDeEseAño = Festivos(añoActualDeConteo+1900);
                }
                if(diaHabil(fechaContador)) contadorDias+=1;
                fechaContador.setTime(fechaContador.getTime()+ (1000 * 60 * 60 * 24));
            }while(fechaContador.getTime()<(fechaFinal.getTime()+(1000 * 60 * 60 * 24)));
        }
        return contadorDias;
    }
    private ArrayList<String> limitesFechas() throws IOException {
        ArrayList<String> limites = new ArrayList<>();
        Path path = Paths.get("");
        String directoryName = path.toAbsolutePath().toString();
        File archivoLimites = new File(directoryName+ "/configuracion/limites");
        if(!archivoLimites.exists()) return null;
        else{
            BufferedReader obj = new BufferedReader(new FileReader(archivoLimites));
            String auxiliar;
            while ((auxiliar = obj.readLine())!=null){
                limites.add(auxiliar);
            }
        }
        return limites;
    }
    private boolean diaHabil(Date fecha) throws JSONException, IOException, ParseException, InterruptedException {
        Calendar fechaCalendario = new GregorianCalendar();
        fechaCalendario.setTime(fecha);
        String fechaActualFormateada = Formato.format(fecha);
        //Verificar si es sabado o domingo
        if(fechaCalendario.get(Calendar.DAY_OF_WEEK)==1  || fechaCalendario.get(Calendar.DAY_OF_WEEK)==7) return false;
        //Recorrer lista de festivos de ese año
        else{
            for(String fechaFestivo: festivosDeEseAño){
                if(fechaActualFormateada.equals(fechaFestivo)) return false;
            }
        }
        return true;
    }
    private ArrayList<String> Festivos(int año) throws IOException, InterruptedException, JSONException, ParseException {
        //Definimos los parametros de la API
        String hosting = "https://calendarific.com/api/v2/holidays?";
        String APIKey = "70accceb3d04c32bf63415e8b0b1daf56bd51bc6";
        String pais = "CO";
        String year = String.valueOf(año);
        String URL_API = hosting+"&api_key="+APIKey+"&country="+pais+"&year="+year;
        ArrayList<String> festivos = new ArrayList<>();
        //Cconfiguramos cliente HTTP
        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest solicitud = HttpRequest.newBuilder()
                .GET()
                .header("accept",
                "application/json")
                .uri(URI.create(URL_API))
                .build();
        HttpResponse<String> respuesta = cliente.send(solicitud,HttpResponse.BodyHandlers.ofString());
        //Empezamos a trabajar con la respuesta JSON
        JSONObject archivoJson = new JSONObject(respuesta.body());
        JSONArray listaHolidays = archivoJson.getJSONObject("response").getJSONArray("holidays");
        for(int i=0;i<listaHolidays.length();i++){
            JSONObject jsonAuxiliar = listaHolidays.getJSONObject(i);
            JSONArray listaTipoFecha = jsonAuxiliar.getJSONArray("type");
            for(int j=0;j<listaTipoFecha.length();j++){
                if("National holiday".equals(listaTipoFecha.get(j).toString())){
                    JSONObject fechaJSON = jsonAuxiliar.getJSONObject("date");
                    festivos.add(fechaJSON.get("iso").toString());
                }
            }
        }
        return festivos;
    }
}
