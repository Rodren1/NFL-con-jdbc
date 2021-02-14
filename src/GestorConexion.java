
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Javi
 */
public class GestorConexion {

    Connection conn1 = null;
    String urlBBDD = "jdbc:mysql://localhost:3306/equipos_nfl?serverTimexone=UTC"; //creo la variable url con la dirección del servidor para poder crear la base de datos desde la interfaz
    String user = "root";
    String password = "";

    public int GestorConexion() { //cambio el constructor por un método que retorna un int para poder conectar con la base de datos mediante un boton
       
        int aux = 1; //esta variable auxiliar recibe 0 en caso de conectar a la bbdd, 1 en el caso de no poder conectar y -1 si ha habido algún error  
        try {

            conn1 = DriverManager.getConnection(urlBBDD, user, password);

            if (conn1 != null) {
                aux = 0;
            } else {
                aux = 1;
            }
        } catch (SQLException ex) {
            aux = -1;
        }
        return aux;
    }

    public void cerrar_conexion() {
        try {
            conn1.close();
            if (conn1.isClosed()) {
                System.out.println("Desconectado de la bbdd");
            }

        } catch (SQLException ex) {
            System.out.println("Error al cerrar la conexion a la bbdd");
        }
    }


    //este método inserta un campeonato dado por el usuario en su respectiva tabla
    public void insertarCampeonato(String tipo_campeonato, String numero_victorias, String fechas_victorias, int equipo_campeon) {

        try {
            Statement sta = conn1.createStatement();
            //el valor de la columna es introducido por el usuario, +variable+ es una forma alternativa a la de la ?
            sta.executeUpdate("INSERT INTO `campeonatos` (`tipo_campeonato`, `numero_victorias`, `fechas_victorias`, `equipo_campeon`) "
                    + "VALUES('" + tipo_campeonato + "', '" + numero_victorias + "', '" + fechas_victorias + "', '" + equipo_campeon + "');"); 
            sta.close();
            System.out.println("Datos añadidos correctamente");
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }

    //este método hace lo mismo que el anterior pero en la tabla equipos con sus datos pertinentes, todos ellos introduccidos por el usuario, excepto el id que al igual 
    //que en el resto de tablas es auto incrementativo
    public void insertarEquipo(String nombre_equipo, String ciudad, String estadio) {

        try {
            Statement sta = conn1.createStatement();
            sta.executeUpdate("INSERT INTO `equipos` (`nombre_equipo`, `ciudad_equipo`, `estadio_equipo`) "
                    + "VALUES('" + nombre_equipo + "', '" + estadio + "', '" + estadio + "');");
            sta.close();
            System.out.println("Datos añadidos correctamente");
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }

    //este método hace lo mismo que los dos anteriores pero en la tabla datos
    public void insertarDatos(String conferencia, String division, String fundacion, String entrada_nfl, int dato_equipo) {

        try {
            Statement sta = conn1.createStatement();
            sta.executeUpdate("INSERT INTO datos (`conferencia`, `division`, `fundacion`, `entrada_nfl`, `dato_equipo`) "
                    + "VALUES('" + conferencia + "', '" + division + "', '" + fundacion + "', '" + entrada_nfl + "', '" + dato_equipo + "');");
            sta.close();
            System.out.println("Datos añadidos correctamente");
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }

    
    public void borrarEquipo(String dato) {
        try {
            Statement sta = conn1.createStatement();
                sta.executeUpdate("DELETE FROM equipos WHERE nombre_equipo = '" + dato + "';");
                sta.close();
            System.out.println("Datos borrados correctamente");
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }
    
    public void borrarDato(int dato) {
        try {
            Statement sta = conn1.createStatement();
                sta.executeUpdate("DELETE FROM datos WHERE dato_equipo = '" + dato + "';");
                sta.close();
            System.out.println("Datos borrados correctamente");
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }
    
    public void borrarCampeonato(String campeonato, int equipo) {
        try {
            Statement sta = conn1.createStatement();
                sta.executeUpdate("DELETE FROM campeonatos WHERE equipo_campeon = '" + equipo + "' AND tipo_campeonato = '" + campeonato + "';");
                sta.close();
            System.out.println("Datos borrados correctamente");
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }

    //este método modifica una cancion, la seleccion de la canción es elegida por el usuario mediante un combobox que le da valor al parámetro de entrada datoMod y
    //los datos nuevos son, también, introduccidos por el usuario
    public void modificar(String nombre_equipo, String ciudad, String estadio, String division, int equipoModificado) {
        try {
            conn1.setAutoCommit(false);
            
            Statement sta = conn1.createStatement();
            sta.executeUpdate("UPDATE equipos "
                    + "SET nombre_equipo = '" + nombre_equipo + "', ciudad_equipo = '" + ciudad + "', estadio_equipo = '" + estadio + "'"
                    + "WHERE id_equipo = '" + equipoModificado + "';");
            sta.executeUpdate("UPDATE datos SET division = '" + division + "' WHERE dato_equipo = '" + equipoModificado + "'");
            sta.close();
            conn1.commit();
            System.out.println("Datos modificados correctamente");
        } catch (SQLException ex) {
            System.out.println(ex.toString());
            if(conn1 != null){
                try {
                    conn1.rollback();
                } catch (SQLException ex1) {
                    System.out.println(ex1.toString());
                }
            }
        }
    }

    //este método busca una canción por su nombre, introducido por el usuario y devuelve todos los datos de dicha canción
    public String buscarEquipo(String nombre) {
        String query = "SELECT * FROM `equipos`, `datos`, `campeonatos` WHERE `nombre_equipo` = '" + nombre + "' AND id_equipo = dato_equipo AND id_equipo = equipo_campeon";
        String salida = "";
        try {
            PreparedStatement pst = conn1.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                //mientras haya datos en el rs de la query los v sumando a la salida con un salto de linea
                salida = ("\n ID: " + rs.getInt("id_equipo")
                        + "\n Nombre del equipo: " + rs.getString("nombre_equipo")
                        + "\n Ciudad de residencia: " + rs.getString("ciudad_equipo")
                        + "\n Estadio del equipo: " + rs.getString("estadio_equipo")
                        + "\n Conferencia: " + rs.getString("conferencia")
                        + "\n División: " + rs.getString("division")
                        + "\n Fundación: " + rs.getString("fundacion")
                        + "\n Entrada en la NFL: " + rs.getString("entrada_nfl")
                        + "\n Campeonato: " + rs.getString("tipo_campeonato")
                        + "\n Número de victorias: " + rs.getString("numero_victorias")
                        + "\n Año de las victorias: " + rs.getString("fechas_victorias"));
            }
            rs.close();
            pst.close();
            return salida;
        } catch (SQLException ex) {
            return ex.toString();
        }
    }


    //este método consulta los datos de la tabla canciones y los muestra todos por pantalla
    public String selectCampeonatos() {
        String query = "SELECT * FROM `campeonatos`, `equipos` WHERE equipo_campeon = id_equipo ORDER BY `id_campeonato`";
        String salida = "";
        String aux = "";
        try {
            Statement sta = conn1.createStatement();
            ResultSet rs = sta.executeQuery(query);
            while (rs.next()) {

                aux = ("\n-------------------------------------------------"
                        + "\n Campeonato: " + rs.getString("tipo_campeonato")
                        + "\n Número de victorias: " + rs.getString("numero_victorias")
                        + "\n Año de las victorias: " + rs.getString("fechas_victorias")
                        + "\n Nombre del equipo: " + rs.getString("nombre_equipo")
                        + "\n-------------------------------------------------");
                salida = salida + aux;
            }
            rs.close();
            sta.close();
            return salida;
        } catch (SQLException ex) {
            return ex.toString();
        }
    }

    //este método hace lo mismo que el anterior pero sobre la tabla albumes
    public String selectDatos() {
        String query = "SELECT * FROM `datos`, `equipos` WHERE dato_equipo = id_equipo ORDER BY `id_dato`";
        String salida = "";
        String aux = "";
        try {
            Statement sta = conn1.createStatement();
            ResultSet rs = sta.executeQuery(query);
            while (rs.next()) {

                aux = ("\n-------------------------------------------------"
                        + "\n Conferencia: " + rs.getString("conferencia")
                        + "\n División: " + rs.getString("division")
                        + "\n Fundación: " + rs.getString("fundacion")
                        + "\n Entrada en la NFL: " + rs.getString("entrada_nfl")
                        + "\n Nombre del equipo: " + rs.getString("nombre_equipo")
                        + "\n-------------------------------------------------");
                salida = salida + aux;
            }
            rs.close();
            sta.close();
            return salida;
        } catch (SQLException ex) {
            return ex.toString();
        }
    }

    //este método hace lo mismo que los dos anteriores pero muestra los datos de todos los artistas
    public String selectEquipos() {
        String query = "SELECT * FROM `equipos` ORDER BY `nombre_equipo`";
        String salida = "";
        String aux = "";
        try {
            Statement sta = conn1.createStatement();
            ResultSet rs = sta.executeQuery(query);
            while (rs.next()) {

                aux = ("\n-------------------------------------------------"
                        + "\n Nombre del equipo: " + rs.getString("nombre_equipo")
                        + "\n Ciudad de residencia: " + rs.getString("ciudad_equipo")
                        + "\n Estadio del equipo: " + rs.getString("estadio_equipo")
                        + "\n ID equipo: " + rs.getInt("id_equipo")
                        + "\n-------------------------------------------------");
                salida = salida + aux;
            }
            rs.close();
            sta.close();
            return salida;
        } catch (SQLException ex) {
            return ex.toString();
        }
    }

    //este metodo consulta todos los datos de la bbdd con dos joins para evitar datos duplicados
    public String selectAll() {
        String query = "SELECT * FROM `equipos`, `datos`, `campeonatos` WHERE dato_equipo = id_equipo AND equipo_campeon = id_equipo ORDER BY id_equipo";
        String salida = "";
        String aux = "";
        try {
            Statement sta = conn1.createStatement();
            ResultSet rs = sta.executeQuery(query);
            while (rs.next()) {

                aux = ("\n-------------------------------------------------"
                        + "\n ID equipo: " + rs.getInt("id_equipo")
                        + "\n Nombre del equipo: " + rs.getString("nombre_equipo")
                        + "\n Ciudad de residencia: " + rs.getString("ciudad_equipo")
                        + "\n Estadio del equipo: " + rs.getString("estadio_equipo")
                        + "\n Conferencia: " + rs.getString("conferencia")
                        + "\n División: " + rs.getString("division")
                        + "\n Fundación: " + rs.getString("fundacion")
                        + "\n Entrada en la NFL: " + rs.getString("entrada_nfl")
                        + "\n Campeonato: " + rs.getString("tipo_campeonato")
                        + "\n Número de victorias: " + rs.getString("numero_victorias")
                        + "\n Año de las victorias: " + rs.getString("fechas_victorias")
                        + "\n-------------------------------------------------");
                salida = salida + aux;
            }
            rs.close();
            sta.close();
            return salida;
        } catch (SQLException ex) {
            return ex.toString();
        }
    }

    //este método devuelve un array list con el id y el nombre de todos los artistas para mas adelante llenar el combobox
    public ArrayList<String> comboBoxEquipos() {
        ArrayList<String> list = new ArrayList<String>();
        //con la cláusula WHERE EXISTS evito errores de darse el caso de que no existiesen el nombre o el id  
        String query = "SELECT * FROM `equipos` ORDER BY `id_equipo`";
        try {
            Statement sta = conn1.createStatement();
            ResultSet rs = sta.executeQuery(query);
            while (rs.next()) {
                //se van añadiendo a la lista el id, un guion con espacios y el nombre para usarlo mas adelante en la selección de uno de los datos del combobox
                list.add(rs.getInt("id_equipo") + " - " + rs.getString("nombre_equipo"));
            }
            rs.close();
            sta.close();
            return list;
        } catch (SQLException ex) {
            System.out.println(ex.toString());
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    //este método crea la base de datos y cambia la url para que se conecte con la base de datos creada
    public void crearBBDD() {
        urlBBDD = "jdbc:mysql://localhost:3306/equipos_nfl?serverTimexone=UTC";
        try {
            //se van ejecutando los updates de forma individual para evitar posibles errores y/o localizarlos facilmente
            Statement sta = conn1.createStatement();
            sta.executeUpdate("CREATE DATABASE IF NOT EXISTS `equipos_nfl`;");
            sta.executeUpdate("USE equipos_nfl;");
            sta.executeUpdate("CREATE TABLE IF NOT EXISTS `equipos`( "
                    + "`id_equipo` int(11) NOT NULL AUTO_INCREMENT, "
                    + "`nombre_equipo` varchar(40) NOT NULL,"
                    + "`ciudad_equipo` varchar(40) NOT NULL,"
                    + "`estadio_equipo` varchar(40) NOT NULL,"
                    + "PRIMARY KEY (`id_equipo`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;");
            sta.executeUpdate("CREATE TABLE IF NOT EXISTS `datos` ( "
                    + "`id_dato` int(11) NOT NULL AUTO_INCREMENT, "
                    + "`conferencia` varchar(40) DEFAULT NULL, "
                    + "`division` varchar(40) DEFAULT NULL, "
                    + "`fundacion` varchar(10) NOT NULL,"
                    + "`entrada_nfl` varchar(10) NOT NULL,"
                    + "`dato_equipo` int(11) NOT NULL, "
                    + "PRIMARY KEY (`id_dato`), "
                    + "UNIQUE KEY (`dato_equipo`), "
                    + "FOREIGN KEY (`dato_equipo`) REFERENCES `equipos` (`id_equipo`) ON DELETE CASCADE ON UPDATE CASCADE) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;");
            sta.executeUpdate("CREATE TABLE IF NOT EXISTS `campeonatos` ( "
                    + "`id_campeonato` int(11) NOT NULL AUTO_INCREMENT, "
                    + "`tipo_campeonato` varchar(20) NOT NULL, "
                    + "`numero_victorias` varchar(5), "
                    + "`fechas_victorias` varchar(80) DEFAULT NULL, "
                    + "`equipo_campeon` int(11) NOT NULL, "
                    + "PRIMARY KEY (`id_campeonato`), "
                    + "FOREIGN KEY (`equipo_campeon`) REFERENCES `equipos` (`id_equipo`) ON DELETE CASCADE ON UPDATE CASCADE) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;");
            sta.executeUpdate("INSERT INTO `equipos` (`nombre_equipo`, `ciudad_equipo`, `estadio_equipo`) VALUES"
                    + "('Buffalo Bills', 'Orchard Park, NY', 'New Era Field'),"
                    + "('Miami Dolphins', 'Miami Gardens, FL', 'Hard Rock Stadium'),"
                    + "('New England Patriots', 'Foxborough, MA', 'Gillette Stadium'),"
                    + "('New York Jets', 'East Rutherford, NJ', 'MetLife Stadium'),"
                    + "('Baltimore Ravens', 'Baltimore, MD', 'M&T Bank Stadium'),"
                    + "('Cincinnati Bengals', 'Cincinnati, OH', 'Paul Brown Stadium'),"
                    + "('Cleveland Browns', 'Cleveland, OH', 'FirstEnergy Stadium'),"
                    + "('Pittsburgh Steelers', 'Pittsburgh, PA', 'Heinz Field'),"
                    + "('Houston Texans', 'Houston, TX', 'NRG Stadium'),"
                    + "('Indianapolis Colts', 'Indianápolis, IN', 'Lucas Oil Stadium'),"
                    + "('Jacksonville Jaguars', 'Jacksonville, FL', 'TIAA Bank Field'),"
                    + "('Tennessee Titans', 'Nashville, TN', 'Nissan Stadium'),"
                    + "('Denver Broncos', 'Denver, CO', 'Empower Field at Mile High'),"
                    + "('Kansas City Chiefs', 'Kansas City, MO', 'Arrowhead Stadium'),"
                    + "('Las Vegas Raiders', 'Paradise, NV', 'Allegiant Stadium'),"
                    + "('Los Angeles Chargers', 'Inglewood, CA', 'SoFi Stadium');");
            sta.executeUpdate("INSERT INTO `equipos` (`nombre_equipo`, `ciudad_equipo`, `estadio_equipo`) VALUES"
                    + "('Dallas Cowboys', 'Arlington, TX', 'AT&T Stadium'),"
                    + "('New York Giants', 'East Rutherford, NJ', 'MetLife Stadium'),"
                    + "('Philadelphia Eagles', 'Filadelfia, PA', 'Lincoln Financial Field'),"
                    + "('Washington Redskins', 'Landover, MD', 'FedExField'),"
                    + "('Chicago Bears', 'Chicago, IL', 'Soldier Field'),"
                    + "('Detroit Lions', 'Detroit, MI', 'Ford Field'),"
                    + "('Green Bay Packers', 'Green Bay, WI', 'Lambeau Field'),"
                    + "('Minnesota Vikings', 'Minneapolis, MN', 'U.S. Bank Stadium'),"
                    + "('Atlanta Falcons', 'Atlanta, GA', 'Mercedes-Benz Stadium'),"
                    + "('Carolina Panthers', 'Charlotte, NC', 'Bank of America Stadium'),"
                    + "('New Orleans Saints', 'Nueva Orleans, LA', 'Mercedes-Benz Superdome'),"
                    + "('Tampa Bay Buccaneers', 'Tampa, FL', 'Raymond James Stadium'),"
                    + "('Arizona Cardinals', 'Glendale, AZ', 'State Farm Stadium'),"
                    + "('Los Angeles Rams', 'Inglewood, CA', 'SoFi Stadium'),"
                    + "('San Francisco 49ers', 'Santa Clara, CA', 'Levi`s Stadium'),"
                    + "('Seattle Seahawks', 'Seattle, WA', 'CenturyLink Field');");
            sta.executeUpdate("INSERT INTO `datos` (`conferencia`, `division`, `fundacion`, `entrada_nfl`, `dato_equipo`) VALUES"
                    + "('American Football Conference', 'AFC EAST', '1959', '1970', 1),"
                    + "('American Football Conference', 'AFC EAST', '1966', '1970', 2),"
                    + "('American Football Conference', 'AFC EAST', '1959', '1970', 3),"
                    + "('American Football Conference', 'AFC EAST', '1960', '1970', 4),"
                    + "('American Football Conference', 'AFC NORTH', '1996', '1996', 5),"
                    + "('American Football Conference', 'AFC NORTH', '1968', '1970', 6),"
                    + "('American Football Conference', 'AFC NORTH', '1946', '1950', 7),"
                    + "('American Football Conference', 'AFC NORTH', '1933', '1933', 8),"
                    + "('American Football Conference', 'AFC SOUTH', '2002', '2002', 9),"
                    + "('American Football Conference', 'AFC SOUTH', '1953', '1953', 10),"
                    + "('American Football Conference', 'AFC SOUTH', '1995', '1995', 11),"
                    + "('American Football Conference', 'AFC SOUTH', '1960', '1970', 12),"
                    + "('American Football Conference', 'AFC WEST', '1960', '1970', 13),"
                    + "('American Football Conference', 'AFC WEST', '1960', '1970', 14),"
                    + "('American Football Conference', 'AFC WEST', '1960', '1970', 15),"
                    + "('American Football Conference', 'AFC WEST', '1960', '1970', 16);");
            sta.executeUpdate("INSERT INTO `datos` (`conferencia`, `division`, `fundacion`, `entrada_nfl`, `dato_equipo`) VALUES"
                    + "('National Football Conference', 'NFC EAST', '1960', '1960', 17),"
                    + "('National Football Conference', 'NFC EAST', '1925', '1925', 18),"
                    + "('National Football Conference', 'NFC EAST', '1933', '1933', 19),"
                    + "('National Football Conference', 'NFC EAST', '1932', '1932', 20),"
                    + "('National Football Conference', 'NFC NORTH', '1919', '1920', 21),"
                    + "('National Football Conference', 'NFC NORTH', '1929', '1930', 22),"
                    + "('National Football Conference', 'NFC NORTH', '1919', '1921', 23),"
                    + "('National Football Conference', 'NFC NORTH', '1961', '1961', 24),"
                    + "('National Football Conference', 'NFC SOUTH', '1966', '1966', 25),"
                    + "('National Football Conference', 'NFC SOUTH', '1995', '1995', 26),"
                    + "('National Football Conference', 'NFC SOUTH', '1967', '1967', 27),"
                    + "('National Football Conference', 'NFC SOUTH', '1976', '1976', 28),"
                    + "('National Football Conference', 'NFC WEST', '1898', '1920', 29),"
                    + "('National Football Conference', 'NFC WEST', '1936', '1937', 30),"
                    + "('National Football Conference', 'NFC WEST', '1946', '1950', 31),"
                    + "('National Football Conference', 'NFC WEST', '1976', '1976', 32);");
            sta.executeUpdate("INSERT INTO `campeonatos` (`tipo_campeonato`, `numero_victorias`, `fechas_victorias`, `equipo_campeon`) VALUES"
                    + "('AFC', '4', '1990, 1991, 1992, 1993', 1),"
                    + "('AFC', '5', '1971, 1972, 1973, 1982, 1984', 2),"
                    + "('AFC', '11', '1985, 1996, 2001, 2003, 2004, 2007, 2011, 2014, 2016, 2017, 2018', 3),"
                    + "('Super Bowl', '1', '1968', 4),"
                    + "('AFC', '2', '2000, 2012', 5),"
                    + "('AFC', '2', '1981, 1988', 6),"
                    + "('NFL American', '3', '1950, 1951, 1952', 7),"
                    + "('AFC', '8', '1974, 1975, 1978, 1979, 1995, 2005, 2008, 2010', 8),"
                    + "('AFC South', '6','2011, 2012, 2015, 2016, 2018, 2019', 9),"
                    + "('NFL Western', '4', '1958, 1959, 1964, 1968', 10),"
                    + "('AFC Central', '2', '1998, 1999', 11),"
                    + "('AFL East', '4', '1960, 1961, 1962, 1967', 12),"
                    + "('AFC', '8', '1977, 1986, 1987, 1989, 1997, 1998, 2013, 2015', 13),"
                    + "('AFC', '2', '1970, 2019', 14),"
                    + "('AFC', '4', '1976, 1980, 1983, 2002', 15),"
                    + "('NFL Capitol', '3', '1967, 1968, 1969', 16);");
            sta.executeUpdate("INSERT INTO `campeonatos` (`tipo_campeonato`, `numero_victorias`, `fechas_victorias`, `equipo_campeon`) VALUES"
                    + "('NFL Eastern:', '6', '1956, 1958, 1959, 1961, 1962, 1963', 17),"
                    + "('NFC', '3', '1980, 2004, 2017', 18),"
                    + "('NFC', '5', '1972, 1982, 1983, 1987, 1991', 19),"
                    + "('Super Bowls', '1', '1985', 20),"
                    + "('NFL West', '8', '1933, 1934, 1937, 1940, 1941, 1942, 1943, 1946', 21),"
                    + "('NFC Central', '3', '1983, 1991, 1993', 22),"
                    + "('Super Bowls', '4', '1967, 1968, 1997, 2011', 23),"
                    + "('NFC North', '4', '2008, 2009, 2015, 2017', 24),"
                    + "('NFC', '2', '1998, 2016', 25),"
                    + "('NFC South', '5', '2003, 2008, 2013, 2014, 2015', 26),"
                    + "('NFC South', '6', '2006, 2009, 2011, 2017, 2018, 2019', 27),"
                    + "('NFC Central', '3', '1979, 1981, 1999', 28),"
                    + "('NFC West', '3', '2008, 2009, 2015', 29),"
                    + "('NFC', '4', '1979, 1999, 2001, 2018', 30),"
                    + "('Super Bowls', '5', '1982, 1985, 1989, 1990, 1995', 31),"
                    + "('Super Bowls', '1', '2014', 32);");

            sta.close();
            System.out.println("Base de datos creada correctamente");
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }

    //este método borra la base de datos y cambia la url para que se conecte solo al servidor 
    //un posible uso de este método es usar el método anterior a modo de backup
    public void borrarBBDD() {
        urlBBDD = "jdbc:mysql://localhost:3306/";
        try {
            Statement sta = conn1.createStatement();
            sta.executeUpdate("DROP DATABASE IF EXISTS `equipos_nfl`");
            sta.close();
            System.out.println("Base de datos borrada correctamente");
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }

}
