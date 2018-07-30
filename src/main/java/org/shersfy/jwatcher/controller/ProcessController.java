package org.shersfy.jwatcher.controller;

import java.io.IOException;

import javax.annotation.Resource;

import org.shersfy.jwatcher.beans.Result;
import org.shersfy.jwatcher.connector.JVMConnector;
import org.shersfy.jwatcher.service.SystemInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/process")
public class ProcessController extends BaseController {
	
	@Resource
	private SystemInfoService systemInfoService;
	
	@RequestMapping("/open")
	@ResponseBody
	public ModelAndView openRemoteConnector(String url){

		ModelAndView mv = new ModelAndView("watcher");
		try {
			JVMConnector connector = systemInfoService.getConnector(url);
			systemInfoService.startWatcher(connector);
			mv.addObject("url", url);
			mv.addObject("os", systemInfoService.getServerOS(connector));
			mv.addObject("jvm", systemInfoService.getServerJVM(connector));
			mv.addObject("threads", systemInfoService.getServerThreads(connector, false));
			mv.addObject("gcnames", systemInfoService.getServerGCNames(connector));
		} catch (IOException e) {
			LOGGER.error(url, e);
			mv.setViewName("redirect:/error");
			mv.addObject("status", FAIL);
			mv.addObject("error", e.getMessage());
			mv.addObject("message", e.getMessage());
		}
		return mv;
	}
	
	@RequestMapping("/close")
	@ResponseBody
	public Result closeRemoteConnector(String url){
		Result res = new Result();
		try {
			JVMConnector connector = systemInfoService.getConnector(url);
			systemInfoService.stopWatcher(connector);
			res.setModel(url);
		} catch (IOException e) {
			LOGGER.error(url, e);
			res.setCode(FAIL);
			res.setMsg(e.getMessage());
		}
		return res;
	}
	
	@RequestMapping("/data")
	@ResponseBody
	public Result getLocalData(String url){
		
		Result res = new Result();
		try {
			res.setModel(systemInfoService.getData(url));
		} catch (IOException e) {
			LOGGER.error(url, e);
			res.setCode(FAIL);
			res.setMsg(e.getMessage());
		}
		return res;
	}
	
	@RequestMapping("/gc")
	@ResponseBody
	public ModelAndView getGCDetail(String url){

		ModelAndView mv = new ModelAndView("watcher_gc");
		try {
			JVMConnector connector = systemInfoService.getConnector(url);
			mv.addObject("url", url);
			mv.addObject("jvm", systemInfoService.getServerJVM(connector));
			mv.addObject("gcnames", systemInfoService.getServerGCNames(connector));
		} catch (IOException e) {
			LOGGER.error(url, e);
			mv.setViewName("redirect:/error");
			mv.addObject("status", FAIL);
			mv.addObject("error", e.getMessage());
			mv.addObject("message", e.getMessage());
		}
		return mv;
	}
	
	@RequestMapping("/gchart")
	@ResponseBody
	public Result getGcChart(String url){
		
		Result res = new Result();
		try {
			res.setModel(systemInfoService.getGcChart(url));
		} catch (IOException e) {
			LOGGER.error(url, e);
			res.setCode(FAIL);
			res.setMsg(e.getMessage());
		}
		return res;
	}
}
