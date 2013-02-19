package eu.optimis.interopt.provider;

import java.util.HashMap;

public class ServiceComponent
{
    
    //occi.compute
    private String architecture;
    private double speed;
    private double memory;
    private int cores;
    
    //optimis.occi
    private String image;
    private int instances;

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getMemory() {
        return memory;
    }

    public void setMemory(double memory) {
        this.memory = memory;
    }

    public int getCores() {
        return cores;
    }

    public void setCores(int cores) {
        this.cores = cores;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getInstances() {
        return instances;
    }

    public void setInstances(int instances) {
        this.instances = instances;
    }
    
    /**
     * compute architecture of the VM instances (i.e. "x86")
     */
    public static final String OCCI_COMPUTE_ARCHITECTURE = "occi.compute.architecture";

    /**
     * the individual CPU speed for the VM instances (i.e. "1.33")
     */
    public static final String OCCI_COMPUTE_SPEED = "occi.compute.speed";

    /**
     * the required individual memory of each compute instance (i.e. "2.0")
     */
    public static final String OCCI_COMPUTE_MEMORY = "occi.compute.memory";

    /**
     * the individual CPU cores of each VM instance (i.e. "2")
     */
    public static final String OCCI_COMPUTE_CORES = "occi.compute.cores";

    /**
     * the URI of the VM image to start for each instance (i.e.
     * http://datamanager.optimis.eu/vm#e3ac-a34l-1234)
     */
    public static final String OPTIMIS_VM_IMAGE = "optimis.occi.optimis_compute.image";
    
    /**
     * the number of VM instances to provide for this component (i.e. "4")
     */
    public static final String OPTIMIS_SERVICE_ID = "optimis.occi.optimis_compute.service_id";
}
