$(function(){
	var interval = 2000;
	var url = $("title").attr("url");
	
	watcherGC.flush(url);
	// 数据频率刷新
	if(watcherGC.intervalId<1){
		watcherGC.intervalId = watcherGC.flushInterval(interval, url);
	}
	
	$(".easyui-accordion").accordion('getSelected').panel('collapse');
	
	window.onbeforeunload= function(event) {
		return "";
	}
})

function Ydata(name, xlen) {
	this.name = name;
	this.type = 'line';
	
	this.smooth = true;
	this.data = new Array(xlen);
};

var watcherGC = {

	intervalId: 0,
	// gc chart
	initChart: function(title, subjects, xdata, yname, ydata){
		var gcChart = echarts.init(document.getElementById(title.text+'Chart'));
		var option = {
				title : title,
				tooltip : {
					trigger: 'axis',
					formatter: ""
				},
				legend: {
					data:subjects
				},
				toolbox: {
					show : true,
					feature : {
						saveAsImage : {show: true, title: "save"}
					}
				},
				xAxis : [
					{
						type : 'category',
						boundaryGap : false,
						data : xdata
					}
				],
				yAxis : {
							name : yname,
						    type : 'value'
						},
				series : ydata
		};

		gcChart.setOption(option);
	},

	// 定时刷新
	flushInterval: function(interval, url){
		var id = setInterval(function () {
			watcherGC.flush(url);
		}, interval);
		return id;
	},
	// 刷新
	flush: function(url){

		// get data
		$.ajax({
			url: basePath + '/process/gchart',
			data: {
				"url": url
			},
			success:function(result){
				if (result.code != 200 ){
					$.messager.alert('错误', result.msg, 'error');
					watcherGC.clean();
					return ;
				}
				if(result.model==''||result.model=='null'){
					return;
				}
				
				var subjects = result.model.subjects; // 类型元素
				var xdata    = result.model.xdata; // x时间元素
				
				var percentYdata = new Array(subjects.length); // 百分比
				var countYdata   = new Array(subjects.length); // 统计次数
				var timeYdata    = new Array(subjects.length); // 回收时间

				for(var i=0; i<percentYdata.length; i++){
					percentYdata[i] = new Ydata(subjects[i], xdata.length);
					
					countYdata[i] 	= new Ydata(subjects[i], xdata.length);
					timeYdata[i] 	= new Ydata(subjects[i], xdata.length);
					
					countYdata[i].smooth = false;
					timeYdata[i].smooth  = false;
				}
				
				for(var i=0; i<subjects.length; i++){
					for(var j=0; j<xdata.length; j++){
						percentYdata[i].data[j] = parseInt(result.model.ydata[i][j].percent*100);
						countYdata[i].data[j]   = result.model.ydata[i][j].collectionCnt;
						timeYdata[i].data[j]    = result.model.ydata[i][j].collectionTime/1000;
					}
				}
				
				// 渲染图表
				// gc chart
				var gc = {
						text: 'GC',
						subtext: ''
					};
				watcherGC.initChart(gc, subjects, xdata, 'GC回收率 (%)', percentYdata);
				var count = {
						text: 'GC-Count',
						subtext: ''
				};
				watcherGC.initChart(count, subjects, xdata, 'GC次数', countYdata);
				var time = {
						text: 'GC-Time',
						subtext: ''
				};
				watcherGC.initChart(time, subjects, xdata, '最近一次GC时间(秒s)',timeYdata);
				
			}
			
		});
	},
	// 清除定时任务
	clean: function(){
		clearInterval(watcherGC.intervalId);
		watcherGC.intervalId = 0;
	}

}
