 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,uploadService,itemCatService,typeTemplateService,goodsService){	
	
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
	$scope.findOne=function(){
        var id=$location.search()['id'];
        if(id==null||id==undefined){//增加时,什么都不做
            return;
        }
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;		
				//向富文本编辑器添加商品介绍
                editor.html(response.tbGoodsDesc.introduction);
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
	//根据规格名称和规格选项返回是否勾选
    $scope.checkAttrValue=function (specName,specOption) {
        var specList=$scope.entity.tbGoodsDesc.specificationItems;
        var object=$scope.searchObjectByKey(specList,'attributeName',specName);
        if(object!=null&&object.attributeValue.indexOf(specOption)>0){
            return true
        }else{
            return false;
        }
    }
	
	//保存 
	$scope.save=function(){	
	    $scope.entity.tbGoodsDesc.introduction=editor.html();
		var serviceObject;//服务层对象
        var isUpdate=false;
		if($scope.entity.tbGoods.id!=null){//如果有ID
            isUpdate=true;
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity);//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
                    alert(response.message);
		        	$scope.reloadList();//重新加载
                    $scope.entity={};
                    editor.html('');//清空服务本编辑器
                    if(isUpdate){
                        location.href="goods.html";
                    }
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
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
    //上传图片
    $scope.uploadFile=function () {
        uploadService.uploadFile().success(
            function (response) {
                if(response.success){
                    //设置图片路径
                    $scope.imageEntity.url=response.message;
                }else{
                    alert(response.message);
                }
            }
        ).error(function () {
            alert("上传出错了");
        });
    };
    //图片列表
    $scope.entity={tbGoods:{},tbGoodsDesc:{itemImages:[]}};//定义页面实体结构
    $scope.addImageEntity=function () {
        $scope.entity.tbGoodsDesc.itemImages.push($scope.imageEntity);
    }
    //删除图片
    $scope.removeImageEntity=function (index) {
        $scope.entity.tbGoodsDesc.itemImages.splice(index,1);
    }
    
    //商品1级分类
    $scope.selectItemCat1List=function () {
        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.itemCat1List=response;
            }
        )
    }
    //商品2级分类
    $scope.$watch('entity.tbGoods.category1Id',function (newValue,oldValue) {
        if(newValue!=undefined){
            itemCatService.findByParentId(newValue).success(
                function (response) {
                    $scope.itemCat2List=response;
                }
            );
        }
    }); 
    //商品3级分类
    $scope.$watch('entity.tbGoods.category2Id',function (newValue,oldValue) {
       if(newValue!=undefined){
           itemCatService.findByParentId(newValue).success(
               function (response) {
                   $scope.itemCat3List=response;
               }
           );
       }
    }); 
    //模板id
    $scope.$watch('entity.tbGoods.category3Id',function (newValue,oldValue) {
        if(newValue!=undefined){
            itemCatService.findOne(newValue).success(
                function (response) {
                    $scope.entity.tbGoods.typeTemplateId=response.typeId;
                }
            );
        }
    });
    //根据模板id找该分类的模板
    $scope.$watch('entity.tbGoods.typeTemplateId',function (newValue, oldValue) {
       if(newValue!=undefined){
           //品牌列表
           typeTemplateService.findOne(newValue).success(
               function (response) {
                   $scope.brandList=JSON.parse(response.brandIds);//品牌列表
                   if($location.search()['id']==null){
                       $scope.entity.tbGoodsDesc.customAttributeItems=
                           JSON.parse(response.customAttributeItems);
                   }
                   
               }
           );
           //查询规格和规格选项
           typeTemplateService.findSpecList(newValue).success(
               function (response) {
                   $scope.specList=response
               }
           );
       }
    })
    //保存规格选项
    //抽取的方法:从集合中查找某一元素
    $scope.entity={tbGoodsDesc:{itemImages:[],specificationItems:[]}};
    //规格保存的数据类型如下:
    //entity.tbGoodsDesc.specificationItems=[{"attributeName":"网络","attributeValue":["电信2G","联通3G"]}]
    $scope.updateSpecAttribute=function ($event,name,value) {
        var obj=$scope.searchObjectByKey($scope.entity.tbGoodsDesc.specificationItems,'attributeName',name);
        if(obj!=null){//集合中有
            //判断是勾选还是取消勾选
            if($event.target.checked){//勾选
                obj.attributeValue.push(value);
            }else{//取消勾选
                //从attributeValue中移除value:1.先查出value的索引 indexOf()2.再删除splice()
                obj.attributeValue.splice(obj.attributeValue.indexOf(value),1);
            }
            //如果选项都取消了,将整条记录移除
            if(obj.attributeValue.length==0){
                $scope.entity.tbGoodsDesc.specificationItems.splice(
                  $scope.entity.tbGoodsDesc.specificationItems.indexOf(obj),1  
                );
            }
            
        }else{//集合中没有--直接添加即可
            $scope.entity.tbGoodsDesc.specificationItems.push(
                {"attributeName":name,"attributeValue":[value]}  
            );
        }
    }
    //搜索集合中是否有某个元素
    //entity.tbGoodsDesc.specificationItems=[{"attributeName":"网络","attributeValue":["电信2G","联通3G"]},{"attributeName":"机身内存",...}]
    //$scope.searchObjectByKey($scope.entity.tbGoodsDesc.specificationItems,'attributeName',name); //name:网络
    $scope.searchObjectByKey=function (list,key,value) {
        for (var i = 0; i < list.length; i++) {
           if(list[i][key]==value) {
               return list[i];
           }
        }
        return null;
    }
    //生成sku列表
   $scope.createItemList=function () {
       $scope.entity.itemList=[{spec:{},price:0,num:9999,status:'1',isDefault:'1'}];//定义sku列表
       var specList=$scope.entity.tbGoodsDesc.specificationItems;//规格列表
       //遍历
       for (var i = 0; i < specList.length; i++) {
           $scope.entity.itemList=addColumn($scope.entity.itemList,specList[i].attributeName,specList[i].attributeValue);
       }
   }
    //定义方法:往集合list中的每个元素(深克隆得到新元素,每个新元素上)添加属性columnName和属性值(columnValues中的一个)
    addColumn=function (list, columnName, columnValues) {
        var newList=[];
        for (var i = 0; i < list.length; i++) {
            var oldRow=list[i];
            for (var j = 0; j < columnValues.length; j++) {
                var newRow=JSON.parse(JSON.stringify(oldRow));// 深克隆
                newRow.spec[columnName]=columnValues[j];//每一行同时添加属性名和属性值
                newList.push(newRow);
            }

        }
        return newList;
    }
    
    //显示商品状态
    $scope.status=['未审核','已审核','审核未通过','关闭'];
    //显示商品分类
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
    //上/下架
    $scope.pushOrPull=function (status) {
        if($scope.ids.length==0){
            alert("请至少选择一个商品");
            return;
        }
        goodsService.pushOrPull($scope.ids,status).success(
            function (response) {
                alert(response.message);
                if(response.success){
                    $scope.reloadList();
                    $scope.ids=[];
                }
            }
        )
    }
});	
