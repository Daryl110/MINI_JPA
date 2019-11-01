/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eam.tlf.mini_jpa.connection;

import eam.tlf.mini_jpa.annotatios.Column;
import eam.tlf.mini_jpa.annotatios.Entity;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author daryl
 */
public class Persistence {

    public static final String PROPERTIES = "persistence.properties";

    private static Connection getConnection(Class clase) throws Exception {
        Properties properties = Persistence.loadProperties(clase);
        Class.forName(properties.getProperty("driver"));
        return DriverManager.getConnection(
                properties.getProperty("url"),
                properties.getProperty("user"),
                properties.getProperty("password")
        );
    }

    private static Properties loadProperties(Class clase) throws IOException {
        Properties properties = new Properties();
        InputStream input = clase.getClassLoader().getResourceAsStream(PROPERTIES);
        properties.load(input);
        return properties;
    }

    // Create table query
    private static String createDropTableQuery(Class clase) {
        String query = "";
        Entity entity = (Entity) clase.getAnnotation(Entity.class);

        if (entity != null) {
            query += "DROP TABLE " + entity.schema() + "." + entity.name();
        } else {
            query += "DROP TABLE " + clase.getSimpleName();
        }

        return query;
    }
    
    // Create table query
    private static String createQuery(Class clase) {
        String query = "";
        Entity entity = (Entity) clase.getAnnotation(Entity.class);

        if (entity != null) {
            query += "CREATE TABLE " + entity.schema() + "." + entity.name();
        } else {
            query += "CREATE TABLE " + clase.getSimpleName();
        }

        query += "(";

        String cols = "";

        Field[] attributes = clase.getDeclaredFields();
        for (Field field : attributes) {
            Column column = (Column) field.getAnnotation(Column.class);

            if (column == null) {
                break;
            }

            cols += column.name();

            switch (field.getType().getSimpleName()) {
                case "String":
                    cols += " varchar(255)";
                    break;
                default:
                    throw new AssertionError();
            }

            cols += ",";
        }

        return query + cols.substring(0, cols.length() - 1) + ");";
    }

    // Select query by id
    private static String createSelectQuery(Class clase, Object id) {
        String query = "";
        Entity entity = (Entity) clase.getAnnotation(Entity.class);

        if (entity != null) {
            query += "SELECT * FROM " + entity.schema() + "." + entity.name();
        } else {
            query += "SELECT * FROM " + clase.getSimpleName();
        }

        if (id != null) {
            query += " WHERE ";

            String equals = "";

            Field[] attributes = clase.getDeclaredFields();
            for (Field field : attributes) {
                Column column = (Column) field.getAnnotation(Column.class);

                if (column == null) {
                    break;
                }

                if (column.isPk()) {
                    equals += column.name() + "=";

                    switch (id.getClass().getSimpleName()) {
                        case "String":
                        case "Date":
                            equals += "\'" + id + "\'";
                            break;
                        default:
                            equals += id;
                    }
                }
            }

            return query + equals + ";";
        } else {
            return query + ";";
        }
    }

    // Delete query
    private static String createDeleteQuery(Class clase, Object id) {
        String query = "";
        Entity entity = (Entity) clase.getAnnotation(Entity.class);

        if (entity != null) {
            query += "DELETE FROM " + entity.schema() + "." + entity.name();
        } else {
            query += "DELETE FROM " + clase.getSimpleName();
        }

        if (id != null) {
            query += " WHERE ";

            String equals = "";

            Field[] attributes = clase.getDeclaredFields();
            for (Field field : attributes) {
                Column column = (Column) field.getAnnotation(Column.class);

                if (column == null) {
                    break;
                }

                if (column.isPk()) {
                    equals += column.name() + "=";

                    switch (id.getClass().getSimpleName()) {
                        case "String":
                        case "Date":
                            equals += "\'" + id + "\'";
                            break;
                        default:
                            equals += id;
                    }
                }
            }

            return query + equals + ";";
        } else {
            return query + ";";
        }
    }

