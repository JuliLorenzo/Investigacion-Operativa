package invop;
import java.sql.*;

public class TestConnection {
    public static void main(String[] args){
        realizarPruebaConexion();
    }

    public static void realizarPruebaConexion(){
        String url = "jdbc:mysql://localhost:3306/gestionInventario";
        String username = "root";
        String password = "Mochila12345";

        try (Connection connection = DriverManager.getConnection(url, username, password)){
            System.out.println("Conexion exitosa");
            /* String queryPruebaArticulos = "SELECT * FROM Articulos";
            Statement statement1 = connection.createStatement();
            ResultSet resultSet1 = statement1.executeQuery(queryPruebaArticulos);
            //mostrar resultados de la query
            while(resultSet1.next()){
                String idArticulo = resultSet1.getString("id");
                String nombreArticulo = resultSet1.getString("nombre_articulo");
                System.out.println("id del articulo: " + idArticulo);
                System.out.println("nombre del articulo: " + nombreArticulo);
            }
            String queryPruebaVentas = "SELECT * FROM Ventas";
            Statement statement2 = connection.createStatement();
            ResultSet resultSet2 = statement2.executeQuery(queryPruebaVentas);
            //mostrar resultados de la query
            while(resultSet2.next()){
                String idVenta = resultSet2.getString("id");
                String fechaVenta = resultSet2.getString("fecha_venta");
                System.out.println("id de venta: " + idVenta);
                System.out.println("fecha de venta: " + fechaVenta);
            }*/

        } catch(SQLException e) {
            System.out.println("Error al conectar con la base de datos: " + e.getMessage());
        }
    }
}
