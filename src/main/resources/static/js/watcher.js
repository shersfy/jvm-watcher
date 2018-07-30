$(function(){
	var interval = 2000;
	var url = $("title").attr("url");
	
	watcher.flush(url);
	// 数据频率刷新
	if(watcher.intervalId<1){
		watcher.intervalId = watcher.flushInterval(interval, url);
	}
	
	$("#close-btn").click(function(){
		var status = $(this).attr("status");
		if("off"==status){
			// close()
			watcher.close(url);
		} else {
			// reopen()
			watcher.reopen(interval, url);
		}
	});
	
	$(".easyui-accordion").accordion('getSelected').panel('collapse');
	
	window.onbeforeunload= function(event) {
		return "";
	}
})

function Ydata(name, xlen) {
	this.name = name;
	this.type = 'line';
	this.smooth = true;
	this.itemStyle = {normal: {areaStyle: {type: 'default'}}};
	this.data = new Array(xlen);
};

var watcher = {

	intervalId: 0,
	// heap memory
	initChart: function(title, subjects, xdata, ydata){
		var heapChart = echarts.init(document.getElementById(title.text+'Chart'));
		var option = {
				title : title,
				tooltip : {
					trigger: 'axis'
				},
				legend: {
					data:subjects
				},
				toolbox: {
					show : true,
					feature : {
						mark : {show: false},
						dataView : {show: false, readOnly: false},
						magicType : {show: false, type: ['line', 'bar', 'stack', 'tiled']},
						restore : {show: false},
						saveAsImage : {show: true, title: "save"}
					}
				},
				calculable : true,
				xAxis : [
					{
						type : 'category',
						boundaryGap : false,
						data : xdata
					}
				],
				yAxis : [
						{
							type : 'value'
						}
				],
				series : ydata
		};

		heapChart.setOption(option);
	},

	// 定时刷新
	flushInterval: function(interval, url){
		var id = setInterval(function () {
			watcher.flush(url);
		}, interval);
		return id;
	},
	// 刷新
	flush: function(url){

		// get data
		$.ajax({
			url: basePath + '/process/data',
			data: {
				"url": url
			},
			success:function(result){
				if (result.code != 200 ){
					$.messager.alert('错误', result.msg, 'error');
					watcher.clean();
					return ;
				}
				if(result.model.length==0){
					return;
				}
				
				var xdata = new Array(result.model.length); // x时间元素
				
				var heapMax = 0;
				var heapSubjects = new Array(result.model[0].heapPools.length); // 类型元素
				var heapYdata = new Array(heapSubjects.length); // 数据元素

				var nonHeapMax = 0;
				var nonHeapSubjects = new Array(result.model[0].nonHeapPools.length); // 类型元素
				var nonHeapYdata = new Array(nonHeapSubjects.length); // 数据元素
				
				var usageSubjects = new Array('init', 'max', 'committed', 'used'); // 类型元素
				
				var edenMax = 0;
				var survMax = 0;
				var tenuMax = 0;
				
				var heapDetailYdata = new Array(usageSubjects.length); // 堆数据
				var edenDetailYdata = new Array(usageSubjects.length); // eden数据
				var survDetailYdata = new Array(usageSubjects.length); // survivor数据
				var tenuDetailYdata = new Array(usageSubjects.length); // old gen数据
				
				for(var i=0; i<heapDetailYdata.length; i++){
					heapDetailYdata[i] = new Ydata(usageSubjects[i], xdata.length);
					edenDetailYdata[i] = new Ydata(usageSubjects[i], xdata.length);
					survDetailYdata[i] = new Ydata(usageSubjects[i], xdata.length);
					tenuDetailYdata[i] = new Ydata(usageSubjects[i], xdata.length);
				}
				
				var nonHeapDetailYdata = new Array(usageSubjects.length); // 数据元素
				var metaspaceYdata = new Array(usageSubjects.length);
				var classpaceYdata = new Array(usageSubjects.length);
				var codecacheYdata = new Array(usageSubjects.length);
				
				for(var i=0; i<nonHeapDetailYdata.length; i++){
					nonHeapDetailYdata[i] = new Ydata(usageSubjects[i], xdata.length);
					metaspaceYdata[i] = new Ydata(usageSubjects[i], xdata.length);
					classpaceYdata[i] = new Ydata(usageSubjects[i], xdata.length);
					codecacheYdata[i] = new Ydata(usageSubjects[i], xdata.length);
				}
				
				for(var i=0; i<xdata.length; i++){
					var date = new Date(result.model[i].createTime);
					xdata[i] = (date.getMinutes()<10?"0":"")+date.getMinutes()+(date.getSeconds()<10?":0":":")+date.getSeconds();
					//heap
					for(var j=0; j<result.model[i].heapPools.length; j++){
						var name = result.model[i].heapPools[j].name;
						heapSubjects[j] = name;
						if(heapYdata[j] == undefined){
							heapYdata[j] = new Ydata(heapSubjects[j], xdata.length);
						}
						var init = parseFloat((result.model[i].heapPools[j].init/1024.0/1024).toFixed(3));
						var max  = parseFloat((result.model[i].heapPools[j].max/1024.0/1024).toFixed(3));
						var committed  = parseFloat((result.model[i].heapPools[j].committed/1024.0/1024).toFixed(3));
						var used = parseFloat((result.model[i].heapPools[j].used/1024.0/1024).toFixed(3));
						
						if(name=="heap"){
							heapYdata[j].data[i] = committed;
							if(heapMax==0){
								heapMax = init>max?init:max;
							}
							// heap-detail
							heapDetailYdata[0].data[i] = init;
							heapDetailYdata[1].data[i] = max;
							heapDetailYdata[2].data[i] = committed;
							heapDetailYdata[3].data[i] = used;
						} else {
							heapYdata[j].data[i] = used;
							// eden
							if(name.indexOf("Eden")>-1){
								if(edenMax==0){
									edenMax = init>max?init:max;
								}
								edenDetailYdata[0].data[i] = init;
								edenDetailYdata[1].data[i] = max;
								edenDetailYdata[2].data[i] = committed;
								edenDetailYdata[3].data[i] = used;
							}
							// survivor
							else if(name.indexOf("Survivor")>-1){
								if(survMax==0){
									survMax = init>max?init:max;
								}
								survDetailYdata[0].data[i] = init;
								survDetailYdata[1].data[i] = max;
								survDetailYdata[2].data[i] = committed;
								survDetailYdata[3].data[i] = used;
							}
							// Tenured Gen
							else if(name.indexOf("Tenured")>-1 || name.indexOf("Old")>-1){
								if(tenuMax==0){
									tenuMax = init>max?init:max;
								}
								tenuDetailYdata[0].data[i] = init;
								tenuDetailYdata[1].data[i] = max;
								tenuDetailYdata[2].data[i] = committed;
								tenuDetailYdata[3].data[i] = used;
							}
							
						}
					}
					// non-heap
					for(var j=0; j<nonHeapSubjects.length; j++){
						var name = result.model[i].nonHeapPools[j].name;
						nonHeapSubjects[j] = name;
						if(nonHeapYdata[j] == undefined){
							nonHeapYdata[j] = new Ydata(nonHeapSubjects[j], xdata.length);
						}
						
						var init = parseFloat((result.model[i].nonHeapPools[j].init/1024.0/1024).toFixed(3));
						var max  = parseFloat((result.model[i].nonHeapPools[j].max/1024.0/1024).toFixed(3));
						var committed  = parseFloat((result.model[i].nonHeapPools[j].committed/1024.0/1024).toFixed(3));
						var used = parseFloat((result.model[i].nonHeapPools[j].used/1024.0/1024).toFixed(3));
						
						if(name=='non-heap'){
							nonHeapYdata[j].data[i] = committed;
							if(nonHeapMax==0){
								nonHeapMax = init>committed?init:committed;
							}
							// non-heap-detail
							nonHeapDetailYdata[0].data[i] = init;
							nonHeapDetailYdata[1].data[i] = max;
							nonHeapDetailYdata[2].data[i] = committed;
							nonHeapDetailYdata[3].data[i] = used;
						} else {
							nonHeapYdata[j].data[i] = used;
							// metaspace
							if(name.indexOf("Meta")>-1){
								metaspaceYdata[0].data[i] = init;
								metaspaceYdata[1].data[i] = max;
								metaspaceYdata[2].data[i] = committed;
								metaspaceYdata[3].data[i] = used;
							}
							// class space
							else if(name.indexOf("Class")>-1){
								classpaceYdata[0].data[i] = init;
								classpaceYdata[1].data[i] = max;
								classpaceYdata[2].data[i] = committed;
								classpaceYdata[3].data[i] = used;
							}
							// code cache
							else if(name.indexOf("Code")>-1){
								codecacheYdata[0].data[i] = init;
								codecacheYdata[1].data[i] = max;
								codecacheYdata[2].data[i] = committed;
								codecacheYdata[3].data[i] = used;
							}
						}
					}
					
				}
				
				// 渲染图表
				// heap
				var heap = {
						text: 'heap',
						subtext: heapMax+" M"
					};
				watcher.initChart(heap, heapSubjects, xdata, heapYdata);
				// heap-detail
				var heapDetail = {
						text: 'heap-detail',
						subtext: heapMax+" M"
				};
				watcher.initChart(heapDetail, usageSubjects, xdata, heapDetailYdata);
				// heap-eden
				var edenDetail = {
						text: 'heap-eden',
						subtext: edenMax+" M"
					};
				watcher.initChart(edenDetail, usageSubjects, xdata, edenDetailYdata);
				// heap-survivor
				var survDetail = {
						text: 'heap-survivor',
						subtext: survMax+" M"
				};
				watcher.initChart(survDetail, usageSubjects, xdata, survDetailYdata);
				// heap-tenured
				var tenuDetail = {
						text: 'heap-tenured',
						subtext: tenuMax+" M"
				};
				watcher.initChart(tenuDetail, usageSubjects, xdata, tenuDetailYdata);
				
				// non-heap
				var nonHeap = {
						text: 'non-heap',
						subtext: nonHeapMax+" M"
				};
				watcher.initChart(nonHeap, nonHeapSubjects, xdata, nonHeapYdata);
				// non-heap-detail
				var nonHeapDetail = {
						text: 'non-heap-detail',
						subtext: nonHeapMax+" M"
				};
				watcher.initChart(nonHeapDetail, usageSubjects, xdata, nonHeapDetailYdata);
				
			}
			
		});
	},
	// 重新打开
	reopen: function(interval, url){
		$.messager.confirm('确认', '确认重新打开连接吗?', function(ok){
			if (ok && watcher.intervalId<1){
				location.reload();
			}
		});
	},
	// 关闭连接
	close: function(url){
		$.messager.confirm('确认', '确认断开连接吗?', function(ok){
			if (ok){
				watcher.clean();
				$.ajax({
					url: basePath + '/process/close',
					data: {
						"url": url
					},
					success:function(result){
						if (result.code != 200 ){
							$.messager.alert('错误', result.msg, 'error');
							watcher.clean();
							return ;
						} else {
							$.messager.alert('提示', "已断开", 'info');
							$("#close-btn").children("span").children("span").text("连接");
							$("#close-btn").attr("status", "on");
						}
					}
				});
			}
		});
	},
	// 清除定时任务
	clean: function(){
		clearInterval(watcher.intervalId);
		watcher.intervalId = 0;
	}

}
