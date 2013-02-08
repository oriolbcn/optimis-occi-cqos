/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.optimis.interopt.provider;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author A545568
 */
public class Service {
    
    private String id;
    private List<VMProperties> vms;
    
    public Service() {
        vms = new LinkedList<VMProperties>();
    }

    public Service(String id) {
        vms = new LinkedList<VMProperties>();
        this.id = id;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<VMProperties> getVms() {
        return vms;
    }

    public void setVms(List<VMProperties> vms) {
        this.vms = vms;
    }
    
    public void addVM(VMProperties vm) {
        vms.add(vm);
    }
}
