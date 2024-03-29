 //控制层 
app.controller('typeTemplateController' ,function($scope,$controller,brandService,specificationService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		typeTemplateService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		typeTemplateService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		typeTemplateService.findOne(id).success(
			function(response){
				$scope.entity= response;	
                $scope.entity.specIds=JSON.parse($scope.entity.specIds);
                $scope.entity.brandIds=JSON.parse($scope.entity.brandIds);
                $scope.entity.customAttributeItems=JSON.parse( $scope.entity.customAttributeItems);
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=typeTemplateService.update( $scope.entity ); //修改  
		}else{
			serviceObject=typeTemplateService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		typeTemplateService.dele( $scope.ids ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.ids=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		typeTemplateService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    //select2 关联品牌
	$scope.brandList={data:[]};
	$scope.linkBrand=function () {
        brandService.linkBrand().success(
            function (response) {
                $scope.brandList={data:response}
            }
        );
    }
    //select2 关联规格
    $scope.specificationList={data:[]};
	$scope.linkSpecification=function () {
        specificationService.linkSpecification().success(
            function (response) {
                $scope.specificationList={data:response}
            }
        );
    }
    //扩展属性
    $scope.addTableRow=function () {
        $scope.entity.customAttributeItems.push({});
    }
    $scope.deleTableRow=function (index) {
        $scope.entity.customAttributeItems.splice(index,1);
    }
});	
