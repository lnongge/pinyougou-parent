app.controller("contentController",function ($scope,contentService) {
   $scope.contentList=[];//广告的集合
   $scope.findByCategoryId=function (categoryId) {
       contentService.findByCategoryId(categoryId).success(
         function (response) {
             $scope.contentList[categoryId]=response;
         }  
       );
   } 
   //搜索
    $scope.search=function () {
        location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }
});