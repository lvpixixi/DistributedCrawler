$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'task/list?name=fail',
        datatype: "json",
        colModel: [			
        	{ label: '节点', name: 'worker', index: 'worker', width: 80 }, 
        	{ label: '网站名称', name: 'domain', index: 'domain', width: 80 }, 			
 			{ label: '请求URL', name: 'url', index: 'url', width: 80 }, 			
 			{ label: '请求参数', name: 'data', index: 'data', width: 80 },
 			{ label: '加入时间', name: 'extendMap', index: 'extendMap', width: 80 ,formatter: function (value, options, row) {
                if(!row.extendMap) return "未知";
                var timestamp = row.extendMap.startTime;
                var d = new Date(timestamp);    //根据时间戳生成的时间对象
                var date = (d.getFullYear()) + "-" +
                        (d.getMonth() + 1) + "-" +
                        (d.getDate()) + " " +
                        (d.getHours()) + ":" +
                        (d.getMinutes()) + ":" +
                        (d.getSeconds());
                return date;
            }},
            { label: '结束时间', name: 'endTime', index: 'endTime', width: 80 },
 			{ label: '任务标志', name: 'hashCode', index: 'hashCode', width: 80 }
        ],
		viewrecords: true,
        height: 385,
        rowNum: 50,
		rowList : [10,30,50],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader : {
            root: "page.rows",
            page: "page.currPage",
            total: "page.totalPages",
            records: "page.total"
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
                page:page
            }).trigger("reloadGrid");
		}
	}
});