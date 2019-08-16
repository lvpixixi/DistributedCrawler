$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'spiderInfo/list',
        datatype: "json",
        colModel: [			
			{ label: '采集源ID', name: 'id', index: "user_id", hidden:true,width: 45, key: true },
			{ label: '网站域名', name: 'domain', width: 75 },
			{ label: '网站名称', name: 'siteName', width: 90 },			
			{ label: '创建时间', name: 'createTime', index: "create_time", width: 80}
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
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.roleList = {};
			vm.spiderInfo = {status:1,type:0,roleIdList:[]};			
		},
		update: function () {
			var spiderInfoId = getSelectedRow();
			if(spiderInfoId == null){
				return ;
			}
			
			vm.showList = false;
            vm.title = "修改";
			
			vm.getSpiderInfo(spiderInfoId);
			
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