package org.shersfy.jwatcher.controller;

import javax.annotation.Resource;

import org.shersfy.jwatcher.service.SystemInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PageController extends BaseController {
	
	@Resource
	private SystemInfoService systemInfoService;
	
	@RequestMapping("/")
	@ResponseBody
	public ModelAndView index(){
		ModelAndView mv = new ModelAndView("index");
		return mv;
	}
	
	@RequestMapping("/system")
	@ResponseBody
	public ModelAndView system(){
		ModelAndView mv = new ModelAndView("system");
		mv.addObject("system", systemInfoService.getSystemInfo());
		return mv;
	}
	
	@RequestMapping("/jvm")
	@ResponseBody
	public ModelAndView jvm(){
		ModelAndView mv = new ModelAndView("jvm");
		mv.addObject("jvm", systemInfoService.getJvmInfo());
		return mv;
	}
	
	@RequestMapping("/process")
	@ResponseBody
	public ModelAndView process(){
		ModelAndView mv = new ModelAndView("process");
		mv.addObject("processes", systemInfoService.getLocalJvmProcesses());
		return mv;
	}
	
	@RequestMapping("/capacity")
	@ResponseBody
	public ModelAndView capacity(){
		ModelAndView mv = new ModelAndView("capacity");
		mv.addObject("memo", systemInfoService.getMemory());
		mv.addObject("cpu", systemInfoService.getCpuInfo());
		return mv;
	}
	@RequestMapping("/remote")
	@ResponseBody
	public ModelAndView config(){
		ModelAndView mv = new ModelAndView("watcher_rmi");
		mv.addObject("connectors", systemInfoService.getRemoteConnectors());
		return mv;
	}
}
