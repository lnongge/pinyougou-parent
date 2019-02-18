 //控制层 
app.controller('itemCatController' ,function($scope,$controller ,typeTemplateService ,itemCatService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;
                $scope.entity.typeId=response.typeId;//select2默认选中
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
            $scope.entity.parentId=$scope.parentId;//设置父id
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
                    alert(response.message);
		        	$scope.findByParentId($scope.parentId);//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.ids ).success(
			function(response){
				if(response.success){
				//	$scope.reloadList();//刷新列表--(这是有分页的情况)
                    $scope.grade=1;
                    $scope.findByParentId(0);
					$scope.ids=[];
				}else{
				    alert(response.message);
                }						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    //记录上级id--新增商品分类时需要
    $scope.parentId=0;
    //商品分类
    $scope.findByParentId=function (pid) {
        $scope.parentId=pid;//查询时记录父id
        itemCatService.findByParentId(pid).success(
            function (response) {
                $scope.list=response;
            }
        );
    }
    //面包屑
    $scope.grade=1;
	$scope.setGrade=function (grad) {
        $scope.grade=grad;
    }
	$scope.selectList=function (p_entity) {
        if($scope.grade==1){
            $scope.entity_1=null;
            $scope.entity_2=null;
        }
        if($scope.grade==2){
            $scope.entity_1=p_entity;
            $scope.entity_2=null;
        }
        if($scope.grade==3){
            $scope.entity_2=p_entity;
        }
        //查询此级下拉列表
        $scope.findByParentId(p_entity.id);
    }
    //修改分类
    $scope.typeTemplateList={data:[]};
    $scope.findTypeTemplate=function () {
        typeTemplateService.findAllInSelect2().success(
            function (response) {
                $scope.typeTemplateList.data=response;
            }
        );
    }
});	
