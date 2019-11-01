/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eam.tlf.prueba_mini_jpa;

import eam.tlf.mini_jpa.connection.Persistence;
import eam.tlf.prueba_mini_jpa.Models.Person;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 *
 * @author daryl
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Persistence persistence = new Persistence();

        try {
            Person person = new Person("1094971007", "Daryl");
            persistence.persist(person);
        } catch (Exception e) {
            Logger.getLogger(Persistence.class.getSimpleName()).severe(e.getMessage());
        }
    }

}
