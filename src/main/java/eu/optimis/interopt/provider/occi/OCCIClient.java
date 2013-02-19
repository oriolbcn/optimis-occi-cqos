package eu.optimis.interopt.provider.occi;

import eu.optimis.interopt.provider.Service;
import eu.optimis.interopt.provider.ServiceComponent;
import eu.optimis.interopt.provider.ServiceInstantiationException;
import eu.optimis.interopt.provider.VMManagementSystemClient;
import eu.optimis.interopt.provider.VMProperties;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import java.net.UnknownServiceException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.log4j.Logger;

public class OCCIClient
        implements VMManagementSystemClient
{
    private static Logger log = Logger.getLogger( OCCIClient.class );

    private static int maxvms = 10;
    private String username;
    private String password;
    private String url;
    
    public void setAuth(String auth_username, String password) {
        this.username = auth_username;
        this.password = password;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void deployService(String service_id, List<ServiceComponent> serviceComponents, XmlBeanServiceManifestDocument manifest) throws ServiceInstantiationException {
        
        OCCI_RestClient rc = new OCCI_RestClient(username, password, url);
        log.debug( "OCCI REST Client is instantiated" );
        
        if (isDeployed(service_id)) {
            //throw new ServiceInstantiationException("This service is already deployed! "
            //        + "Terminate it before deploying it again.", new java.lang.Throwable());
        }
        // Get the number of VMs to deploy
        int total_vms = 0;

        for (ServiceComponent sc : serviceComponents) {
            total_vms = total_vms + sc.getInstances();
        }

        // If sum < maxvms invoke createVM method as many times as needed
        if ( total_vms > OCCIClient.maxvms )
        {
            throw new ServiceInstantiationException("Number of VMs to deploy exceeds the maximum", new java.lang.Throwable() );
        }

        for (ServiceComponent sc : serviceComponents) {
            int numInstances = sc.getInstances();
            log.info( "number of vm instances to deploy: " +  numInstances);                
            for (int j = 0; j < numInstances; j++) {
                // Invoke the service and get response
                log.info("creating vm for service [" + service_id + "]");
                String res = rc.createVM(service_id, sc, j+1);
                // Convert base64 response to xml string
                log.info("response: " + res);
                //Check if VM has been succesfully created
                
                // TODO: Check if creation was correct
                /*if (true) {
                    log.error( "service deployment has failed" );
                    throw new ServiceInstantiationException( "Service deployment has failed: " + res,
                            new java.lang.Throwable() );
                }*/
                
                log.trace(res);
            }
        }
    }

    @Override
    public List<VMProperties> queryServiceProperties(String serviceId) throws UnknownServiceException {

        try {
            OCCI_RestClient rc = new OCCI_RestClient(username, password, url);
            log.debug("OCCI REST Client is instantiated");

            List<VMProperties> res = rc.getServiceVMs(serviceId);
            return res;
        }
        catch (Exception e) {
            throw new UnknownServiceException("Service not found");
        }
    }

    @Override
    public void terminate(String serviceId) throws UnknownServiceException {

        OCCI_RestClient rc = new OCCI_RestClient(username, password, url);
        log.debug("OCCI REST Client is instantiated");

        rc.terminateService(serviceId);

        // TODO: Check if result is ok
        
        log.info("Servce [" + serviceId + "] terminated successfully.");
    }
    
    public boolean isDeployed(String serviceId) {
        List<VMProperties> vms = null;        
        try {
            vms = queryServiceProperties(serviceId);
        } catch (UnknownServiceException e) {
            return false;
        }
        for (VMProperties vm : vms) {
            //System.out.println(vm.getStatus() + " - " + InstanceStateName.Terminated.toString());
            if (!(vm.getStatus().equals("terminated") ||
                    vm.getStatus().equals("terminating"))) {
                return true;
            }
        }
        return false;
    }
    
    public void deleteVM(String serviceId, int index) {
        OCCI_RestClient rc = new OCCI_RestClient(username, password, url);
        rc.deleteVM(serviceId, index);
    }
    
    public void updateVM(String serviceId, ServiceComponent sc, int index) {
        OCCI_RestClient rc = new OCCI_RestClient(username, password, url);
        try {
            rc.updateVM(serviceId, sc, index);
        } catch (ServiceInstantiationException ex) {
            java.util.logging.Logger.getLogger(OCCIClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void executeAction(String serviceId, String action, int index, Map<String, String> attrs) {
        OCCI_RestClient rc = new OCCI_RestClient(username, password, url);
        try {
            rc.executeAction(action, serviceId, index, attrs);
        } catch (ServiceInstantiationException ex) {
            java.util.logging.Logger.getLogger(OCCIClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public VMProperties getVM(String serviceId, int index) {
        OCCI_RestClient rc = new OCCI_RestClient(username, password, url);
        VMProperties res = null;
        try {
            res = rc.getVM(serviceId, index);
        } catch (ServiceInstantiationException ex) {
            java.util.logging.Logger.getLogger(OCCIClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
    
    public List<Service> getAllVMs() throws UnknownServiceException {
        try {
            OCCI_RestClient rc = new OCCI_RestClient(username, password, url);
            List<Service> res = rc.getAllVMs();
            return res;
        }
        catch (Exception e) {
            throw new UnknownServiceException("Service not found");
        }
    }
    
}
