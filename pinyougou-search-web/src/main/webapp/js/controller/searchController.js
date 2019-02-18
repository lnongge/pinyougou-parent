app.controller("searchController",function ($scope,$location,searchService) {
   //搜索
    $scope.search=function () {
        $scope.searchMap.pageNo=parseInt($scope.searchMap.pageNo);//确保是int类型
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap=response;
                //构建分页工具条
                buildPageLabel();
            }
        );
    }
    
    //搜索对象---即搜索条件
    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':40,'sortField':'','sort':''}
    //添加搜索项
    $scope.addSearchItem=function (key, value) {
        if(key=='category'||key=='brand' || key=='price'){//第一个种情况:如果点击的是分类或品牌
            $scope.searchMap[key]=value;
        }else{//第二种情况:点击的是规格
            $scope.searchMap.spec[key]=value;
        }
        //添加搜索项后要重新查询
        $scope.search();
    }
    //撤销搜索项
    $scope.removeSearchItem=function (key) {
        if(key=='category'||key=='brand' || key=='price'){//第一个种情况:如果点击的是分类或品牌
            $scope.searchMap[key]='';
        }else{//第二种情况:点击的是规格
            delete $scope.searchMap.spec[key];
        }
        //移除搜索项后要重新查询
        $scope.search();
    }
    //构建分页工具条中的页码
    buildPageLabel=function () {
        $scope.pageLabel=[];//数组存放分页工具栏需要显示的页码
        var maxPage=$scope.resultMap.totalPages;//总页数
        var firstPage=1;
        var lastPage=maxPage;
        $scope.frontDot=true;//前面有点(即省略号)
        $scope.rearDot=true;//后面有点
        if(maxPage>5){//总页数>5
            if($scope.searchMap.pageNo<=3){
                lastPage=5;//显示前5页(头)
                $scope.frontDot=false;//前面无点
            }else if($scope.searchMap.pageNo>maxPage-2){
                firstPage=maxPage-4;//显示后5页(尾)
                $scope.frontDot=false;//后面无点
            }else{//中间
                firstPage=$scope.searchMap.pageNo-2;
                lastPage=$scope.searchMap.pageNo+2;
            }
        }else{//显示的页码刚好5页
            $scope.frontDot=false;//前面无点
            $scope.rearDot=false;//后面无点
        }
        //把页面装到数组中
        for(var i=firstPage;i<=lastPage;i++){
            $scope.pageLabel.push(i);
        }
        
    }
    //执行分页查询的方法
    $scope.queryByPage=function (pageNo) {
        if(pageNo<1 ||pageNo>$scope.resultMap.totalPages){//页码不合理
            return;
        }
        $scope.searchMap.pageNo=pageNo;//更新当前页
        $scope.search();//查询
    }
    //排序查询
    $scope.sortSearch=function (sortField, sort) {
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sort=sort;
        $scope.search();
    }
    //判断搜索关键字是否是品牌
    $scope.keywordsIsBrand=function () {
        for(var i=0;i<$scope.resultMap.brandList.length;i++){
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
                return true;
            }
        }
        return false;
    }
    //接收主页传来的关键字,并自动搜索
    $scope.loadKeywords=function () {
        $scope.searchMap.keywords=$location.search()['keywords'];//接收参数
        $scope.search();//查询
    }
});