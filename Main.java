import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class Main {

    private static final String API_KEY = "0ff7143fb02e97ac0b5265a4";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        List<String> monedasSoportadas = Arrays.asList("USD", "EUR", "CLP", "MXN", "BRL", "JPY", "ARS");

        while (true) {
            System.out.println("\n=== Conversor de Monedas ===");
            System.out.println("Monedas disponibles: " + monedasSoportadas);
            System.out.print("Ingrese moneda de ORIGEN (por ejemplo: USD): ");
            String origen = scanner.nextLine().trim().toUpperCase();

            if (!monedasSoportadas.contains(origen)) {
                System.out.println("Moneda no válida.");
                continue;
            }

            System.out.print("Ingrese moneda de DESTINO (por ejemplo: CLP): ");
            String destino = scanner.nextLine().trim().toUpperCase();

            if (!monedasSoportadas.contains(destino)) {
                System.out.println("Moneda no válida.");
                continue;
            }

            System.out.print("Ingrese cantidad a convertir: ");
            double cantidad = scanner.nextDouble();
            scanner.nextLine(); // limpiar el buffer

            double tasa = obtenerTasaDeCambio(origen, destino);
            if (tasa == -1) {
                System.out.println("No se pudo obtener la tasa de cambio.");
            } else {
                double resultado = cantidad * tasa;
                System.out.printf("%.2f %s equivalen a %.2f %s\n", cantidad, origen, resultado, destino);
            }

            System.out.print("\n¿Desea realizar otra conversión? (s/n): ");
            String continuar = scanner.nextLine().trim().toLowerCase();
            if (!continuar.equals("s")) {
                System.out.println("¡Hasta luego!");
                break;
            }
        }
    }

    private static double obtenerTasaDeCambio(String origen, String destino) {
        try {
            String apiUrl = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/" + origen;
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
            con.disconnect();

            Gson gson = new Gson();

            System.out.println("Respuesta cruda desde la API:");
            // System.out.println(response.toString());

            ExchangeResponse data = gson.fromJson(response.toString(), ExchangeResponse.class);

            return data.conversion_rates.get(destino);

        } catch (Exception e) {
            System.out.println("Error al obtener la tasa: " + e.getMessage());
            return -1;
        }
    }
}
