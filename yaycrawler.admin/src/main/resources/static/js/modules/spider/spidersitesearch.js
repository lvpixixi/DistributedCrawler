$(function () {
	
	 $("#siteGrid").jqGrid({
	        url: baseURL + 'spider/spidersitesresult/list',
	        datatype: "json",
	        colModel: [			
				{ label: 'id', name: 'id', index: 'id', hidden:true, width: 50, key: true },
				{ label: '域名', name: 'domain', index: 'domain', width: 80 }, 		
				{ label: '命中数', name: 'hits', index: 'hits', width: 30 }, 		
				{ label: '网址', name: 'url', index: 'url', width: 120 ,formatter: function(value, options, row){
		        	   return "<a  href='javascript:vm.openUrl(\""+row.url+"\")'>" + row.url + "</a>";
		        }},
				{ label: '标题', name: 'title', index: 'title', width: 80 }, 			
				{ label: '关键字', name: 'keyWord', index: 'keyWord', width: 80 }, 		
				{ label: '摘要', name: 'summary', index: 'summary', width: 80 }		
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
	        pager: "#siteGridPager",
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
	        	$("#siteGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" }); 
	        }
	    });
	 
    $("#jqGrid").jqGrid({
        url: baseURL + 'spider/spidersitesearch/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id',hidden:true, width: 50, key: true },
			{ label: '搜索名称', name: 'searchName', index: 'search_name', width: 80 }, 			
			{ label: '关键字集合', name: 'keyWord', index: 'key_word', width: 80 }, 			
			{ label: '创建时间', name: 'searchTime', index: 'search_time', width: 80 }, 			
			{ label: '搜索引擎', name: 'useEngine', index: 'use_engine', width: 80 },
			{ label: '操作', name: 'id', index: 'id', width: 80 , formatter: function(value, options, row){
	        	   return "<a  href='javascript:vm.executeSearchById(\""+row.id+"\")'>执行</a>";
	        }}	
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
});

var vm = new Vue({
	el:'#rrapp',
	data:{
		showList: true,
		siteList: false,
		title: null,
		spiderSiteSearch: {}
	},
	methods: {
		openUrl:function(url){
			window.open (url)
		},
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.siteList = false;			
			vm.title = "新增";
			vm.spiderSiteSearch = {};
			
		},
		update: function (event) {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			vm.showList = false;
			vm.siteList = true;
            vm.title = "修改";
            
            vm.getInfo(id);
            vm.loadSite(id);
           
        
		},
		saveOrUpdate: function (event) {
			var url = vm.spiderSiteSearch.id == null ? "spider/spidersitesearch/save" : "spider/spidersitesearch/update";
			$.ajax({
				type: "POST",
			    url: baseURL + url,
                contentType: "application/json",
			    data: JSON.stringify(vm.spiderSiteSearch),
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
		executeSearch: function (event) {
			
			var url = "spider/spidersitesearch/executeSearch"
			$.ajax({
				type: "POST",
			    url: baseURL + url,
                contentType: "application/json",
			    data: JSON.stringify(vm.spiderSiteSearch),
			    success: function(r){
			    	if(r.code === 0){
						alert('操作成功', function(index){
							 vm.loadSite(r.id);
							 vm.siteList = true;
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
				    url: baseURL + "spider/spidersitesearch/delete",
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
		executeSearchById: function (id) {
			$.ajax({
				type: "POST",
			    url: baseURL + "spider/spidersitesearch/executeSearchById/"+id,
                contentType: "application/json",
			    success: function(r){
					if(r.code == 0){
						alert('操作成功', function(index){
							vm.showList = false;
							vm.siteList = true;
				            vm.getInfo(r.id);
				            vm.loadSite(r.id);
						});
					}else{
						alert(r.msg);
					}
				}
			});
		},
		getInfo: function(id){
			$.get(baseURL + "spider/spidersitesearch/info/"+id, function(r){
                vm.spiderSiteSearch = r.spiderSiteSearch;
            });
		},
		loadSite: function (id) {
			
			var page = $("#siteGrid").jqGrid('getGridParam','page');
			$("#siteGrid").jqGrid('setGridParam',{ 
				postData:{'id': id},
                page:page
            }).trigger("reloadGrid");
			
		},
		reload: function (event) {
			vm.showList = true;
			vm.siteList = false;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page
            }).trigger("reloadGrid");
		}
	}
});