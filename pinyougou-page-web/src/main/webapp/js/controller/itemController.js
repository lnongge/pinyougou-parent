app.controller('itemController',function($scope,$location){
    //商品数据增/减
   $scope.addNum=function(x){
	   $scope.num=$scope.num+x;
	   if($scope.num<1){
		   $scope.num=1;
	   }
   }
   //规格选择
    $scope.specificationItems=[];//数组,用于包存用户选择的规格
   $scope.selectSpecification=function (specName, specValue) {
       $scope.specificationItems[specName]=specValue;
       searchSku();//读取sku
   }
   //判断是否选择规格
    $scope.isSelected=function (specName, specValue) {
        if($scope.specificationItems[specName]==specValue){
            return true;
        }else{
            return false;
        }
    }
    //加载默认的sku信息
    $scope.loadSku=function () {
       var skuId=parseInt($location.search()['skuId']);
       if(skuId==null){
           $scope.sku=skuList[0];
       }
       for(var i=0;i<skuList.length;i++){
           if(skuList[i].id==skuId){
               $scope.sku=skuList[i];
               break;
           }
       }
        $scope.specificationItems=JSON.parse(JSON.stringify($scope.sku.spec));
    }
   //判断2个map(或对象)是否相等的方法
   matchObject=function (map1, map2) {
       for(var k in map1){
           if(map1[k]!=map2[k]){
               return false;
           }
       }
       for(var k in map2){
           if(map2[k]!=map1[k]){
               return false;
           }
       }
       return true;
   }
   //在sku列表中查询当前用户选择的sku
    searchSku=function () {
        for(var i=0;i<skuList.length;i++){
            if(matchObject(skuList[i].spec,$scope.specificationItems)){
                $scope.sku=skuList[i];//找到匹配了
                return;
            }
        }
        //如果没有匹配的给一个默认的
        $scope.sku={id:0,title:'暂时没货,敬请期待!',price:0}
    }
    //添加购物车的方法(模拟,为后面准备)
    $scope.addToCart=function () {
        alert("添加成功skuid为:"+$scope.sku.id+",数量:"+$scope.num);
    }
});