package com.crawlermanage.controller.api.zjsfgkw;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.crawler.domain.json.Result;
import com.crawler.zjsfgkw.domain.json.CreditJson;
import com.crawlermanage.service.zjsfgkw.ZjsfgkwCreditService;
import com.google.gson.Gson;


@Controller
@RequestMapping("/api/zjsfgkw")
public class ZjsfgkwCreditController {

	private static final Logger log = LoggerFactory.getLogger(ZjsfgkwCreditController.class);
	
	@Autowired
	private ZjsfgkwCreditService zjsfgkwCreditService;
	
	
	
	/**
	   * 个人未履行生效裁判信息
	   * @param txtReallyName 姓名,@param  txtCredentialsNumber 证件号,@param txtAH 案号,@param  drpZXFY 执行法院,@param txtStartLARQ 立案日期起止,@param  txtEndLARQ 立案日期结束
	   */
	@RequestMapping(value = "/credit")
	@ResponseBody
    public String execute(@RequestParam("txtReallyName") String txtReallyName,@RequestParam("txtCredentialsNumber") String txtCredentialsNumber,
			@RequestParam("txtAH") String txtAH,@RequestParam("drpZXFY") String drpZXFY,@RequestParam("txtStartLARQ") String txtStartLARQ,
			@RequestParam("txtEndLARQ") String txtEndLARQ, boolean isDebug, String logback){
		
		String creditJson=null;
		 try {
			  
			  log.info("个人未履行生效裁判信息txtReallyName:{}",URLDecoder.decode(txtReallyName,"utf-8"));

			  String url =  "http://www.zjsfgkw.cn/Execute/CreditPersonal?txtReallyName="
			  +URLDecoder.decode(txtReallyName,"utf-8")+"&txtCredentialsNumber="+URLDecoder.decode(txtCredentialsNumber,"utf-8")
					  +"&txtAH="+URLDecoder.decode(txtAH,"utf-8")+"&drpZXFY="+URLDecoder.decode(drpZXFY,"utf-8")
					  +"&txtStartLARQ="+URLDecoder.decode(txtStartLARQ,"utf-8")+"&txtEndLARQ="+URLDecoder.decode(txtEndLARQ,"utf-8"); 
			  log.info("个人未履行生效裁判信息url:{}",url);
			  
			  Result<List<CreditJson>> result = zjsfgkwCreditService.searchCredit(url,isDebug,logback);
	          
	          Gson gson = new Gson();  		
		     creditJson = gson.toJson(result);
		      log.info("个人未履行生效裁判信息搜索结果:{}", creditJson);	
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return creditJson;
		
	}
}
