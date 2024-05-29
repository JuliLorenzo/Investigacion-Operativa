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
            String queryPrueba = "SELECT * FROM Articulos";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(queryPrueba);
            //mostrar resultados de la query
            while(resultSet.next()){
                String idArticulo = resultSet.getString("idArticulo");
                String nombreArticulo = resultSet.getString("nombreArticulo");
                System.out.println("id del articulo: " + idArticulo);
                System.out.println("nombre del articulo: " + nombreArticulo);
            }

        }catch(SQLException e){
            System.out.println("Error al conectar con la base de datos: " + e.getMessage());
        }
    }
}
