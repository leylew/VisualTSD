package edu.uiuc.zenvisage.api;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import edu.uiuc.zenvisage.service.ZvMain;

@Controller
public class ZvBasicAPI {
	@Autowired
	private ZvMain zvMain;
    public ZvBasicAPI(){};
    
//    @RequestMapping(value = "/login", method = RequestMethod.POST)
//	@ResponseBody
//	public void userLogin(HttpServletRequest request, HttpServletResponse response) throws ClassNotFoundException, InterruptedException, IOException, ServletException, SQLException {
//    	ObjectMapper mapper = new ObjectMapper();
//    	if(zvMain.userLogin(request)) {
//    		Map<String,String> res = new HashMap<String, String>();
//    		res.put("res","Success");
//			response.getWriter().write(mapper.writeValueAsString(res));
//		}
//		else {
//			Map<String,String> res = new HashMap<String, String>();
//    		res.put("res","Fail");
//			response.getWriter().write(mapper.writeValueAsString(res));
//			}
//	}

	@RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
	@ResponseBody
	public void fileUpload(HttpServletRequest request, HttpServletResponse response) throws ClassNotFoundException, InterruptedException, IOException, ServletException, SQLException {
		zvMain.fileUpload(request, response);
	}

	@RequestMapping(value = "/postRepresentative", method = RequestMethod.POST)
	@ResponseBody
	public String postRepresentative(HttpServletRequest request, HttpServletResponse response) throws InterruptedException, IOException, SQLException {
		StringBuilder stringBuilder = new StringBuilder();
	    Scanner scanner = new Scanner(request.getInputStream());
	    while (scanner.hasNextLine()) {
	        stringBuilder.append(scanner.nextLine());
	    }
	    String body = stringBuilder.toString();
		return zvMain.runDragnDropInterfaceQuerySeparated(body, "RepresentativeTrends");
	}

	@RequestMapping(value = "/postOutlier", method = RequestMethod.POST)
	@ResponseBody
	public String postOutlier(HttpServletRequest request, HttpServletResponse response) throws InterruptedException, IOException, SQLException {
		StringBuilder stringBuilder = new StringBuilder();
	    Scanner scanner = new Scanner(request.getInputStream());
	    while (scanner.hasNextLine()) {
	        stringBuilder.append(scanner.nextLine());
	    }
	    String body = stringBuilder.toString();
		return zvMain.runDragnDropInterfaceQuerySeparated(body, "Outlier");
	}

	@RequestMapping(value = "/postSimilarity", method = RequestMethod.POST)
	@ResponseBody
	public String postSimilarity(HttpServletRequest request, HttpServletResponse response) throws InterruptedException, IOException, SQLException {
		StringBuilder stringBuilder = new StringBuilder();
	    Scanner scanner = new Scanner(request.getInputStream());
	    while (scanner.hasNextLine()) {
	        stringBuilder.append(scanner.nextLine());
	    }
	    String body = stringBuilder.toString();
		return zvMain.runDragnDropInterfaceQuerySeparated(body, "SimilaritySearch");
	}

	@RequestMapping(value = "/getformdata", method = RequestMethod.GET)
	@ResponseBody
	public String getformdata(@RequestParam(value="query") String arg) throws JsonGenerationException, JsonMappingException, IOException, InterruptedException, SQLException {
		return zvMain.getInterfaceFomData(arg);
	}
}
