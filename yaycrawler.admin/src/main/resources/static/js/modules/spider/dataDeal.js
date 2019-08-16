$(function () {
	
	// 日期插件的格式化
	laydate.render({
		elem: '#begindate',
		trigger: 'click',
		type: "date", // 组件的类型：year,month,time···
	     done: value => { // 点击确认执行的回调函数，会把当前选择的时间传入进来
	      // 把选择的时间赋值给先前定义好的变量，显示在页面
	      vm.q.begindate = value;
	    } 
	});

	laydate.render({
		elem: '#enddate',
		trigger: 'click',
		type: "date", // 组件的类型：year,month,time···
	    done: value => { // 点击确认执行的回调函数，会把当前选择的时间传入进来
	      	// 把选择的时间赋值给先前定义好的变量，显示在页面
	    	vm.q.enddate = value;
	    } 
	});
	
    $("#jqGrid").jqGrid({
    	url: baseURL + 'newsquery/searchDoubleList',
        datatype: "json",
        postData:{'keyWord':"",'entity':vm.q.entitySelected,'begindate':vm.q.begindate,'enddate':vm.q.enddate},
        colModel: [			
			{ label: '采集源ID', name: '_id', index: "_id", hidden:true, key: true },
	        { label: '标题', name: 'title', width: 180 ,formatter: function(value, options, row){
	        	   return "<a  href='javascript:vm.view(\""+row._id+"\")'>" + row.title + "</a>";
	         }},
	        { label: '发布时间', name: 'pubDate', width: 50},
	        { label: '网站', name: 'crawler_site', width: 40},
	        { label: '采集时间', name: 'crawler_date', index: 'crawler_date', width: 40 ,formatter: function (value, options, row) {
	                 if(!row.crawler_date) return "未知";
	                 var timestamp = row.crawler_date*1;
	                 var date = new Date(timestamp);    // 根据时间戳生成的时间对象
	                 return dateFtt("yyyy-MM-dd hh:mm:ss",date);
	             }},
	        { label: '重复键', name: 'double_key', index: 'double_key', width: 20},
	        { label: '项目', name: 'crawler_project', width: 20},
        ],
		viewrecords: true,
        height: 385,
        rowNum: 30,
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
    
    vm.getEntitys();
    
   /* $('#selector').cron({
    	onChange: function() {
			$('#cron').val($(this).cron("value"));
		}
	}); // apply cron with default options
    
    //初始化项目列表
    vm.getProjectList();
    vm.getWorkerList();*/
    vm.getProjectList();
   
});


var vm = new Vue({
	el:'#rrapp',
	data:{
		q:{
			key: "",
			begindate: "",
			enddate: "",
			entitySelected: "news"
		},
		entitys:{},
		showList:true,
		showEdit:false,
		showView:false,
		projectList:{},
		datainfo: {
            status: 1,
            type: 0,
            domain: '',
            jsonData: ''
        }
	},
	methods: {
		query: function () {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
		        postData:{'keyWord': vm.q.key,'entity':vm.q.entitySelected,'begindate':vm.q.begindate,'enddate':vm.q.enddate},
		        page:page
		    }).trigger("reloadGrid");
		},
		getEntitys: function(){
			$.get(baseURL + "newsquery/select", function(r){
				vm.entitys = r.list;
			});
		},
		reload:function(){
			vm.showList=true;
			vm.showEdit=false;
			vm.showView=false;
			vm.query();
		},
		view:function(id){
			var iddb = {'id':id,'entity':vm.q.entitySelected};
			var str = JSON.stringify(iddb);
			var url = "newsquery/searchByid";
			$.ajax({
				type: "POST",
			    url: baseURL + url,
		        contentType: "application/json",
			    data: str,
			    success: function(info){
			    	$("div#content_main").html("");
			    	if(info.content_tr != null) {
		                //$("div#content_main").append("<div class=\"content_main_con\">"+info.contentTr+"</div>");
		                $("div#content_main").append("<div class=\"con-eng col-md-6\"><h4 style=\"text-align: center;\"><b>" + info.title_tr + "</b></h4>" + info.content_tr + "</div>");
		                $("div#content_main").append("<div class=\"con-chin col-md-6\"><h4 style=\"text-align: center;\"><b>" + info.title + "</b></h4>" + info.content + "</div>");
		            } else {
		                $("div#content_main").append("<div class=\"content_main_title\" >"+info.title+"</div>");
		                $("div#content_main").append("<div class=\"content_main_dis\" >"+info.pubDate+"</div>");
		                $("div#content_main").append("<div class=\"content_main_con\">"+info.content+"</div>");
			    	}
				}
			});
			$("#mymodal").modal('show');
		},
		removeDouble:function(){
			var params = {'begindate':vm.q.begindate,'enddate':vm.q.enddate, 'entity':vm.q.entitySelected};
			var str = JSON.stringify(params);
			var url = "newsquery/removeDouble";
			$.ajax({
				type: "POST",
			    url: baseURL + url,
		        contentType: "application/json",
		        data: str,
			    success: function(info){
			    	if(info === 1) {
			    		alert("执行完成");
			    	} else {
			    		alert("执行失败");
			    	}
				}
			});
		},
		back: function() {
	          vm.reload();
		},
		autoClass: function(status) {
			var iddb = {'id':getSelectedRow(),'entity':vm.q.entitySelected,'status':status};
			var str = JSON.stringify(iddb);
			var url = "newsquery/check";
			$.ajax({
				type: "POST",
			    url: baseURL + url,
		        contentType: "application/json",
			    data: str,
			    success: function(r){
			    	if (r.code === 0) {
                        alert('审核成功', function () {
                            vm.reload();
                        });
                    } else {
                        alert(r.msg);
                    }
				}
			});
		},
		validateBeginDate: function () {
            if (isBlank(vm.q.begindate)) {
                alert("开始时间不能不能为空");
                return true;
            }

        },
        doubleCheck: function () {
        	var ids = getSelectedRows();
        	if(ids) {
				var iddb = {'ids':ids,'entity':vm.q.entitySelected};
				var str = JSON.stringify(iddb);
				var url = "newsquery/multiVerifyNODouble";
				$.ajax({
					type: "POST",
				    url: baseURL + url,
			        contentType: "application/json",
				    data: str,
				    success: function(r){
				    	if (r.code === 0) {
	                        alert('审核成功', function () {
	                            vm.reload();
	                        });
	                    } else {
	                        alert(r.msg);
	                    }
					}
				});
			}else{
				alert("请选中记录");
			}

        },
		getProjectList: function(){
			$.get(baseURL + "spider/spiderproject/select", function(r){
				vm.projectList = r.list;
			});
		}
	}
});