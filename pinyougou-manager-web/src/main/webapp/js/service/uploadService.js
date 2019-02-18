app.service('uploadService',function ($http) {
    this.uploadFile=function () {
        //获得要上传文件
        var formDate=new FormData();
        formDate.append("file",file.files[0]);
        //上传
       return $http({
            url:'../upload/pic.do',
            method:'POST',
            data:formDate,
            headers:{'Content-Type':undefined},
            transformRequest:angular.identity
        });
    }
})