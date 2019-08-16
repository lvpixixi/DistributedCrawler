$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'spider/spiderproject/list',
        datatype: "json",
        colModel: [			
			{ label: '项目id', name: 'id', index: 'id',hidden:true, width: 50, key: true },
			{ label: '项目名称', name: 'name', index: 'name', width: 80 }, 			
			{ label: '项目代码', name: 'code', index: 'code', width: 80 }, 			
			{ label: '联系人', name: 'contactor', index: 'contactor', width: 80 },
			{ label: '电子邮箱', name: 'email', index: 'email', width: 80 },
			{ label: '电话', name: 'mobile', index: 'mobile', width: 80 },
			{ label: '创建时间', name: 'createTime', index: 'createTime', width: 80 }			
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
    
    vm.getModelList();
});

var vm = new Vue({
	el:'#rrapp',
	data:{
		q:{
			name: null
		},
		showList: true,
		title: null,
		modelList:[],
		spiderProject: {
			status:1,
			type:0,
			modelIdList:[]
		}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.spiderProject = {
					status:1,
					type:0,
					modelIdList:[]
			};
		},
		update: function (event) {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(id)
		},
		saveOrUpdate: function (event) {
			
			var url = vm.spiderProject.id == null ? "spider/spiderproject/save" : "spider/spiderproject/update";
			$.ajax({
				type: "POST",
			    url: baseURL + url,
                contentType: "application/json",
			    data: JSON.stringify(vm.spiderProject),
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
				    url: baseURL + "spider/spiderproject/delete",
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
			$.get(baseURL + "spider/spiderproject/info/"+id, function(r){
                vm.spiderProject = r.spiderProject;
                if(!vm.spiderProject.modelIdList){
                	vm.spiderProject.modelIdList=[];
                }
            });
			this.getProjectList();
		},
		getModelList: function(){
			$.get(baseURL + "spider/spiderproject/selectModels", function(r){
				vm.modelList = r.list;
			});
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
				postData:{'name': vm.q.name},
                page:page
            }).trigger("reloadGrid");
		}
	}
});