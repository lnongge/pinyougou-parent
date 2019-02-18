app.controller('brandController',function($scope,$controller,brandService){
    //继承父控制器
    $controller('baseController',{$scope:$scope});
    //搜索方法(带分页)
    $scope.searchEntity={};//初始化
    $scope.search=function(pageNum,pageSize){
        brandService.search(pageNum,pageSize,$scope.searchEntity).success(
            function(response){
                $scope.list=response.rows;//分页数据
                $scope.paginationConf.totalItems=response.total;//总记录数
            }
        );
    }
    //添加和修改的方法
    $scope.save=function () {
        brandService.save($scope.entity).success(
            function (response) {
                if(response.success){
                    $scope.reloadList();
                }else{
                    alert(response.message);
                }
            }
        );
    }
    //修改前查找
    $scope.findOne=function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity=response;
            }
        );
    }
   
    //执行删除
    $scope.del=function () {
        brandService.del($scope.ids).success(
            function (response) {
                if(response.success){
                    $scope.reloadList();
                }else{
                    alert(response.message);
                }
            }
        )
    }
});