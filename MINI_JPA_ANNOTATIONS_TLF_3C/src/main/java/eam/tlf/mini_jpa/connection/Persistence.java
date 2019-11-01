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

    private Connection getConnection(Class clase) throws Exception {
        Properties properties = this.loadProperties(clase);
        Class.forName(properties.getProperty("driver"));
        return DriverManager.getConnection(
                properties.getProperty("url"),
                properties.getProperty("user"),
                properties.getProperty("password")
        );
    }

    private Properties loadProperties(Class clase) throws IOException {
        Properties properties = new Properties();
        InputStream input = clase.getClassLoader().getResourceAsStream(PROPERTIES);
        properties.load(input);
        return properties;
    }
    
    // Insert query
    private String createPersistQuery(Object obj) {
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

    private List<Object> getValues(Object obj) throws IllegalArgumentException, IllegalAccessException,
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

    private PreparedStatement createPreparedStatement(String query, Object obj) throws Exception {
        Class clase = obj.getClass();
        List values = this.getValues(obj);
        Connection connection = this.getConnection(obj.getClass());
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        int i = 1;
        for (Object value : values) {
            preparedStatement.setObject(i, value);
            i++;
        }

        return preparedStatement;
    }

    /**
     * ****************************** CRUD ***********************************
     */
    public void persist(Object obj) throws Exception {
        String query = this.createPersistQuery(obj);
        PreparedStatement preparedStatement = this.createPreparedStatement(query, obj);
        preparedStatement.execute();
    }
    /**
     * **************************** FIN CRUD *********************************
     */
}
