/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eam.tlf.prueba_mini_jpa.Models;

import eam.tlf.mini_jpa.annotatios.Column;
import eam.tlf.mini_jpa.annotatios.Entity;

/**
 *
 * @author daryl
 * @date 31/10/2019
 */
@Entity(name = "Person", schema = "public")
public class Person {
    
    @Column(name = "id", isPk = true)
    private String cc;
    
    @Column(name = "name")
    private String name;

    public Person() {
    }

    public Person(String cc, String name) {
        this.cc = cc;
        this.name = name;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
