package eu.optimis.interopt.provider.occi;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.core.util.Base64;
import eu.optimis.interopt.provider.JSONEncodingException;
import eu.optimis.interopt.provider.Service;
import eu.optimis.interopt.provider.ServiceComponent;
import eu.optimis.interopt.provider.ServiceInstantiationException;
import eu.optimis.interopt.provider.VMProperties;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author oriol.collell
 */
public class OCCI_RestClient {
    
    public static final String KIND_TAG = "kind";
    public static final String KIND = "http://optimis-project.eu/occi/schemas#optimis_compute";
    public static final String ATTRIBUTES_TAG = "attributes";
    public static final String ID_TAG = "id";
    public static final String ID_PREFIX = "vm";
    public static final String RESOURCES_TAG = "resources";
    public static final String SERVICES_TAG = "services";
    
    private static Logger log = Logger.getLogger(OCCIClient.class);
    
    private String base_url;
    
    private class AuthClientFilter extends ClientFilter {
        
        private String user = null;
        private String pass = null;
        
        public AuthClientFilter(String user, String pass) {
            this.user = user;
            this.pass = pass;
        }
        
        public ClientResponse handle(ClientRequest cr) {
            cr.getHeaders().add(HttpHeaders.AUTHORIZATION, "Basic " + new String(Base64.encode(user + ":" + pass), Charset.forName("ASCII")));
            ClientResponse resp = getNext().handle(cr);
            return resp;
        }
    }
    
    private Client client = null;
    
    public OCCI_RestClient(String user, String pass, String url) {        
        log.info("Creating OCCI Client...");
        client = Client.create();
        client.addFilter(new LoggingFilter(System.out));
        client.addFilter(new AuthClientFilter(user, pass));
        
        this.base_url = url;
    }
    
