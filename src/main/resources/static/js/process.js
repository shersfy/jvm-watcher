$(document).ready( function () {
	$('#process').DataTable({
		"info": false,
		"ordering": false,
		"lengthChange": false,
		"paging": false
	});
	process.initProcess();

} );

var process = {
		initProcess: function(){
			$('.jvm-process').each(function(){
				$(this).click(function(){
					var url = "localhost/"+$(this).text();
					$.messager.confirm('确认', '确认创建连接吗?', function(ok){
						if (ok){
							process.watchProcess(url);
						}
					});
				});
			});
		},
		watchProcess: function(url){
			window.open(basePath + "/process/open?url="+url);
		}
}
