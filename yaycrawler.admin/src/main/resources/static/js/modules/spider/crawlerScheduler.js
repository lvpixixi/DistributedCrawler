$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'spider/listScheduler',
        datatype: "json",
        colModel: [			
			{ label: 'ID', name: 'spiderInfoId',  hidden:true,width: 45, key: true },
			{ label: '网站名称', name: 'siteName', width: 75 },
			{ label: '上次执行时间', name: 'previousFireTime',  width: 80},
			{ label: '下次执行时间', name: 'nextFireTime', width: 90 },
			{ label: '开始时间', name: 'startTime', width: 90 },
			{ label: '操作', name: 'spiderInfoId', width: 80, formatter: function(value, options, row){
				return '<button onclick="vm.removeScheduler(\''+value+'\')" class="btn btn-info">删除</button>';
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
		removeScheduler:function(taskId) {       
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
		reload: function () {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                postData:{'siteName': vm.q.siteName},
                page:page
            }).trigger("reloadGrid");
		}
	}
});