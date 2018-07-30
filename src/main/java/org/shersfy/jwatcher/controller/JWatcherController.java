package org.shersfy.jwatcher.controller;

import javax.annotation.Resource;

import org.shersfy.jwatcher.beans.Result;
import org.shersfy.jwatcher.service.SystemInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/watch")
public class JWatcherController extends BaseController{
	
	@Resource
	private SystemInfoService systemInfoService;
	
	
	@RequestMapping("/capacity")
	@ResponseBody
	public Result getMemoInfo(){
		Result res = new Result();
		ModelMap model = new ModelMap();
		model.put("memo", systemInfoService.getMemory());
		model.put("cpu", systemInfoService.getCpuInfo());
		res.setModel(model);
		return res;
	}
	
}
