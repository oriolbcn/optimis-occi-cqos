package eu.optimis.interopt.provider;

import java.util.HashMap;
import java.util.Map;

public class VMProperties extends HashMap<String, String>
    implements Map<String, String>
{
    private static final long serialVersionUID = 1L;

    private String id;
    private String status;
    private String hostname;

    /**
     * the name or IP of the VM instance (i.e. "127.0.0.1")
     */
    public static final String OCCI_COMPUTE_HOSTNAME = "occi.compute.hostname";
    public static final String OCCI_COMPUTE_STATUS = "occi.compute.state";

    /**
     * Returns the id of a particular VM instance.
     * 
     * @return the id of the VM instance
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id of a VM instance.
     * 
     * @param id
     *            the VM id
     */
    public void setId( String id )
    {
        this.id = id;
    }

    /**
     * Retrieves the status of a VM. The status should be compliant to the states as defined by the OCCI
     * specification.
     * 
     * @return VM status
     */
    public String getStatus()
    {
        return status;
    }

    /**
     * Sets the status of a VM. The status should be compliant to the states as defined by the OCCI
     * specification.
     * 
     * @param status
     *            the VM status.
     */
    public void setStatus( String status )
    {
        this.status = status;
    }
    
    public String getHostname() {
        return this.hostname;
    }
    
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

}
