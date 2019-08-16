var entityNamecode;

$(function () {
	vm.getEntitys();
	vm.getProject();
	$("#jqGrid").jqGrid({
		url: baseURL + 'dataexport/searchByQuery',
		datatype: "json",
		postData:{'keyWord':"",'entity':vm.q.entitySelected,'begindate':vm.q.begindate,'enddate':vm.q.enddate},
		colModel: [			
	           { label: '采集源ID', name: '_id', index: "_id", hidden:true,width: 45, key: true },
	           { label: '标题', name: 'title', width: 200 ,formatter: function(value, options, row){
	        	   return "<a  href='javascript:vm.querybyid(\""+row._id+"\")'>" + row.title + "</a>";
	           }},
	           { label: '发布时间', name: 'pubDate', width: 50},
	           { label: '发布网站', name: 'crawler_site', width: 25},
	           { label: '所属项目', name: 'crawler_project', width: 25},
	           { label: '审核状态', name: 'status', index: 'status', width: 25,formatter: "select", editoptions:{value:"0:未通过;1:通过"}},
	           { label: '采集时间', name: 'crawler_date', index: 'crawler_date', width: 40 ,formatter: function (value, options, row) {
	                 if(!row.crawler_date) return "未知";
	                 var timestamp = row.crawler_date*1;
	                 var date = new Date(timestamp);    // 根据时间戳生成的时间对象
	                 return dateFtt("yyyy-MM-dd hh:mm:ss",date);
	             }}
	           ],
	           viewrecords: true,
	           height: 385,
	           rowNum: 30,
	           rowList : [10,30,50],
	           rownumbers: true, 
	           rownumWidth: 25, 
	           autowidth:true,
	           multiselect: false,
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
	        	   // 隐藏grid底部滚动条
	        	   $("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" }); 
	           },

	});
	
});
function queryGrid(){
	
	vm.showList = true;
	var page = $("#jqGrid").jqGrid('getGridParam','page');
	$("#jqGrid").jqGrid('setGridParam',{ 
        postData:{'keyWord': vm.q.key,'entity':vm.q.entitySelected,'begindate':vm.q.begindate,'enddate':vm.q.enddate},
        page:page
    }).trigger("reloadGrid");
};

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
		project:{},
		showList:true,
		info: {
            status: 1,
            type: 0,
            domain: '',
            roleIdList: [],
            jsonData: ''
        },
	},
	methods: {
		query: function () {
			queryGrid();
		},
		getEntitys: function(){
			$.get(baseURL + "dataexport/select", function(r){
				vm.entitys = r.list;
			});
		},
		getProject: function() {
			var params = {'entity':vm.q.entitySelected};
			var str = JSON.stringify(params);
			var url = "dataexport/getProject";
			$.ajax({
				type: "POST",
			    url: baseURL + url,
		        contentType: "application/json",
			    data: str,
			    success: function(r){
			    	vm.project = r.project;
				}
			});
		},
		reload:function(){
			queryGrid();
			
		},
		down2local:function(){
			if(vm.validateDate()) {
				return;
			}
			
			var params = {'entity':vm.q.entitySelected, 'project':vm.project,'begindate':vm.q.begindate,'enddate':vm.q.enddate};
			var str = JSON.stringify(params);
			var url = "dataexport/down2local";
			$.ajax({
				type: "POST",
			    url: baseURL + url,
		        contentType: "application/json",
			    data: str,
			    success: function(info){
			    	if(info.code === 0) {
			    		alert("打包成功!!!");
			    	} else {
			    		alert("打包失败,请联系管理员...");
			    	}
				}
			});
		},
		validateDate: function () {
            if (isBlank(vm.q.begindate)) {
                alert("开始时间不能为空!");
                return true;
            }
            if (isBlank(vm.q.enddate)) {
                alert("结束时间不能为空!");
                return true;
            }
            if(vm.q.enddate < vm.q.begindate) {
            	alert("结束时间不能小于开始时间!");
            	return true;
            }

        }
	}
});