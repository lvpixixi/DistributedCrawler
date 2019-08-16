$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'spider/list',
        datatype: "json",
        colModel: [			
			{ label: '任务ID', name: 'id',  hidden:true,width: 45, key: true },
			{ label: '任务名称', name: 'taskName', width: 75 },
			{ label: '任务开始时间', name: 'startTime', index: "startTime", width: 80},
			{ label: '已抓取数量', name: 'pageCount', width: 90 },		
			{ label: '抓取状态', name: 'status', width: 80, formatter: function(value, options, row){
				return value === 1 ? 
					'<span class="label label-danger">运行中</span>' : 
					'<span class="label label-success">停止</span>';
			}},
			{ label: '编辑模板', name: 'spiderId', width: 80, formatter: function(value, options, row){
				return  '<a class="btn btn-primary">编辑</a>' ;
			}},	
			{ label: '查看数据', name: 'id', width: 80, formatter: function(value, options, row){
				return  '<a class="btn btn-primary">查看数据</a>' ;
			}},	
			{ label: '操作', name: 'id', width: 80, formatter: function(value, options, row){
				return row.status === 1 ? 
				  '<button onclick="vm.stopTask(\''+value+'\')" class="btn btn-info">停止</button>':
				'<button onclick="vm.delTask(\''+value+'\')" class="btn btn-info">删除</button>';
			}}	
			
			
        ],
		viewrecords: true,
        height: 385,
        rowNum: 10,
		//rowList : [10,30,50],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: true,
        //pager: "#jqGridPager",
        jsonReader : {
            root: "page.list",
            //page: "page.currPage",
            //total: "page.totalPage",
            //records: "page.totalCount"
        },
        prmNames : {
            page:"page", 
            rows:"limit", 
            order: "order"
        },
        gridComplete:function(){
        	//隐藏grid底部滚动条
        	$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" }); 
        }
    });
    
    
});

var vm = new Vue({
	el:'#rrapp',
	data:{
		q:{
			username: null
		},
		showList: true,
		title:null,
		roleList:{},
		spiderInfo:{
			status:1,
			type:0,
			roleIdList:[]
		}
	},
	methods: {
		query: function () {
			vm.reload();
		},	
		stopTask:function(taskId) {       
			var url = "spider/stop";
			$.ajax({
				type: "POST",
			    url: baseURL + url,
                contentType: "application/json",
			    data: taskId,
			    success: function(r){
			    	if(r.code === 0){
						alert('操作成功', function(){
							vm.reload();
						});
					}else{
						alert(r.msg);
					}
				}
			});
		},
		delTask: function (taskId) {
			var url = "spider/delTask";
			
			confirm('确定要删除选中的记录？', function(){
				$.ajax({
					type: "POST",
				    url: baseURL + url,
	                contentType: "application/json",
				    data: taskId,
				    success: function(r){
				    	if(r.code === 0){
							alert('操作成功', function(){
								vm.reload();
							});
						}else{
							alert(r.msg);
						}
					}
				});
			});
		},		
		testTask:function () {        
			var url =  "spider/testSpiderInfo";
			$.ajax({
				type: "POST",
			    url: baseURL + url,
                contentType: "application/json",
			    data: JSON.stringify(vm.spiderInfo),
			    success: function(r){
			    	if(r.code === 0){
						alert('操作成功', function(){
							vm.reload();
						});
					}else{
						alert(r.msg);
					}
				}
			});
		},
		runTask:function () {        
			
			var spiderInfoId = getSelectedRow();
			if(spiderInfoId == null){
				return ;
			}
			alert(spiderInfoId)
			var url = "spider/start";
			$.ajax({
				type: "POST",
			    url: baseURL + url,
                contentType: "application/json",
			    data: spiderInfoId,
			    success: function(r){
			    	if(r.code === 0){
						alert('操作成功', function(){
							vm.reload();
						});
					}else{
						alert(r.msg);
					}
				}
			});
		},
		runTimeTask:function () {        
			var url = "spider/runTimeTask";
			$.ajax({
				type: "POST",
			    url: baseURL + url,
                contentType: "application/json",
			    data: JSON.stringify(vm.spiderInfo),
			    success: function(r){
			    	if(r.code === 0){
						alert('操作成功', function(){
							vm.reload();
						});
					}else{
						alert(r.msg);
					}
				}
			});
		},
		getSpiderInfo: function(spiderInfoId){
			$.get(baseURL + "spiderInfo/info/"+spiderInfoId, function(r){
				vm.spiderInfo = r.spiderInfo;
			});
		},
		reload: function () {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                postData:{'siteName': vm.q.siteName},
                page:page
            }).trigger("reloadGrid");
		},
        validator: function () {
            if(isBlank(vm.spiderInfo.siteName)){
                alert("网站名不能为空");
                return true;
            }

            if(isBlank(vm.spiderInfo.domain)){
                alert("域名不能为空");
                return true;
            }
            if(isBlank(vm.spiderInfo.jsonData)){
                alert("爬虫配置信息不能为空");
                return true;
            }

        
        }
	}
});