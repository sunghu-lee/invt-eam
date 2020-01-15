package com.naviworks.invt.controller;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.naviworks.invt.dao.ApprovallineDAO;
import com.naviworks.invt.dao.EmployeeDAO;
import com.naviworks.invt.dao.EmployeeDTO;
import com.naviworks.invt.model.EmployeeInfo;
import com.naviworks.invt.model.ResponseMessage;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class EAMController
{
	@Autowired
	private EmployeeDAO dao = null;
	
	@Autowired
	private ApprovallineDAO daoA = null;
	
	RestTemplate restTemplate;
	
	ResponseMessage message = null;
	private static final Logger logger = LoggerFactory.getLogger(EAMController.class);
	
	@GetMapping()
	public String mainPage()
	{
		return "mainEAM-0115, Naviworks Co., Sales@naviworks.com 031-687-2000 경기도 안양시";
	}
	
	@GetMapping("/Approval")
	@ApiOperation(value="Approval", notes="Make Approval Information at Employee DB in Naviworks Co.")
	@ApiImplicitParams({
		@ApiImplicitParam(name="cid", value="임직원 사번", dataType="string"),
		@ApiImplicitParam(name="LineID", value="결재라인 번호(1:근태신청서, 2:구매 검수 및 지출 결의서", dataType="String")
	})
	public ResponseEntity<ResponseMessage> Approval(@RequestParam(value="cid", required=false , defaultValue="")String cid,
												  @RequestParam(value="LineID", required=false , defaultValue="")String LineID) throws JsonProcessingException, IOException
	{
		restTemplate = new RestTemplate();
		message = restTemplate.getForObject("https://invt-1578939256727.azurewebsites.net/Search?cid=" + cid, ResponseMessage.class);
		
		String res = message.getMessage();
		ObjectMapper mapper = new ObjectMapper();
		//전달해 주는 JSON 값을 모두 다 받아서 처리할 이유가 없다면 내가 필요한 값만 DTO 클래스에 정의하여 바인드
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);		
		
	    List<EmployeeDTO> employees = mapper.reader()
	      .forType(new TypeReference<List<EmployeeDTO>>() {})
	      .readValue(res);
	    	 
	    Assert.assertEquals(1, employees.size());

	    // 검색한 직원의 정보를 가져옴
	    String empName = employees.get(0).getName();
	    String empHead = employees.get(0).getHead();
	    String empDept = employees.get(0).getDepartment();
	    
	    // 테스트 완료   
	    // logger.info("검색한 임직원 정보 : " + empName + ", " + empHead + ", " + empDept);
	    
	    String strEmpFlag = dao.getInfoByFlag(cid).getEmployeeFlag().toString();
	    String[] empApprovalLine = strEmpFlag.split("-");
	    
	    String strApprovalline = daoA.getInfoByLineInfo(LineID).getLineInfo().toString();
	    String strApprovallineName = daoA.getInfoByLineName(LineID).getLineName().toString();
	    
	    // 테스트 완료
	    logger.info(empName + "님의 Depth 정보 : " + strEmpFlag);
	    
	    for(int i = 0; i < empApprovalLine.length; i++)
	    {
	    	logger.info(empName + "님의 Depth " + i + "정보 : " + empApprovalLine[i]);
	    }
	    
	    String[] selApprovalline = strApprovalline.split("-");
	    
	    // 경영혁신본부
	    if(empApprovalLine[1].equals("1"))
	    {
	    	// 모든 팀장의 경우
	    	if(empApprovalLine[5].equals("3"))
	    	{
	    		for(int i = 0; i < selApprovalline.length; i++ )
	    		{
	    			if(selApprovalline[i].equals("파트장")) selApprovalline[i] = "-";
	    			if(selApprovalline[i].equals("팀장")) selApprovalline[i] = "-";
	    			if(selApprovalline[i].equals("연구소장")) selApprovalline[i] = "-";
	    			if(selApprovalline[i].equals("본부장")) selApprovalline[i] = "윤덕상";
	    		}
	    	}
	    	// 본부장의 경우
	    	if(empApprovalLine[5].equals("4"))
	    	{
	    		for(int i = 0; i < selApprovalline.length; i++ )
	    		{
	    			if(selApprovalline[i].equals("파트장")) selApprovalline[i] = "-";
	    			if(selApprovalline[i].equals("팀장")) selApprovalline[i] = "-";
	    			if(selApprovalline[i].equals("연구소장")) selApprovalline[i] = "-";
	    			if(selApprovalline[i].equals("본부장")) selApprovalline[i] = "원준희";
	    		}
	    	}
	    	// 경영지원팀
	    	if(empApprovalLine[2].equals("1"))
	    	{
	    		// 일반 사원의 경우
	    		if(empApprovalLine[5].equals("1"))
	    		{
	    			for(int i = 0; i < selApprovalline.length; i++ )
	    			{
	    				if(selApprovalline[i].equals("파트장")) selApprovalline[i] = "-";
		    			if(selApprovalline[i].equals("팀장")) selApprovalline[i] = "고미량";
		    			if(selApprovalline[i].equals("연구소장")) selApprovalline[i] = "-";
		    			if(selApprovalline[i].equals("본부장")) selApprovalline[i] = "윤덕상";
	    			}
	    		}
	    	}
	    	// 솔루션혁신팀
	    	else if(empApprovalLine[2].equals("2"))
	    	{
	    		// 일반 사원의 경우
	    		if(empApprovalLine[5].equals("1"))
	    		{
	    			for(int i = 0; i < selApprovalline.length; i++ )
	    			{
	    				if(selApprovalline[i].equals("파트장")) selApprovalline[i] = "-";
		    			if(selApprovalline[i].equals("팀장")) selApprovalline[i] = "오준호";
		    			if(selApprovalline[i].equals("연구소장")) selApprovalline[i] = "-";
		    			if(selApprovalline[i].equals("본부장")) selApprovalline[i] = "윤덕상";
	    			}
	    		}
	    	}
	    	// 신사업영업팀
	    	else if(empApprovalLine[2].equals("3"))
	    	{
	    		// 일반 사원의 경우
	    		if(empApprovalLine[5].equals("1"))
	    		{
	    			for(int i = 0; i < selApprovalline.length; i++ )
	    			{
	    				if(selApprovalline[i].equals("파트장")) selApprovalline[i] = "-";
		    			if(selApprovalline[i].equals("팀장")) selApprovalline[i] = "이자훈";
		    			if(selApprovalline[i].equals("연구소장")) selApprovalline[i] = "-";
		    			if(selApprovalline[i].equals("본부장")) selApprovalline[i] = "윤덕상";
	    			}
	    		}
	    	}
	    	// 품질서비스혁신팀
	    	else if(empApprovalLine[2].equals("4"))
	    	{
	    		// 일반 사원의 경우
	    		if(empApprovalLine[5].equals("1"))
	    		{
	    			for(int i = 0; i < selApprovalline.length; i++ )
	    			{
	    				if(selApprovalline[i].equals("파트장")) selApprovalline[i] = "-";
		    			if(selApprovalline[i].equals("팀장")) selApprovalline[i] = "구본홍";
		    			if(selApprovalline[i].equals("연구소장")) selApprovalline[i] = "-";
		    			if(selApprovalline[i].equals("본부장")) selApprovalline[i] = "윤덕상";
	    			}
	    		}
	    	}
	    }
	    // 연구개발본부
	    else if(empApprovalLine[1].equals("2"))
	    {
	    	// 본부장의 경우
	    	if(empApprovalLine[5].equals("4"))
	    	{
	    		for(int i = 0; i < selApprovalline.length; i++ )
	    		{
	    			if(selApprovalline[i].equals("파트장")) selApprovalline[i] = "-";
	    			if(selApprovalline[i].equals("팀장")) selApprovalline[i] = "-";
	    			if(selApprovalline[i].equals("연구소장")) selApprovalline[i] = "-";
	    			if(selApprovalline[i].equals("본부장")) selApprovalline[i] = "원준희";
	    		}
	    	}
	    	
	    	// 제1연구소
	    	if(empApprovalLine[2].equals("1"))
	    	{
	    		// 미래기술연구팀
	    		if(empApprovalLine[3].equals("1"))
	    		{
	    			// 일반
	    			if(empApprovalLine[5].equals("1"))
		    		{
		    			for(int i = 0; i < selApprovalline.length; i++ )
		    			{
		    				if(selApprovalline[i].equals("파트장")) selApprovalline[i] = "-";
			    			if(selApprovalline[i].equals("팀장")) selApprovalline[i] = "-";
			    			if(selApprovalline[i].equals("연구소장")) selApprovalline[i] = "안재욱";
			    			if(selApprovalline[i].equals("본부장")) selApprovalline[i] = "서성만";
		    			}
		    		}
	    			// 팀장(연구소장)의 경우
	    			else if(empApprovalLine[5].equals("3"))
		    		{
		    			for(int i = 0; i < selApprovalline.length; i++ )
		    			{
		    				if(selApprovalline[i].equals("파트장")) selApprovalline[i] = "-";
			    			if(selApprovalline[i].equals("팀장")) selApprovalline[i] = "-";
			    			if(selApprovalline[i].equals("연구소장")) selApprovalline[i] = "-";
			    			if(selApprovalline[i].equals("본부장")) selApprovalline[i] = "서성만";
		    			}
		    		}
	    		}
	    	}
	    	// 제2연구소
	    	else if(empApprovalLine[2].equals("2"))
	    	{
	    		// 차세대기술연구팀
	    		if(empApprovalLine[3].equals("1"))
	    		{
	    			// 팀장의 경우
    				if(empApprovalLine[5].equals("3"))
		    		{	
		    			for(int i = 0; i < selApprovalline.length; i++ )
		    			{
		    				if(selApprovalline[i].equals("파트장")) selApprovalline[i] = "-";
			    			if(selApprovalline[i].equals("팀장")) selApprovalline[i] = "-";
			    			if(selApprovalline[i].equals("연구소장")) selApprovalline[i] = "-";
			    			if(selApprovalline[i].equals("본부장")) selApprovalline[i] = "서성만";
		    			}
		    		} 	    			
	    			// RealBX 파트
	    			if(empApprovalLine[4].equals("1"))
		    		{	
	    				// 일반의 경우
	    				if(empApprovalLine[5].equals("1"))
			    		{	
			    			for(int i = 0; i < selApprovalline.length; i++ )
			    			{
			    				if(selApprovalline[i].equals("파트장")) selApprovalline[i] = "이성후";
				    			if(selApprovalline[i].equals("팀장")) selApprovalline[i] = "김영은";
				    			if(selApprovalline[i].equals("연구소장")) selApprovalline[i] = "-";
				    			if(selApprovalline[i].equals("본부장")) selApprovalline[i] = "서성만";
			    			}
			    		} //if("1" == empApprovalLine[5])
	    				// 파트장의 경우
	    				else if(empApprovalLine[5].equals("2"))
			    		{	
			    			for(int i = 0; i < selApprovalline.length; i++ )
			    			{
			    				if(selApprovalline[i].equals("파트장")) selApprovalline[i] = "-";
				    			if(selApprovalline[i].equals("팀장")) selApprovalline[i] = "김영은";
				    			if(selApprovalline[i].equals("연구소장")) selApprovalline[i] = "-";
				    			if(selApprovalline[i].equals("본부장")) selApprovalline[i] = "서성만";
			    			}
			    		} // else if("2" == empApprovalLine[5])
	    				// 팀장의 경우
	    				else if(empApprovalLine[5].equals("3"))
			    		{	
			    			for(int i = 0; i < selApprovalline.length; i++ )
			    			{
			    				if(selApprovalline[i].equals("파트장")) selApprovalline[i] = "-";
				    			if(selApprovalline[i].equals("팀장")) selApprovalline[i] = "-";
				    			if(selApprovalline[i].equals("연구소장")) selApprovalline[i] = "-";
				    			if(selApprovalline[i].equals("본부장")) selApprovalline[i] = "서성만";
			    			}
			    		} // else if("3" == empApprovalLine[5])
		    		} // if("1" == empApprovalLine[4])
	    		} // if("1" == empApprovalLine[3])
	    	} // else if("2" == empApprovalLine[2])
	    } // if("2" == empApprovalLine[1])

	    ResponseMessage result = null;
	    String temp = "";
	    
	    for(int i = 0; i < selApprovalline.length; i++)
	    {
	    	if(i == (selApprovalline.length-1)) temp += selApprovalline[i];
	    	else temp += selApprovalline[i] + ", ";
	    }
	    
	    result = new ResponseMessage("Success", temp, "", "");
	    
	    logger.info(empName + "님의 " + strApprovallineName + " 결재라인 : " + result.getMessage().toString());
	    
		return new ResponseEntity<ResponseMessage>(result, HttpStatus.OK);
					
	}
	//} // Search
} // class EAMController
