$(function(){
	$('#watcher-rmi').DataTable({
		"info": false,
		"ordering": false,
		"lengthChange": false,
		"paging": false,
		"searching": false
	});
	
	$('.rmi-url').each(function(){
		var url = $(this).attr("url");
		$(this).click(function(){
			$('#url').textbox('setValue', url);
		});
	});
});

function submitForm(){
	var url = $('#url').val();
	if(url == undefined || url.trim().length==0){
		$.messager.alert("提示", "URL不能为空白", "info");
		return;
	}
	$.messager.confirm('确认', '确认创建RMI连接吗?', function(ok){
		if (ok){
			process.watchProcess(url);
		}
	});
}

function clearForm(){
	$('#ff').form('clear');
}