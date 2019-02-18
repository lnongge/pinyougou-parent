app.service('brandService',function ($http) {
    this.search=function (pageNum,pageSize,searchEntity) {
        return $http.post('../brand/search.do?pageNum='+pageNum+'&pageSize='+pageSize,searchEntity);
    }
    this.save=function (entity) {
        return $http.post('../brand/save.do',entity);
    }
    this.findOne=function (id) {
        return  $http.get('../brand/findOne.do?id='+id);
    }
    this.del=function (ids) {
        return $http.get('../brand/delete.do?ids='+ids);
    }
    this.linkBrand=function () {
        return $http.get('../brand/findLinkedBrand.do');
    }

});