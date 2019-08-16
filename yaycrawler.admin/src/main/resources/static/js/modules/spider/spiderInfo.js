$(function () {
	
    $("#jqGrid").jqGrid({
        url: baseURL + 'spiderInfo/list',
        datatype: "json",
        colModel: [			
			{ label: '采集源ID', name: 'id',  hidden:true,width: 45, key: true },
			{ label: '网站域名', name: 'domain', width: 75 },
			{ label: '请求URL', name: 'url', width: 120 },
			{ label: '网站名称', name: 'siteName', width: 90 },			
			{ label: '更新时间', name: 'createTime', index: "create_time", width: 80}
        ],
		viewrecords: true,
        height: 385,
        rowNum: 10,
		rowList : [10,30,50],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader : {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
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
    
   
    
    $('#selector').cron({
    	onChange: function() {
			$('#cron').val($(this).cron("value"));
		}
	}); // apply cron with default options
    
    //初始化项目列表
    vm.getProjectList();
    vm.getWorkerList();
    vm.getSiteList();
   
});


function crawlerDataGridInit(r){
	var model=[];
	$.each(r.columns, function(index,value) {
		model.push({
			label: value,
			name: value,
			index: value,
			width: 75
		});
	});

	 $("#testGrid").jqGrid({
	    	datatype: "local",
	        colModel: model,
			viewrecords: true,
	        height: 385,
	        rowNum: 10,
			rowList : [10,30,50],
	        rownumbers: true, 
	        rownumWidth: 25, 
	        autowidth:true,
	        multiselect: true,
	        gridComplete:function(){
	        	//隐藏grid底部滚动条
	        	$("#testGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" }); 
	        }
	    });
	 
	for(var i=0;i<=mydata.length;i++){
		$("#testGrid").jqGrid('addRowData',i+1,r.records[i]);
	}
}


var vm = new Vue({
	components: {
	      Multiselect: window.VueMultiselect.default
	},
	el:'#rrapp',
	data:{
	    options: [],
		q:{
			searchword: null,
			projectSelected:0
		},
		showList: true,
		showEdit: false,
		showRunList:false,
		title:null,
		projectList:{},
		workerList:{},
		siteList:[],
		siteMap:{},
		spiderInfo:{
			status:1,
			type:0,
			projectIdList:[],
			workerList:[],
		}
	},
	methods: {
		openUrl:function(url){
			window.open (url)
		},
		templateTest:function(){
			window.open (baseURL + "jsonEditor.html?spiderInfoId="+vm.spiderInfo.id)
		},
		selectAction(action) {
			if(vm.siteMap){
				var obj = vm.siteMap[action];
				//alert(JSON.stringify(obj),);
				vm.spiderInfo.siteId = obj.id;
				vm.spiderInfo.url = obj.url;
				vm.spiderInfo.domain = obj.domain;
			}
	    },
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.showEdit = true;
			vm.showRunList = false;
			vm.title = "新增";
			vm.projectList = {};
			vm.spiderInfo = {status:1,type:0,projectIdList:[]};	
			
			this.getProjectList();
		},
		update: function () {
			var spiderInfoId = getSelectedRow();
			if(spiderInfoId == null){
				return ;
			}
			
			vm.showList = false;
			vm.showEdit = true;
			vm.showRunList = false;
			
            vm.title = "修改";
			vm.getSpiderInfo(spiderInfoId);
			//this.getProjectList();
		},
		del: function () {
			var spiderInfoIds = getSelectedRows();
			if(spiderInfoIds == null){
				return ;
			}
			
			confirm('确定要删除选中的记录？', function(){
				$.ajax({
					type: "POST",
				    url: baseURL + "spiderInfo/delete",
                    contentType: "application/json",
				    data: JSON.stringify(spiderInfoIds),
				    success: function(r){
						if(r.code == 0){
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
		getProjectList: function(){
			$.get(baseURL + "spider/spiderproject/select", function(r){
				vm.projectList = r.list;
			});
		},
		getWorkerList: function(){
			$.get(baseURL + "spider/worker/select", function(r){
				vm.workerList = r.list;
			});
		},
		getSiteList: function(){
			$.get(baseURL + "spider/spidersite/select", function(r){
				vm.siteList = r.list;
				if(vm.siteList){
					$.each(vm.siteList, function(i) {     
					    vm.options.push(vm.siteList[i].siteName);
					    vm.siteMap[vm.siteList[i].siteName] = vm.siteList[i];
					}); 
				}
			});
		},
		saveOrUpdate: function () {
            if(vm.validator()){
                return ;
            }

			var url = vm.spiderInfo.spiderInfoId == null ? "spiderInfo/save" : "spiderInfo/update";
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
		testTask:function () {        
			var spiderInfoIds = getSelectedRows();
			if(spiderInfoIds == null){
				return;
			}
			var url =  "spider/testSpiderInfo";
			startMask("#myModal");
				
			$.ajax({
				type: "POST",
			    url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(spiderInfoIds),
			    success: function(r){
			    	if(r.code === 0){
			    		shutdown("#myModal");
			    		vm.showList = false;
						vm.showEdit = false;
						vm.showRunList = true;
						crawlerDataGridInit(r);
					}else{
						shutdown("#myModal");
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
			
			layer.open({
			      type: 1 //Layer提供了5种层类型。可传入的值有：0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
			      ,title: '选择采集节点'
			      ,area: ['300px', '130px']
			      ,shade: 0.4
			      ,content: $("#selectWorker") //支持获取DOM元素
			      ,btn: ['确定', '关闭'] //按钮
			      ,scrollbar: false //屏蔽浏览器滚动条
			      ,btn1: function(index){
			          //layer.msg('yes');
			          layer.close(index);
			          var url = "task/publishTask";
			          var submitdata = JSON.stringify({spiderInfoId:spiderInfoId,workerId:vm.q.workerSelected});
						$.ajax({
							type: "POST",
						    url: baseURL + url,
			                contentType: "application/json",
						    data: submitdata,
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
			      }
			      ,btn2: function(){
			          layer.closeAll();
			      }
			  }); 
			
			
		},
		runTimeTask:function () {  
			var spiderInfoId = getSelectedRow();
			if(spiderInfoId == null){
				return ;
			}
			layer.open({
			      type: 1 //Layer提供了5种层类型。可传入的值有：0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
			      ,title: '请设置定时调度'
			      ,area: ['390px', '330px']
			      ,shade: 0.4
			      ,content: $("#createScheduler") //支持获取DOM元素
			      ,btn: ['确定', '关闭'] //按钮
			      ,scrollbar: false //屏蔽浏览器滚动条
			      ,btn1: function(index){
			          //layer.msg('yes');
			          layer.close(index);
			          var url = "task/createQuartzJob";
					  var submitdata = JSON.stringify({spiderInfoId:spiderInfoId,cronExp:$("#cron").val()});
						$.ajax({
							type: "POST",
						    url: baseURL + url,
			                contentType: "application/json",
						    data: submitdata,
						    success: function(r){
						    	if(r.code === 0){
									alert('操作成功', function(){
										
									});
								}else{
									alert(r.msg);
								}
							}
						});
			      }
			      ,btn2: function(){
			          //layer.alert('aaa',{title:'msg title'});
			          layer.closeAll();
			      }
			  }); 
			
		},
		getSpiderInfo: function(spiderInfoId){
			$.get(baseURL + "spiderInfo/info/"+spiderInfoId, function(r){
				vm.spiderInfo = r.spiderInfo;
				//alert(r.spiderInfo.jsonData)
				//jsonEditor.set(JSON.parse(r.spiderInfo.jsonData));
				//updateEditor();
				//jsonEditor.set($.parseJSON(r.spiderInfo.jsonData));
			});
		},
		reload: function () {
			vm.showList = true;
			vm.showEdit = false;
			vm.showRunList = false;
			
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                postData:{'searchword': vm.q.searchword,'project':vm.q.projectSelected},
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