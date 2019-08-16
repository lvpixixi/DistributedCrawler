$(function () {
	
	$('#selector').cron({
	    	onChange: function() {
				$('#cron').val($(this).cron("value"));
			}
	}); // apply cron with default options
	 
    //初始化项目列表
    vm.getSpiderInfoList();
   
});

var vm = new Vue({
	el:'#rrapp',
	data:{
		showList: true,
		title:null,
		spiderInfoList:{},
		task:{
			spiderInfoId:0,
			paramJson:"",
			taskType:1,
			cron:"",
			spiderInfoIdList:[]
		}
	},
	methods: {
		getSpiderInfoList:function(){
			$.get(baseURL + "spiderInfo/select?project="+vm.task.projectId, function(r){
				vm.spiderInfoList = r.list;
			});
		},
		reset:function () {      
			vm.task = {
					spiderInfoId:null,
					paramJson:"",
					taskType:1,
					cron:""
					}
		},
		submit:function () {     
			if(vm.validator()){
	                return ;
	        }
			alert("spiderInfoId"+vm.task.spiderInfoId)
			$.ajax({
				type: "POST",
			    url: baseURL + "task/publishTask",
                contentType: "application/json",
			    data: {"spiderInfoId": vm.task.spiderInfoId,"paramJson":vm.task.paramJson,"jobType":vm.task.jobType},
			    success: function(r){
			    	if(r.code === 0){
						alert('操作成功', function(){
							vm.reset();
						});
					}else{
						alert(r.msg);
					}
				}
			});
		},
		showLayer:function () {  
			layer.open({
			      type: 1 //Layer提供了5种层类型。可传入的值有：0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
			      ,title: '请设置定时调度'
			      ,area: ['390px', '130px']
			      ,shade: 0.4
			      ,content: $("#createScheduler") //支持获取DOM元素
			      ,btn: ['确定', '关闭'] //按钮
			      ,scrollbar: false //屏蔽浏览器滚动条
			      ,btn1: function(index){
			          //layer.msg('yes');
			          layer.close(index);
			          vm.task.cron = $("#cron").val();
			      }
			      ,btn2: function(){
			          //layer.alert('aaa',{title:'msg title'});
			          layer.closeAll();
			      }
			  }); 
			
		},
        validator: function () {
            if(isBlank(vm.task.spiderInfoId)){
                alert("网站名不能为空");
                return true;
            }
            if(vm.task.taskType==0&&isBlank(vm.task.cron)){
                alert("Cron表达式不能为空");
                return true;
            }
            
        }
	}
});