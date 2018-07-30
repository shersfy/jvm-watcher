$(function() {
	header.init();
})

var header = {
	intervalId: 0, //定时任务数
	init: function(){
		$('#f-nav').tooltip({
		default: $("body").attr("index"),       // 默认为空  --  选中默认值
		width: '200',     // 限制宽度
		//height: '30',
		textList: [],   // 每个导航的内容
		type: 'slideMove',  // 必填, 选择内容
		success: function(ret){
		}   //初始化回调
		});

		$('#f-nav li').each(function(){
			$(this).click(function(){
				header.clean();
				var href = $(this).attr("href");
				if(href==undefined){
					return;
				}
				header.setContent(href);
			});
		});
	}, 
	// 设置模板内容
	setContent: function(url){
		$.ajax({
			url: url,
			success:function(data){
				$("#content").html(data);
			}
		});
	},
	// 清除定时任务
	clean: function(){
		clearInterval(header.intervalId);
		header.intervalId = 0;
	}
}