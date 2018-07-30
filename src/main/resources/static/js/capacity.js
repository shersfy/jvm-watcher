$(function(){
	var initMemo = JSON.parse($('#memoChart').attr('memo'));
	var initCpu  = JSON.parse($('#cpuChart').attr('cpu'));
	
	capacity.initMemoChart(initMemo);
	capacity.initCPUChart(initCpu);
	
	// 数据频率刷新
	if(header.intervalId<1){
		header.intervalId = capacity.flush(2000);
	}
})

var capacity = {
		// memory
		initMemoChart: function(model){
			var memoChart = echarts.init(document.getElementById('memoChart'));
			var option = {
					title: {
						text: '物理内存',
						subtext: model.totalStr
					},
					tooltip : {
						formatter: "{b} "+model.totalStr+"<br/>{a} : {c}%"
					},
					toolbox: {
						feature: {
							saveAsImage: {
								show: false,
								type: 'jpg'
							}
						},
						top : 'bottom',
						itemGap: 20
					},
					series: [
						{
							name: '已使用 '+model.usedStr,
							type: 'gauge',
							detail: {formatter:'{value}%'},
							data: [{value: parseFloat(model.usedPercent*100).toFixed(2), name: '物理内存'}]
						}
						]
			};

			memoChart.setOption(option);
		},
		// CPU
		initCPUChart:function(model){
			var elements = new Array('nice', 'user', 'system', 'wait', 'idle');
			var cpuChart = echarts.init(document.getElementById('cpuChart'));
			var option   = {
					title : {
						text: 'CPU使用率',
						subtext: model.name
					},
					tooltip : {
						trigger: 'item',
						//formatter: "{a} <br/>{b} : {c} ({d}%)"
						formatter: "{a} <br/>{b}: {d}%"
					},
					legend: {
						orient: 'vertical',
						left: 'right',
						data: elements
					},
					series : [
						{
							name: '使用率',
							type: 'pie',
							radius : '55%',
							center: ['50%', '50%'],
							data:[
								{value:model.nice.toFixed(2), name: elements[0]},
								{value:model.user.toFixed(2), name: elements[1]},
								{value:model.system.toFixed(2), name: elements[2]},
								{value:model.wait.toFixed(2), name: elements[3]},
								{value:model.idle.toFixed(2), name: elements[4]}
								],
								itemStyle: {
									emphasis: {
										shadowBlur: 10,
										shadowOffsetX: 0,
										shadowColor: 'rgba(0, 0, 0, 0.5)'
									}
								}
						}
						]
			};

			cpuChart.setOption(option);
		},
		// 定时刷新
		flush: function(interval){
			var id = setInterval(function () {
				// capacity
				$.ajax({
					url: basePath + '/watch/capacity',
					success:function(data){
						if (data.code != 200 ){
							alert("请求错误: "+url);
							return ;
						}
						capacity.initMemoChart(data.model.memo);
						capacity.initCPUChart(data.model.cpu);
					}
				});
				// cpu
//				$.ajax({
//					url: basePath + '/watch/cpu',
//					success:function(data){
//						if (data.code != 200 ){
//							alert("请求错误: "+url);
//							return ;
//						}
//						capacity.initCPUChart(data.model);
//					}
//				});
			}, interval);
			return id;
		}

}
