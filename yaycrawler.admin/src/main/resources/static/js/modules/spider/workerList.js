$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'spider/worker/list',
        datatype: "json",
        colModel: [			
			{ label: '节点ID', name: 'workerId', index: 'workerId', width: 80 }, 
			{ label: '通信地址', name: 'workerContextPath', index: 'workerContextPath', width: 80 }, 	
			{ label: '心跳间隔', name: 'heartbeatInteval', index: 'heartbeatInteval', width: 80 }, 			
			{ label: '最后心跳时间', name: 'lastHeartbeatTime', index: 'lastHeartbeatTime', width: 80 ,formatter: function (value, options, row) {
                if(!value) return "未知";
                var timestamp = value;
                var d = new Date(timestamp);    //根据时间戳生成的时间对象
                var date = (d.getFullYear()) + "-" +
                        (d.getMonth() + 1) + "-" +
                        (d.getDate()) + " " +
                        (d.getHours()) + ":" +
                        (d.getMinutes()) + ":" +
                        (d.getSeconds()<10?"0"+d.getSeconds():d.getSeconds());
                return date;
            }},
			{ label: '剩余任务数', name: 'waitTaskCount', index: 'waitTaskCount', width: 80 }
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
});

var vm = new Vue({
	el:'#rrapp',
	data:{
		q:{
			ip: null
		},
		showList: true
	},
	methods: {
		query: function () {
			vm.reload();
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
				postData:{'ip': vm.q.ip},
                page:page
            }).trigger("reloadGrid");
		}
	}
});