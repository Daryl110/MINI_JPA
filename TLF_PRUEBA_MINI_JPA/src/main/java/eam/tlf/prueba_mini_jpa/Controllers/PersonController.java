/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eam.tlf.prueba_mini_jpa.Controllers;

import eam.tlf.mini_jpa.annotatios.Entity;
import eam.tlf.mini_jpa.connection.Persistence;
import eam.tlf.prueba_mini_jpa.Models.Person;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daryl
 * @date 1/11/2019
 */
public class PersonController {

    public PersonController() {
        this.createTable();
    }

    public boolean persist(Person person) {
        try {
            return !Persistence.persist(person);
        } catch (Exception ex) {
            Logger.getLogger(PersonController.class.getName()).log(Level.SEVERE,
                    null, " error al insentar en " + Person.class.getAnnotation(Entity.class).name() + " " + ex);
            return false;
        }
    }

    public boolean update(Person person) {
        try {
            return !Persistence.update(person);
        } catch (Exception ex) {
            Logger.getLogger(PersonController.class.getName()).log(Level.SEVERE,
                    null, " error al insentar en " + Person.class.getAnnotation(Entity.class).name() + " " + ex);
            return false;
        }
    }

    public Person findById(String cc) {
        try {
            ResultSet resultSet = Persistence.get(Person.class, cc);
            resultSet.next();
            return new Person(resultSet.getString("id"), resultSet.getString("name"));
        } catch (Exception ex) {
            Logger.getLogger(PersonController.class.getName()).log(Level.SEVERE,
                    null, " error al obtener en " + Person.class.getAnnotation(Entity.class).name() + " con el id : " + cc + " " + ex);
            return null;
        }
    }

    public List<Person> findAll() {
        try {
            List<Person> persons = new ArrayList<>();
            ResultSet resultSet = Persistence.get(Person.class);

            while (resultSet.next()) {
                persons.add(new Person(resultSet.getString("id"), resultSet.getString("name")));
            }

            return persons;
        } catch (Exception ex) {
            Logger.getLogger(PersonController.class.getName()).log(Level.SEVERE,
                    null, " error al obtener en " + Person.class.getAnnotation(Entity.class).name() + " " + ex);
            return null;
        }
    }
    
    public boolean delete(String cc) {
        try {
            return !Persistence.delete(Person.class, cc);
        } catch (Exception ex) {
            Logger.getLogger(PersonController.class.getName()).log(Level.SEVERE,
                    null, " error al eliminar en " + Person.class.getAnnotation(Entity.class).name() + " con el id : " + cc + " " + ex);
            return false;
        }
    }
    
    public boolean createTable(){
        try {
            return !Persistence.create(Person.class);
        } catch (Exception ex) {
            Logger.getLogger(PersonController.class.getName()).log(Level.SEVERE,
                    null, " error al crear tabla " + Person.class.getAnnotation(Entity.class).name() + " " + ex.getMessage());
            return false;
        }
    }
    
    public boolean dropTable(){
        try {
            return !Persistence.dropTable(Person.class);
        } catch (Exception ex) {
            Logger.getLogger(PersonController.class.getName()).log(Level.SEVERE,
                    null, " error al eliminar tabla " + Person.class.getAnnotation(Entity.class).name() + " " + ex.getMessage());
            return false;
        }
    }
}
