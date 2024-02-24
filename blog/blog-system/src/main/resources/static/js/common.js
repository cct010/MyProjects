//获取当前登录者的文章数量,用户名,头像
function showData(){
    jQuery.ajax({
        url: "/blog/user/showData",
        type: "post",
        success: function(body){
            if(body!=null && body.code==200){
                jQuery("#username").text(body.data.username);
                jQuery("#artTotal").text(body.data.artTotal);
                jQuery("#headimg")[0].src = body.data.photo;
                // jQuery("#headimg").src(body.data.photo); 关于照片需修改
            }
        }
    });
}

//获取当前url参数的公共方法
function getUrlValue(key){
    var params = location.search;
    if(params.length >1){
        params = location.search.substring(1); //去掉里面的?
        var paramArr = params.split("&");
        for(var i=0;i<paramArr.length;i++){
            var kv = paramArr[i].split("=");
            if(kv[0] == key){
                //是要查找的参数
                return kv[1];
            }
        }
    }
    return "";
}

 //注销
 function logout(){
    if(confirm("确认注销?")){
        jQuery.ajax({
            url: "/blog/user/logout",
            type: "post",
            success: function(body){
                if(body!=null && body.code==200){
                    location.href = "/blog/login.html";
                }
            }
        });
    }

}

// //给所有ajax的url加上前缀 /manage
// jQuery(document).ajaxSend(function(event, xhr, options){
//     console.log( options.url);
//     options.url = "/blog" + options.url;
// });

