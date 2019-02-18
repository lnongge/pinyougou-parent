app.controller('baseController',function($scope){
    //分页控件设置
    $scope.paginationConf={
        currentPage:1,//当前页
        totalItems:10,//总记录数
        itemsPerPage:10,//每页记录数
        perPageOption:[10,20,30,40,50],
        onChange:function(){
            $scope.reloadList();
        }
    }
    $scope.reloadList=function(){
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    }
    
    //批量选择要删除的品牌
    $scope.ids=[];
    $scope.deleteSelection=function ($event,id) {
        if($event.target.checked){
            //选中-添加到数组中
            $scope.ids.push(id);
        }else{
            //查找数组中该索引,然后删除
            var index = $scope.ids.indexOf(id);
            $scope.ids.splice(index,1);
        }
    }
    //将json中部分属性提出
    $scope.JsonToString=function (jsonString, key) {
        var json=JSON.parse(jsonString);
        var values="";
        for(var i=0;i<json.length;i++){
            if(i>0){values+=",";}
            values+=json[i][key];
        }
        return values;
    }
});