    // Insert query
    private static String createPersistQuery(Object obj) {
        Class clase = obj.getClass();
        String query = "";
        Entity entity = (Entity) clase.getAnnotation(Entity.class);

        if (entity != null) {
            query += "INSERT INTO " + entity.schema() + "." + entity.name();
        } else {
            query += "INSERT INTO " + clase.getSimpleName();
        }

        query += "(";

        String params = "";
        String cols = "";

        Field[] attributes = clase.getDeclaredFields();
        for (Field field : attributes) {
            Column column = (Column) field.getAnnotation(Column.class);

            if (column == null) {
                break;
            }

            cols += column.name() + ",";
            params += "?,";
        }

        query += cols.substring(0, cols.length() - 1) + ")";

        return query + " VALUES (" + params.substring(0, params.length() - 1) + ");";
    }

    // Update query
    private static String createUpdateQuery(Object obj) throws NullPointerException {
        Class clase = obj.getClass();
        String query = "";
        Entity entity = (Entity) clase.getAnnotation(Entity.class);

        if (entity != null) {
            query += "UPDATE " + entity.schema() + "." + entity.name();
        } else {
            query += "UPDATE " + clase.getSimpleName();
        }

        query += " SET ";

        String cols = "";

        Field[] attributes = clase.getDeclaredFields();
        Field id = null;
        for (Field field : attributes) {
            Column column = (Column) field.getAnnotation(Column.class);

            if (column == null) {
                break;
            }

            if (!column.isPk()) {
                cols += column.name() + "=?,";
            } else {
                id = field;
            }
        }

        query += cols.substring(0, cols.length() - 1) + " WHERE " + ((Column) id.getAnnotation(Column.class)).name() + "=";

        return query + "?;";
    }

    private static List<Object> getValues(Object obj) throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException, SecurityException {

        Class clase = obj.getClass();
        List values = new ArrayList<>();
        Field[] attributes = clase.getDeclaredFields();

        for (Field field : attributes) {
            Column col = (Column) field.getAnnotation(Column.class);
            if (col == null) {
                break;
            }
            String getter = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
            Method method = clase.getMethod(getter);
            Object value = method.invoke(obj);
            values.add(value);
        }

        return values;
    }

    private static PreparedStatement createPreparedStatement(String query, Object obj, boolean update) throws Exception {
        List values = Persistence.getValues(obj);
        Connection connection = Persistence.getConnection(obj.getClass());
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        int j = 1, i;

        if (update) {
            i = 1;
        } else {
            i = 2;
        }

        while (j < values.size()) {
            preparedStatement.setObject(i, values.get(j));
            i++;
            j++;
        }

        if (update) {
            preparedStatement.setObject(values.size(), values.get(0));
        } else {
            preparedStatement.setObject(1, values.get(0));
        }

        return preparedStatement;
    }

    /*
     * ****************************** CRUD ***********************************
     */
    public static boolean dropTable(Class clase) throws Exception {
        String query = Persistence.createDropTableQuery(clase);
        Connection connection = Persistence.getConnection(clase);
        Statement statement = connection.createStatement();
        return statement.execute(query);
    }
    
    public static boolean create(Class clase) throws Exception {
        String query = Persistence.createQuery(clase);
        Connection connection = Persistence.getConnection(clase);
        Statement statement = connection.createStatement();
        return statement.execute(query);
    }

    public static ResultSet get(Class clase, Object id) throws Exception {
        String query = Persistence.createSelectQuery(clase, id);
        Connection connection = Persistence.getConnection(clase);
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public static ResultSet get(Class clase) throws Exception {
        String query = Persistence.createSelectQuery(clase, null);
        Connection connection = Persistence.getConnection(clase);
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public static boolean delete(Class clase, Object id) throws Exception {
        String query = Persistence.createDeleteQuery(clase, id);
        Connection connection = Persistence.getConnection(clase);
        Statement statement = connection.createStatement();
        return statement.execute(query);
    }

    public static boolean update(Object obj) throws Exception {
        String query = Persistence.createUpdateQuery(obj);
        PreparedStatement preparedStatement = Persistence.createPreparedStatement(query, obj, true);
        return preparedStatement.execute();
    }

    public static boolean persist(Object obj) throws Exception {
        String query = Persistence.createPersistQuery(obj);
        PreparedStatement preparedStatement = Persistence.createPreparedStatement(query, obj, false);
        return preparedStatement.execute();
    }
    /*
     * **************************** FIN CRUD *********************************
     */
}
