/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eam.tlf.prueba_mini_jpa.Controllers;

import eam.tlf.prueba_mini_jpa.Models.Person;
import java.util.logging.Logger;
import org.junit.jupiter.api.Assertions;

/**
 *
 * @author daryl
 */
public class PersonControllerTest {
    
    private static final Logger log = Logger.getLogger(PersonControllerTest.class.getSimpleName());
    
    public PersonControllerTest() {
    }

    /**
     * Test of persist method, of class PersonController.
     */
    @org.junit.jupiter.api.Test
    public void testPersist() {
        log.info("persist");
        PersonController personController = new PersonController();
        Person person = new Person("1094971007", "Daryl");
        Assertions.assertEquals(true, personController.persist(person));
        personController.dropTable();
    }

    /**
     * Test of update method, of class PersonController.
     */
    @org.junit.jupiter.api.Test
    public void testUpdate() {
        log.info("update");
        PersonController personController = new PersonController();
        Person person = new Person("1094971007", "Daryl");
        personController.persist(person);
        person.setName("da");
        Assertions.assertEquals(true, personController.update(person));
        personController.dropTable();
    }

    /**
     * Test of findById method, of class PersonController.
     */
    @org.junit.jupiter.api.Test
    public void testFindById() {
        log.info("find by id");
        PersonController personController = new PersonController();
        Person person = new Person("1094971007", "Daryl");
        personController.persist(person);
        person = personController.findById(person.getCc());
        Assertions.assertNotEquals(null, person);
        personController.dropTable();
    }

    /**
     * Test of findAll method, of class PersonController.
     */
    @org.junit.jupiter.api.Test
    public void testFindAll() {
        log.info("find all");
        PersonController personController = new PersonController();
        Person person = new Person("1094971007", "Daryl");
        personController.persist(person);
        person = new Person("1094971008", "Daryl1");
        personController.persist(person);
        person = new Person("1094971009", "Daryl2");
        personController.persist(person);
        Assertions.assertEquals(3, personController.findAll().size());
        personController.dropTable();
    }

    /**
     * Test of delete method, of class PersonController.
     */
    @org.junit.jupiter.api.Test
    public void testDelete() {
        log.info("delete");
        PersonController personController = new PersonController();
        Person person = new Person("1094971007", "Daryl");
        personController.persist(person);
        Assertions.assertEquals(true, personController.delete(person.getCc()));
        personController.dropTable();
    }
    
}