    public String createVM(String service_id, ServiceComponent sc, int index) throws ServiceInstantiationException {
        WebResource r = client.resource(base_url + "/optimis_compute");
        try {
            ClientResponse res = r.type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, renderCompute(service_id, sc, index));
            if (res.getStatus() != Status.CREATED.getStatusCode()) {
                throw new ServiceInstantiationException("There was a problem while processing the request: " +
                        res.getStatus(), new Exception());
            }
            //return res.getHeaders().get(HttpHeaders.LOCATION).get(0);
            return res.getLocation().toString();
        } catch (JSONEncodingException e) {
            throw new ServiceInstantiationException("There was a problem when transforming the service component to JSON format", e);
        }
    }
    
    public List<VMProperties> getServiceVMs(String serviceId) throws ServiceInstantiationException {
        WebResource r = client.resource(base_url + "/vms/" + serviceId);        
        try {
            JSONObject res = r.accept(MediaType.APPLICATION_JSON_TYPE).get(JSONObject.class);
            
            JSONArray vms_json = res.getJSONArray(RESOURCES_TAG);
            List<VMProperties> vms = new LinkedList<VMProperties>();
            for (int i = 0;i < vms_json.length();i++) {
                vms.add(extractVMProperties(vms_json.getJSONObject(i)));
            }
            return vms;
        } catch (JSONEncodingException e) {
            throw new ServiceInstantiationException("There was a problem when extracting the information about the VMs", e);
        } catch (JSONException e) {
            throw new ServiceInstantiationException("There was a problem when extracting the information about the VMs", e);
        }
    }
    
    public void terminateService(String serviceId) {
        WebResource r = client.resource(base_url + "/vms/" + serviceId);
        r.delete();
    }
        
    public void updateVM(String service_id, ServiceComponent sc, int index) throws ServiceInstantiationException {
        WebResource r = client.resource(base_url + "/vms/" + service_id + "/" + ID_PREFIX + index);
        try {
            ClientResponse res = r.type(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, renderCompute(service_id, sc, index));
            if (res.getStatus() != Status.OK.getStatusCode()) {
                throw new ServiceInstantiationException("There was a problem while processing the request: " +
                        res.getStatus(), new Exception());
            }
        } catch (JSONEncodingException e) {
            throw new ServiceInstantiationException("There was a problem when transforming the service component to JSON format", e);
        }
    }
    
    public void deleteVM(String serviceId, int index) {
        WebResource r = client.resource(base_url + "/vms/" + serviceId + "/" + ID_PREFIX + index);
        r.type(MediaType.APPLICATION_JSON).delete();
    }
    
    public void executeAction(String action, String serviceId, int index, Map<String, String> attrs) throws ServiceInstantiationException {
        WebResource r = client.resource(base_url + "/vms/" + serviceId + "/" + ID_PREFIX + index + "?action=" + action);
        try {
            r.type(MediaType.APPLICATION_JSON).post(renderAction(attrs));
        } catch (JSONEncodingException e) {
            throw new ServiceInstantiationException("There was a problem when transforming the service component to JSON format", e);
        }
    }
    
    public VMProperties getVM(String serviceId, int index) throws ServiceInstantiationException {
        WebResource r = client.resource(base_url + "/vms/" + serviceId + "/" + ID_PREFIX + index);
        JSONObject res = r.accept(MediaType.APPLICATION_JSON_TYPE).get(JSONObject.class);
        try {
            VMProperties vms = extractVMProperties(res);
            return vms;
        } catch (JSONEncodingException e) {
            throw new ServiceInstantiationException("There was a problem when extracting the information about the VMs", e);
        }
    }
    
    public List<Service> getAllVMs() throws ServiceInstantiationException {
        WebResource r = client.resource(base_url + "/vms");
        try {
            JSONArray services_json = r.accept(MediaType.APPLICATION_JSON_TYPE).get(JSONArray.class);
            //JSONArray services_json = res.getJSONArray(SERVICES_TAG);
            List<Service> services = new LinkedList<Service>();
            for (int i = 0;i < services_json.length();i++) {
                JSONObject service_json = services_json.getJSONObject(i);
                Service s = new Service(service_json.getString(ID_TAG));
                JSONArray vms_json = service_json.getJSONArray(RESOURCES_TAG);
                for (int j = 0;j < vms_json.length();j++) {
                    s.addVM(extractVMProperties(vms_json.getJSONObject(j)));
                }
                services.add(s);
            }
            return services;
        } catch (JSONEncodingException e) {
            throw new ServiceInstantiationException("There was a problem when extracting the information about the VMs", e);
        } catch (JSONException e) {
            throw new ServiceInstantiationException("There was a problem when extracting the information about the VMs", e);
        }
    }
    
    private JSONObject renderCompute(String service_id, ServiceComponent sc, Integer index) throws JSONEncodingException{
        JSONObject json = new JSONObject();
        try {
            json.put(KIND_TAG, KIND);
            
            JSONObject attrs = new JSONObject();
            setAttribute(ServiceComponent.OCCI_COMPUTE_ARCHITECTURE, attrs, sc.getArchitecture());
            setAttribute(ServiceComponent.OCCI_COMPUTE_CORES, attrs, sc.getCores());
            setAttribute(ServiceComponent.OCCI_COMPUTE_MEMORY, attrs, sc.getMemory());
            setAttribute(ServiceComponent.OCCI_COMPUTE_SPEED, attrs, sc.getSpeed());
            setAttribute(ServiceComponent.OPTIMIS_VM_IMAGE, attrs, sc.getImage());
            setAttribute(ServiceComponent.OPTIMIS_SERVICE_ID, attrs, service_id);
            json.put(ATTRIBUTES_TAG, attrs);
            json.put(ID_TAG, ID_PREFIX + index.toString());
        } catch (JSONException e) {
            throw new JSONEncodingException("There was an error while trying to convert a "
                    + "ServiceComponent object into a JSON object");
        }
        return json;
    }
    
    private JSONObject renderAction(Map<String, String> attrs) throws JSONEncodingException {
        JSONObject json = new JSONObject();
        try {
            for (String key : attrs.keySet()) {
                json.put(key, attrs.get(key));
            }           
        } catch (JSONException e) {
            throw new JSONEncodingException("There was an error while trying to convert a "
                    + "ServiceComponent object into a JSON object");
        }
        return json;
    }
        
    private void setAttribute(String attr_name, JSONObject attrs, Object value) throws JSONException {
        String parts[] = attr_name.split("\\.");
        setAttributeRec(0,parts,attrs, value);
    }
    
    private void setAttributeRec(int i, String[] parts, JSONObject attrs, Object value) throws JSONException {
        if (i == parts.length - 1) {
            attrs.put(parts[i], value);
            return;
        }
        if (attrs.has(parts[i])) {
            setAttributeRec(i+1, parts,attrs.getJSONObject(parts[i]), value);
        } else {
            attrs.put(parts[i], new JSONObject());
            setAttributeRec(i+1,parts,attrs.getJSONObject(parts[i]), value);
        }
    }
    
    private VMProperties extractVMProperties(JSONObject json) throws JSONEncodingException {
        VMProperties vm = new VMProperties();
        try {
            JSONObject attrs = json.getJSONObject(ATTRIBUTES_TAG);
            vm.setHostname((String) getAttribute(VMProperties.OCCI_COMPUTE_HOSTNAME,attrs));
            vm.setStatus((String) getAttribute(VMProperties.OCCI_COMPUTE_STATUS,attrs));            
            vm.setId(json.getString(ID_TAG));
        } catch (JSONException e) {
            throw new JSONEncodingException("There was an error while trying to convert a "
                    + "ServiceComponent object into a JSON object");
        }
        return vm;
    }
    
    private Object getAttribute(String attr_name, JSONObject attrs) throws JSONException {
        String parts[] = attr_name.split("\\.");
        return getAttributeRec(0,parts,attrs);
    }
    
    private Object getAttributeRec(int i, String[] parts, JSONObject attrs) throws JSONException {
        if (i == parts.length -1) {
            return attrs.get(parts[i]);
        }
        return getAttributeRec(i + 1, parts, attrs.getJSONObject(parts[i]));
    }
    
    public static void main(String[] args) {
        
        OCCI_RestClient cl = new OCCI_RestClient("","","");
        
        List<ServiceComponent> scs = new ArrayList<ServiceComponent>();
        ServiceComponent c1 = new ServiceComponent();
        c1.setArchitecture("x86");
        c1.setCores(2);
        c1.setMemory(2.5);
        c1.setSpeed(0.6);
        c1.setImage("image");
        c1.setInstances(2);
        scs.add(c1);
        
        try {
            JSONObject obj = cl.renderCompute("a",c1,1);
            System.out.println(obj.toString(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
