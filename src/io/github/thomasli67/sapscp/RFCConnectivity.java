package io.github.thomasli67.sapscp;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;
import org.json.JSONException;
import org.json.JSONObject;
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.ext.DestinationDataProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Servlet implementation class RFCConnectivity
 */
//@WebServlet("/RFCConnectivity")
public class RFCConnectivity extends HttpServlet {
	private static final long serialVersionUID = 1L;  
    /**
     * @see HttpServlet#HttpServlet()
     */
	
	static
    {
        Properties connectProperties = new Properties();
        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, "10.8.5.144");
        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,  "00");
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "180");
        connectProperties.setProperty(DestinationDataProvider.JCO_USER,   "lizh");
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, "li671122");
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG,   "en");
        // createDataFile("ECC", "jcoDestination", connectProperties);
    }
	
    public RFCConnectivity() {
        super();
    }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException {
		 PrintWriter responseWriter = response.getWriter();
	        try
	        {
	            // LINE NO 38
	            // Access the SAP Cloud Platform Destination "Q7W_RFC"

	        	JCoDestination destination=JCoDestinationManager.getDestination("ECC");
	            // Make an invocation of STFC_CONNECTION in the backend
	            JCoRepository repo=destination.getRepository();
	            JCoFunction stfcConnection=repo.getFunction("STFC_CONNECTION");
	            // Set Importing Parameter REQUTEXT for the RFC
	            JCoParameterList imports=stfcConnection.getImportParameterList();
	            imports.setValue("REQUTEXT", "SAP HANA Cloud connectivity for SAP CP Workflow");
	            //Execute the RFC via the SAP CP Destination
	            stfcConnection.execute(destination);
	            // Get the exporting parameters ECHOTEXT and RESPTEXT of the RFC
	            JCoParameterList exports=stfcConnection.getExportParameterList();
	            String echotext=exports.getString("ECHOTEXT");
	            String resptext=exports.getString("RESPTEXT");
	            
	            // Output of this servlet needs to be JSON if consumed from SAP CP Workflow
	            JSONObject responseJson = new JSONObject();
	            // Form the JSON object
	            responseJson.put("echotext", echotext);
	            responseJson.put("resptext", resptext);
	            // LINE NO 57 
	            response.addHeader("Content-type", "application/json");
	            // Set the response as the JSON STRING
	            responseWriter.write(responseJson.toString());
	        }
	        catch (AbapException ae)
	        {
	            //TODO Handle Exception.
	        	ae.printStackTrace();
	        }
	        catch (JCoException e)
	        {
	            // TODO Handle Exception
	        	e.printStackTrace();
	        	responseWriter.write(e.toString());
	        } catch (JSONException e) {
	        	// TODO handle expetion
				e.printStackTrace();
			}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	static void createDataFile(String name, String suffix, Properties properties)
    {
        File cfg = new File(name+"."+suffix);
        if(!cfg.exists())
        {
            try
            {
                FileOutputStream fos = new FileOutputStream(cfg, false);
                properties.store(fos, "for tests only !");
                fos.close();
            }
            catch (Exception e)
            {
                throw new RuntimeException("Unable to create the destination file " + cfg.getName(), e);
            }
        }
    }


}