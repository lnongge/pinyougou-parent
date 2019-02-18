 //控制层 
app.controller('goodsController' ,function($scope,$controller,itemCatService,goodsService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		if(id!=undefined){
            goodsService.findOne(id).success(
                function(response){
                    $scope.entity= response;
                    //扩展属性---从数据库取出的是json格式的字符串要解析成json对象
                    $scope.entity.tbGoodsDesc.customAttributeItems=
                        JSON.parse(response.tbGoodsDesc.customAttributeItems);
                    //商品图片
                    $scope.entity.tbGoodsDesc.itemImages=JSON.parse(response.tbGoodsDesc.itemImages);
                    //勾选规格
                    $scope.entity.tbGoodsDesc.specificationItems=
                        JSON.parse(response.tbGoodsDesc.specificationItems);
                    //显示sku列表
                    for (var i = 0; i < $scope.entity.itemList.length; i++) {
                        $scope.entity.itemList[i].spec=
                            JSON.parse($scope.entity.itemList[i].spec);

                    }
                }
            );
        }		
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){		
	    if($scope.ids.length==0){
	        alert("请至少选择一个商品");
	        return;
        }
	    var conf=window.confirm("你确定要删除商品?");
	    if(conf){
            //获取选中的复选框			
            goodsService.dele( $scope.ids ).success(
                function(response){
                    if(response.success){
                        $scope.reloadList();//刷新列表
                        $scope.ids=[];
                    }
                }
            );
        }
	}
	//刷新
    $scope.refresh=function () {
        $scope.reloadList();
        $scope.ids=[];
    }
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	//商品状态
    $scope.status=["未审核",'已审核','审核未通过','关闭'];
	//商品分类
    $scope.itemCatList=[];
    $scope.findItemCatList=function () {
        itemCatService.findAll().success(
            function (response) {
                for (var i = 0; i < response.length; i++) {
                   $scope.itemCatList[response[i].id]=response[i].name; 
                }
            }
        );
    }
    //审核
    $scope.updateStatus=function (status) {
        goodsService.updateStatus($scope.ids,status).success(
            function (response) {
                alert(response.message);
                if(response.success){
                    $scope.reloadList();//重新加载
                    $scope.ids=[];//清空集合
                }
            }
        )
    }
    $scope.updateStatus2=function (ids,status) {
        goodsService.updateStatus(ids,status).success(
            function (response) {
                alert(response.message);
                if(response.success){
                    $scope.reloadList();//重新加载
                    
                }
            }
        )
    }
});	
