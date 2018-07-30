package org.shersfy.jwatcher.timer;

import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.shersfy.jwatcher.connector.JVMConnector;
import org.shersfy.jwatcher.service.SystemInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 定时清除JMX连接超时的对象
 * @date 2018-05-03
 *
 */
@Component("timerJMXConnectTimeout")
public class TimerJMXConnectTimeout {

	private static final Logger LOGGER 	= LoggerFactory.getLogger(TimerJMXConnectTimeout.class);

	// minutes
	@Value("${jmx.connect.timeout.minutes}")
	private int timeout;
	@Value("${jmx.connect.timeout.period}")
	// minutes
	private int period;
	
	
	@PostConstruct
	public void timer(){

		if(timeout <=0){
			return;
		}

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				cleanCache();
			}
		};

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(task, 10000, timeout*60*1000);
		LOGGER.info("{} scheduled", TimerJMXConnectTimeout.class.getName());
	}

	private void cleanCache(){
		LOGGER.debug("JMX connect timeout timer start...");
		long out = timeout * 60 * 1000;
		long now = System.currentTimeMillis();
		if(SystemInfoService.conf!=null){
			Map<String, JVMConnector> cache = SystemInfoService.conf.getCache();
			Set<String> keys = cache.keySet();
			keys.forEach(key->{
				JVMConnector value = cache.get(key);
				if(now-value.getUpdatetime()>out){
					value.stopWatcher();
					LOGGER.info("{} {} connect timeout", value.getClass().getSimpleName(), key);
				}
			});
		}
		LOGGER.debug("JMX connect timeout timer finished.");
	}

}
