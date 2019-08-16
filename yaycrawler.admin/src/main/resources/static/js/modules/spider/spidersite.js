$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'spider/spidersite/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', hidden:true, width: 50, key: true },
			{ label: '网址', name: 'url', index: 'url', width: 120 ,formatter: function(value, options, row){
	        	   return "<a  href='javascript:vm.openUrl(\""+row.url+"\")'>" + row.url + "</a>";
	        }},
			{ label: '网站名称', name: 'siteName', index: 'site_name', width: 80 }, 			
			{ label: '域名', name: 'domain', index: 'domain', width: 80 }, 			
			{ label: '状态', name: 'status', index: 'status', width: 25,formatter: "select", editoptions:{value:"0:失效;1:有效;2:代理有效"}},
			/*{ label: '重试次数', name: 'cycleRetryTimes', index: 'cycle_retry_times', width: 80 }, 			
			{ label: '', name: 'defaultCookies', index: 'default_cookies', width: 80 }, 			
			{ label: '', name: 'headers', index: 'headers', width: 80 }, 			
			{ label: '', name: 'retryTimes', index: 'retry_times', width: 80 }, 			
			{ label: '', name: 'sleepTime', index: 'sleep_time', width: 80 }, 			
			{ label: '', name: 'timeOut', index: 'time_out', width: 80 }, 			*/
			{ label: '状态码', name: 'rspCode', index: 'rsp_code', width: 40 },
			{ label: '备注', name: 'userAgent', index: 'user_agent', width: 80 }		
			//{ label: '', name: 'loginJsFileName', index: 'login_js_file_name', width: 80 }, 			
			//{ label: '', name: 'loginJudgeExpression', index: 'login_judge_expression', width: 80 }			
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
    
    //初始化项目列表
    vm.getProjectList();
});

var vm = new Vue({
	el:'#rrapp',
	data:{
		q:{
			searchword: null,
			projectSelected:0
		},
		showList: true,
		title: null,
		projectList:{},
		spiderSite: {
			projectIdList:[]
		}
	},
	methods: {
		openUrl:function(url){
			window.open (url)
		},
		checkUrl:function(){
			var ids = getSelectedRows();
			if(ids == null){
				confirm('确定要检查所有Url？', function(){
					$.ajax({
						type: "POST",
					    url: baseURL + "spider/spidersite/checkUrls",
	                    contentType: "application/json",
	                    data: "[]",
					    success: function(r){
							if(r.code == 0){
								alert('操作成功', function(index){
									$("#jqGrid").trigger("reloadGrid");
								});
							}else{
								alert(r.msg);
							}
						}
					});
				});
			}else{
				
				$.ajax({
					type: "POST",
				    url: baseURL + "spider/spidersite/checkUrls",
                    contentType: "application/json",
				    data: JSON.stringify(ids),
				    success: function(r){
						if(r.code == 0){
							alert('操作成功', function(index){
								$("#jqGrid").trigger("reloadGrid");
							});
						}else{
							alert(r.msg);
						}
					}
				});
			}
		},
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.spiderSite = {projectIdList:[]};
		},
		update: function (event) {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(id);
		},
		saveOrUpdate: function (event) {
			var url = vm.spiderSite.id == null ? "spider/spidersite/save" : "spider/spidersite/update";
			$.ajax({
				type: "POST",
			    url: baseURL + url,
                contentType: "application/json",
			    data: JSON.stringify(vm.spiderSite),
			    success: function(r){
			    	if(r.code === 0){
						alert('操作成功', function(index){
							vm.reload();
						});
					}else{
						alert(r.msg);
					}
				}
			});
		},
		del: function (event) {
			var ids = getSelectedRows();
			if(ids == null){
				return ;
			}
			
			confirm('确定要删除选中的记录？', function(){
				$.ajax({
					type: "POST",
				    url: baseURL + "spider/spidersite/delete",
                    contentType: "application/json",
				    data: JSON.stringify(ids),
				    success: function(r){
						if(r.code == 0){
							alert('操作成功', function(index){
								$("#jqGrid").trigger("reloadGrid");
							});
						}else{
							alert(r.msg);
						}
					}
				});
			});
		},
		getInfo: function(id){
			$.get(baseURL + "spider/spidersite/info/"+id, function(r){
                vm.spiderSite = r.spiderSite;
                if(!vm.spiderSite.projectIdList){
                	vm.spiderSite.projectIdList=[];
                }
            });
		},
		getProjectList: function(){
			$.get(baseURL + "spider/spiderproject/select", function(r){
				vm.projectList = r.list;
			});
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
				postData:{'searchword': vm.q.searchword,'project':vm.q.projectSelected},
                page:page
            }).trigger("reloadGrid");
		},
		validator: function () {
            if(isBlank(vm.spiderSite.siteName)){
                alert("网站名不能为空");
                return true;
            }
            if(isBlank(vm.spiderSite.url)){
                alert("网址");
                return true;
            }

            if(isBlank(vm.spiderSite.domain)){
                alert("域名不能为空");
                return true;
            }            
        }
	}
});